package Client;

import ChatLog.ChatLogHandler;
import FileHandler.FileHandler;
import GUI.GUIFX;
import Messaging.ErrorMessage;
import Messaging.InputHandler;
import Messaging.PDU;
import Messaging.PduHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.xml.sax.ErrorHandler;

import javax.lang.model.type.ErrorType;
import java.io.IOException;
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
    private boolean useFileHandler = true;

    public ClientController(GUIFX guifx) {
        client = new ClientModel();
        this.guifx = guifx;

        startOutputThread();
        initLogin();
    }

    private void startOutputThread() {
        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
    }


    private void initLogin() {
        guifx.showLogin();

        guifx.getUserTextField().setOnAction(submitLoginEventHandler());
        guifx.getUserPasswordField().setOnAction(submitLoginEventHandler());
        guifx.getUserSubmitButton().setOnAction(submitLoginEventHandler());
        guifx.getRegisterLink().setOnAction(registerLinkEventHandler());
    }

    private void initRegister() {

        guifx.showRegister();
        guifx.getCreateUsernameField().setOnAction(submitRegisterEventHandler());
        guifx.getCreateEmailField().setOnAction(submitRegisterEventHandler());
        guifx.getCreatePasswordField().setOnAction(submitRegisterEventHandler());
        guifx.getCreatePasswordFieldRepeat().setOnAction(submitRegisterEventHandler());

        guifx.getCreateUserSubmitButton().setOnAction(submitRegisterEventHandler());
        guifx.getCreateBackButton().setOnAction(createUserBackEventHandler());
    }

    private void initChat(String username) {
        guifx.showChat(username);
        guifx.setChatCloseEvent(closeChatEventHandler());

        guifx.getChatField().setOnAction(submitChatEventHandler());
        guifx.getChatSendImageButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //sendFile("./resources/hund.jpg", client.getChatPartner());
            }
        });
        guifx.getChatSubmitButton().setOnAction(submitChatEventHandler());

        if (useFileHandler) {
            client.loadMessages(username);
        }
        Platform.runLater(() -> guifx.resetName(username));
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
        String enteredPassword = guifx.getUserPasswordField().getText();

        ErrorMessage usernameStatus = InputHandler.checkUsername(enteredUsername);
        if (usernameStatus != ErrorMessage.INPUT_OK) {
            guifx.showUsernameError(usernameStatus.getErrorMessage());
            return;
        }

        Platform.runLater(() -> guifx.showConnecting());

        new Thread(() -> {
            guifx.lockLoginScreen();
            if (connect(enteredUsername, enteredPassword)) {
            } else {
                Platform.runLater(() -> guifx.showConnectionError());
                guifx.unlockLoginScreen();
                return;
            }
            guifx.unlockLoginScreen();
        }).start();
    }

    private void handleRegisterSubmit() {
        String enteredUsername = guifx.getCreateUsernameField().getText();
        String enteredEmail = guifx.getCreateEmailField().getText();
        String enteredPassword = guifx.getCreatePasswordField().getText();
        String enteredPasswordRepeat = guifx.getCreatePasswordFieldRepeat().getText();

        if (enteredUsername.length() == 0) {
            guifx.showRegisterError(ErrorMessage.CR_USERNAME_EMPTY);
            return;
        }
        if (enteredEmail.length() == 0) {
            guifx.showRegisterError(ErrorMessage.CR_INVALID_MAIL);
            return;
        }
            if (enteredPassword.length() == 0 || enteredPasswordRepeat.length() == 0) {
            guifx.showRegisterError(ErrorMessage.CR_PASSWORD_INVALID);
            return;
        }
        if (!enteredPassword.equals(enteredPasswordRepeat)) {
            guifx.showRegisterError(ErrorMessage.CR_PASSWORD_DOES_NOT_MATCH);
            return;
        }

        Platform.runLater(() -> guifx.showConnecting());

        new Thread(() -> {
            //guifx.lockLoginScreen();
            if (connectNew(enteredUsername, enteredEmail, enteredPassword)) {
            } else {
                Platform.runLater(() -> guifx.showConnectionError());
                return;
            }
            //guifx.unlockLoginScreen();
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

    private EventHandler<javafx.event.ActionEvent> submitRegisterEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleRegisterSubmit();
            }
        };
    }

    private EventHandler<javafx.event.ActionEvent> createUserBackEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initLogin();
            }
        };
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

    private EventHandler<javafx.event.ActionEvent> registerLinkEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initRegister();
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

    private boolean connect(String username, String password) {
        return (client.connectToChatServer(username, password, "localhost", PORT));
    }

    private boolean connectNew(String username, String email, String password) {
        return (client.connectToChatServer(username, email, password, "localhost", PORT));
    }

    private void sendMessage(String message, String target) {

        client.sendMessage(message, target);

        guifx.addTextToChat(client.getUser().getFullName() + ": " + message + "\n");
        guifx.clearTextField();
    }

    private void sendFile(String filename, String target) {
       // client.sendFile(filename, target);
    }
    private void handleMessage(PduHandler.PDU_MESSAGE messagePDU) {

        String message = messagePDU.message;
        String sender = messagePDU.sender;

        if (useFileHandler) {
            client.saveMessage(messagePDU);
        }

        Platform.runLater(() -> {


            if (client.getChatPartner() != null) {
                if (client.getChatPartner().equals(sender)) {
                    guifx.addTextToChat(sender + ": ");
                    guifx.addTextToChat(message + "\n");
                } else {
                    System.out.println("received msg from wrong person");
                }
            } else {
                guifx.setHasSentMessages(sender);
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

                        if (client.updateFriendList(userlistPdu.usernames)) {
                            Platform.runLater(() -> {
                                guifx.clearFriendlist();
                                guifx.addFriendsToList(userlistPdu.usernames, client.getUser().getFullName());
                            });

                            for (String friend : client.getCurrentUserlist()) {
                                if (!friend.equals(client.getUser().getFullName())) {

                                    Platform.runLater(() -> {

                                        guifx.findFriendWithUsername(friend).vbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                            @Override
                                            public void handle(MouseEvent event) {
                                                startChatWith(friend);
                                            }
                                        });
                                    });

                                }
                            }

                        }
                    } else {
                        client.resetFriendlist();
                        Platform.runLater(() -> guifx.sendEmptyFriendlist());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case PduHandler.LOGIN_RESPONSE_PDU: {
               // System.out.println("login response");
                PduHandler.PDU_LOGIN_RESPONSE responsePDU = (PduHandler.PDU_LOGIN_RESPONSE)pdu;

                if (responsePDU.status == ErrorMessage.INPUT_OK.getMessageId()) {
                   // System.out.println("login success!");
                    initFriends();
                    client.login();
                } else {
                    guifx.showLoginError(getCorrectErrorMessage(responsePDU.status));
                    client.killUser();
                    client.shutDownConnection();

                    client = new ClientModel();
                }
                break;
            }
            case PduHandler.CREATE_USER_RESPONSE_PDU: {
                PduHandler.PDU_CREATE_USER_RESPONSE responsePDU = (PduHandler.PDU_CREATE_USER_RESPONSE)pdu;

                if (responsePDU.status == GUIFX.LOGIN_SUCCESS) {
                    System.out.println("Create success!!!");


                    client.killUser();
                    client.shutDownConnection();

                    client = new ClientModel();

                    startOutputThread();

                    Platform.runLater(() -> {

                        initLogin();
                        guifx.showSuccessMessage(ErrorMessage.CR_SUCCESS);
                    });
                } else {
                    System.out.println("Already exists!");
                    Platform.runLater(() -> guifx.showRegisterError(ErrorMessage.CR_USERNAME_ALREADY_EXISTS));
                    client.killUser();
                    client.shutDownConnection();

                    client = new ClientModel();
                }
                break;
            }
            case PduHandler.IMAGE_MESSAGE_PDU: {

                PduHandler.PDU_IMAGE_MESSAGE imgPDU = (PduHandler.PDU_IMAGE_MESSAGE)pdu;
                System.out.println("Received image!");

                /*
                try {

                    client.createNewImage(imgPDU.imageData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                break;
            }
        }
    }



    private ErrorMessage getCorrectErrorMessage(int id) {
        for (ErrorMessage errorMessage : ErrorMessage.values()) {
            if (errorMessage.getMessageId() == id) {
                return errorMessage;
            }
        }
        return null;
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
                    //System.out.println(client.getIncomingMessageQueue().peek().toJSON());
                    handlePDU(client.getIncomingMessageQueue().poll());
                }
            }
        }
    };
}
