package Client;

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

    private static final int STATE_LOGIN = 0;
    private static final int STATE_CHAT = 1;
    private static final int STATE_FRIENDS = 2;

    private static final String IP_GLOBAL = "2.249.12.98";
    private static final String IP_LOCAL = "192.168.1.70";
    private static final int PORT = 4001;

    private ClientModel client;

    private ScheduledExecutorService outputThread;
    private int currentState;
    private GUIFX guifx;


    public ClientController(GUIFX guifx) {
        client = new ClientModel();
        this.guifx = guifx;

        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);

        initLogin();
    }

    private void changeState(int newState) {
        if (currentState == STATE_CHAT && newState == STATE_LOGIN) {
            //outputThread.shutdown();
        }
        currentState = newState;
    }

    private void initLogin() {
        changeState(STATE_LOGIN);
        System.out.println("state login");

        guifx.showLogin();
        addSubmitListeners();
    }

    private void initChat(String username) {
        changeState(STATE_CHAT);

        guifx.showChat(username);
        guifx.setChatCloseEvent(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                client.setTarget(null);
                client.setChatPartner(null);
            }
        });

        addSubmitListeners();
        client.getOldMessages(username);
    }

    private void initFriends() {
       Platform.runLater(() -> guifx.showFriendlist(client.getUser().getFullName()));
    }

    private void startChatWith(String username) {
        initChat(username);

        client.setTarget(username);
        client.setChatPartner(username);

        Platform.runLater(() -> {

            if (client.getChatLogHandler().hasMessages(username)) {
                guifx.addTextToChat("Showing older messages from: " + username + "\n");
                Queue<PduHandler.PDU_MESSAGE> unreadMessages = client.getChatLogHandler().getFullChatLog(username);

                while (!unreadMessages.isEmpty()) {
                    PduHandler.PDU_MESSAGE msgPdu = unreadMessages.poll();

                    guifx.addTextToChat(msgPdu.sender + ": ");
                    guifx.addTextToChat(msgPdu.message + "\n");
                }
            }
        });

    }

    private void addSubmitListeners() {
        switch (currentState) {
            case STATE_LOGIN: {
                guifx.getUserTextField().setOnAction(submitLoginEventHandler());
                guifx.getUserSubmitButton().setOnAction(submitLoginEventHandler());
                break;
            }
            case STATE_FRIENDS: {

                break;
            }
            case STATE_CHAT: {
                guifx.getChatField().setOnAction(submitChatEventHandler());
                guifx.getChatSubmitButton().setOnAction(submitChatEventHandler());
                break;
            }
        }
    }

    private void handleLoginSubmit() {
        guifx.clearError();

        //if has not yet logged in

        if (client.getUser() == null) {
            String textAreaContent = guifx.getUserTextField().getText();
            if (textAreaContent.length() == 0) {
                guifx.showUsernameError();
                return;
            }

            Platform.runLater(() -> guifx.showConnecting());

            new Thread(() -> {
                guifx.lockLoginScreen();
                if (connect(textAreaContent)) {
                } else {
                    Platform.runLater(() -> guifx.showConnectionError());
                }
                guifx.unlockLoginScreen();
            }).start();
        }
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

    private boolean connect(String username) {
        return (client.connectToChatServer(username, "localhost", IP_LOCAL, PORT));
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

                if (responsePDU.status == 1) {
                    initFriends();
                    client.login();
                } else {
                    guifx.showLoginError(GUIFX.WRONG_CREDENTIALS);
                    client.killUser();
                    client.shutDownConnection();
                    //client = new Client.ClientModel();
                  //  outputThread.shutdown();
                }
                break;
            }
        }
    }

    private void updateFriendlist(ArrayList<String> userlist) {

        Platform.runLater(() -> guifx.clearFriendlist());
        for (String friend : userlist) {
            if (!friend.equals(client.getUser().getFullName())) {

                int unreadMessages = client.getChatLogHandler().getUnreadMessages(friend);

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
            } else {
            }
        }
    };
}
