import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import sun.awt.PlatformFont;

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

    private GUIFX guifx;
    private ScheduledExecutorService outputThread;
    private int currentState;


    public ClientController(GUIFX guifx) {
        client = new ClientModel();
        this.guifx = guifx;

        initLogin();
    }

    private void changeState(int newState) {
        if (currentState == STATE_CHAT && newState == STATE_LOGIN) {
            outputThread.shutdown();
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
        addSubmitListeners();
    }

    private void initFriends() {
        guifx.showFriendlist(client.getUser().getFullName());
    }

    private void startChatWith(String username) {

        initChat(username);
        //sendMessage("/connect " + username);
        //Platform.runLater(() -> guifx.openChatWith(username));
        ClientMessageHandler.sendSetTarget(client, username);
        client.setChatPartner(username);

        Platform.runLater(() -> {

            if (client.getChatLogHandler().hasMessages(username)) {
                guifx.addTextToChat("Showing older messages from: " + username + "\n");
            }

                while (client.getChatLogHandler().hasMessages(username)) {
                PduHandler.PDU_MESSAGE msgPdu = client.getChatLogHandler().getFirstMessageFromLog(username);

                guifx.addTextToChat(msgPdu.sender + ": ");
                guifx.addTextToChat(msgPdu.message + "\n");
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

            guifx.showConnecting();

            if (connect(textAreaContent)) {
               // initChat();
                //guifx.hideLogin();
                initFriends();

                outputThread = Executors.newScheduledThreadPool(1);
                outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);

            } else {
                guifx.showConnectionError();
            }
        }
    }

    private void handleChatSubmit() {
        String chatAreaContent = guifx.getChatField().getText();

        guifx.getChatField().requestFocus();
        if (chatAreaContent.isEmpty()) {
            return;
        }
        sendMessage(chatAreaContent);
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
        return (client.connectToChatServer(username, IP_LOCAL, IP_LOCAL, PORT));
    }

    private void sendMessage(String message) {

        ClientMessageHandler.prepareAndSend(client, message);

        guifx.addTextToChat(client.getUser().getFullName() + ": " + message + "\n");
        guifx.clearTextField();
    }

    private void handleMessage(PduHandler.PDU_MESSAGE messagePDU) {

        String message = messagePDU.message;
        String sender = messagePDU.sender;

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
                    //guifx.addTextToChat(message + "\n");
                } else {
                    client.getChatLogHandler().addToLogs(messagePDU);
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
        outputThread.shutdown();
        client.quit();
        guifx = null;
        client = null;
    }
    private Runnable getOutput = new Runnable() {

        Queue<PDU> messageQueue;
        @Override
        public void run() {

            if (messageQueue == null) {
                if (client.getIncomingMessageQueue() != null) {
                    messageQueue = client.getIncomingMessageQueue();
                }
            } else {
                if (!messageQueue.isEmpty()) {
                    handlePDU(messageQueue.poll());
                }
            }
        }
    };
}
