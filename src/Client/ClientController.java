package Client;

import ChatLog.ChatLogHandler;
import FileHandler.FileHandler;
import GUI.GUIFX;
import Messaging.PDU;
import Messaging.PduHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController {

    private static final String IP_GLOBAL = "2.249.12.98";
    private static final String IP_LOCAL = "192.168.1.70";
    private static final int PORT = 4001;

    private ClientModel client;

    private ScheduledExecutorService outputThread;
    private int currentState;
    private GUIFX guifx;

    private ArrayList<String> currentFriendlist;

    public ClientController(GUIFX guifx) {
        client = new ClientModel();
        this.guifx = guifx;

        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);

        initLogin();
    }


    private void initLogin() {
        guifx.showLogin();

        guifx.getUserTextField().setOnAction(submitLoginEventHandler());
        guifx.getUserSubmitButton().setOnAction(submitLoginEventHandler());
    }

    private void initChat(String username) {
        guifx.showChat(username);
        guifx.setChatCloseEvent(closeChatEventHandler());

        guifx.getChatField().setOnAction(submitChatEventHandler());
        guifx.getChatSubmitButton().setOnAction(submitChatEventHandler());
        client.getOldMessages(username);
    }



    private void initFriends() {
       Platform.runLater(() -> guifx.showFriendlist(client.getUser().getFullName()));
    }

    private void startChatWith(String username) {
        initChat(username);

        //client.setTarget(username);
        client.setChatPartner(username);

        Platform.runLater(() -> {

            if (client.getChatLogHandler().hasMessages(username)) {
                guifx.addTextToChat("Showing older messages from: " + username + "\n");

                //TODO: make this only add unread messages to chat instead of all of them (maybe)
                Queue<PduHandler.PDU_MESSAGE> unreadMessages = client.getChatLogHandler().getFullChatLog(username);

                while (!unreadMessages.isEmpty()) {
                    PduHandler.PDU_MESSAGE msgPdu = unreadMessages.poll();

                    guifx.addTextToChat(msgPdu.sender + ": ");
                    guifx.addTextToChat(msgPdu.message + "\n");
                }
            }
        });

    }

    private void handleLoginSubmit() {
        guifx.clearError();

        String enteredUsername = guifx.getUserTextField().getText();
        if (enteredUsername.length() == 0) {
            guifx.showUsernameError();
            return;
        }

        Platform.runLater(() -> guifx.showConnecting());

        new Thread(() -> {
            guifx.lockLoginScreen();
            if (connect(enteredUsername)) {
            } else {
                Platform.runLater(() -> guifx.showConnectionError());
                return;
            }
            guifx.unlockLoginScreen();
        }).start();

    }

    private void handleChatSubmit() {
        String chatAreaContent = guifx.getChatField().getText();

        guifx.getChatField().requestFocus();
        if (chatAreaContent.isEmpty()) {
            return;
        }
        sendMessage(chatAreaContent, client.getChatPartner());
    }

    private EventHandler<javafx.event.ActionEvent> submitLoginEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleLoginSubmit();
            }
        };
    }

    private EventHandler<javafx.event.ActionEvent> submitChatEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleChatSubmit();
            }
        };
    }

    private EventHandler<WindowEvent> closeChatEventHandler() {
        return new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //client.setTarget(null);
                client.setChatPartner(null);
            }
        };
    }

    private boolean connect(String username) {
        return (client.connectToChatServer(username, "localhost", PORT));
    }

    private void sendMessage(String message, String target) {

        client.sendMessage(message, target);

        guifx.addTextToChat(client.getUser().getFullName() + ": " + message + "\n");
        guifx.clearTextField();
    }

    private void handleMessage(PduHandler.PDU_MESSAGE messagePDU) {

        String message = messagePDU.message;
        String sender = messagePDU.sender;

        FileHandler.savePDUToFile(messagePDU, client.getUser().getFullName());


        Platform.runLater(() -> {

            if (client.getChatPartner() != null) {
                if (client.getChatPartner().equals(sender)) {
                    guifx.addTextToChat(sender + ": ");
                    guifx.addTextToChat(message + "\n");
                } else {
                    System.out.println("received msg from wrong person");
                }
            } else {
                if (sender.equals(" ")) {
                } else {
                    //client.getChatLogHandler().addToLogs(messagePDU);
                }
            }
        });
    }

    private void handlePDU(PDU pdu) {

        switch (pdu.type) {
            case PduHandler.MESSAGE_PDU: {
                PduHandler.PDU_MESSAGE msgPdu = (PduHandler.PDU_MESSAGE)pdu;

                System.out.println("received msg pdu: " + msgPdu.toString());
                handleMessage(msgPdu);

                break;
            }

            case PduHandler.CHATINFO_PDU: {
                PduHandler.PDU_CHATINFO chatInfoPdu = (PduHandler.PDU_CHATINFO)pdu;

                if (chatInfoPdu.chatPartner.equals("_null")) {
                 //   gui.setChatPartnerLabel(null);
                    client.setChatPartner(null);
                } else {
                    client.setChatPartner(chatInfoPdu.chatPartner);
                   // gui.setChatPartnerLabel(chatInfoPdu.chatPartner);
                }
                break;
            }

            case PduHandler.USERLIST_PDU: {
                PduHandler.PDU_USERLIST userlistPdu = (PduHandler.PDU_USERLIST)pdu;

                try {

                    updateChat(userlistPdu.usernames);

                    if (userlistPdu.usernames.size() > 1) {
                        updateFriendlist(userlistPdu.usernames);
                    } else {
                        Platform.runLater(() -> {
                            guifx.clearFriendlist();
                            guifx.sendEmptyFriendlist();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case PduHandler.LOGIN_RESPONSE_PDU: {
                PduHandler.PDU_LOGIN_RESPONSE responsePDU = (PduHandler.PDU_LOGIN_RESPONSE)pdu;

                if (responsePDU.status == GUIFX.LOGIN_SUCCESS) {
                    initFriends();
                    client.login();
                } else {
                    guifx.showLoginError(GUIFX.WRONG_CREDENTIALS);
                    client.killUser();
                    client.shutDownConnection();

                    client = new ClientModel();
                    //client = new Client.ClientModel();
                  //  outputThread.shutdown();
                }
                break;
            }
        }
    }

    private void updateChat(ArrayList<String> userlist) {
        Platform.runLater(() -> {
            if (!userlist.contains(client.getChatPartner())) {
                guifx.chatSetOffline(client.getChatPartner());
            } else {
                if (!guifx.getPartnerOnline()) {
                    guifx.chatSetOnline(client.getChatPartner());
                }
            }
        });
    }
    private void updateFriendlist(ArrayList<String> userlist) {

        boolean listHasChanged = false;

        if (currentFriendlist == null) {
            currentFriendlist = new ArrayList<>(userlist);
            listHasChanged = true;
        } else {
            if (currentFriendlist.equals(userlist)) {
                //no update
                return;
            }
        }
        //client.updateChatLogs(userlist);

        Platform.runLater(() -> guifx.clearFriendlist());
        for (String friend : userlist) {
            if (!friend.equals(client.getUser().getFullName())) {

                int unreadMessages = client.getChatLogHandler().getUnreadMessages(friend);
                System.out.println("Unread from " + friend + ": " + unreadMessages);
                listHasChanged = true;

                if (listHasChanged) {
                    Platform.runLater(() -> {

                        boolean isCurrentChatPartner = false;

                        if (client.getChatPartner() != null) {
                            if (client.getChatPartner().equals(friend)) {
                                isCurrentChatPartner = true;
                            }
                        }

                        guifx.addToFriendList(friend, unreadMessages, isCurrentChatPartner).setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                startChatWith(friend);
                            }
                        });
                    });
                }
            }
        }
    }

    public void quit() {
        System.out.println("Quit!");
        client.sendIsLeaving();
        outputThread.shutdown();
        client.quit();
        guifx = null;
        client = null;

        System.exit(1);
    }
    private Runnable getOutput = new Runnable() {

        @Override
        public void run() {

            if (client.getIncomingMessageQueue() != null) {
                if (!client.getIncomingMessageQueue().isEmpty()) {
                    handlePDU(client.getIncomingMessageQueue().poll());
                }
            }
        }
    };
}
