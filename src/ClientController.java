import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController {

    private static final String IP_GLOBAL = "2.249.12.98";
    private static final String IP_LOCAL = "192.168.1.70";
    private static final int PORT = 4001;

    private ClientModel client;
    //private GUI gui;
    private GUIFX guifx;
    private ScheduledExecutorService outputThread;

    private ClientController cont;
    public ClientController(GUIFX guifx) {
        client = new ClientModel();

        cont = this;
        this.guifx = guifx;
        guifx.showLogin();
        addGUIFXListeners();

        /*
        SwingUtilities.invokeLater(() -> {
            gui = new GUI();

            gui.getSendMessageButton().addActionListener(new submitMessageListener());
            gui.getEnterMessageArea().addActionListener(new submitMessageListener());
        });
        */

        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
    }

    //TODO: Duplicated code here. fix
    private void addGUIFXListeners() {
        Button submitButton = guifx.getSubmitButton();

        if (guifx.getChatField() != null) {
            guifx.getChatField().setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent event) {
                    guifx.clearError();

                    String textAreaContent = guifx.getUserTextField().getText();
                    if (textAreaContent.length() == 0) {
                        return;
                    }
                    if (guifx.getUserTextField().getLength() == 0) {
                        guifx.showUsernameError();
                    } else {
                        guifx.showConnecting();

                        if (client.getUser() == null) {
                            if (connect(textAreaContent)) {
                                guifx.showChat();
                                addGUIFXListeners();
                                // guifx.showFXML();
                            } else {
                                guifx.showConnectionError();
                            }
                        } else {
                            String chatAreaContent = guifx.getChatField().getText();
                            sendMessage(chatAreaContent);
                            System.out.println("sending " + chatAreaContent);
                        }
                        //  new guiSendThread().start();
                    }
                }
            });
        }

        submitButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                guifx.clearError();

                String textAreaContent = guifx.getUserTextField().getText();
                if (textAreaContent.length() == 0) {
                    return;
                }
                if (guifx.getUserTextField().getLength() == 0) {
                    guifx.showUsernameError();
                } else {
                    guifx.showConnecting();

                    if (client.getUser() == null) {
                        if (connect(textAreaContent)) {
                            guifx.showChat();
                            addGUIFXListeners();
                           // guifx.showFXML();
                        } else {
                            guifx.showConnectionError();
                        }
                    } else {
                        String chatAreaContent = guifx.getChatField().getText();
                        sendMessage(chatAreaContent);
                        System.out.println("sending " + chatAreaContent);
                    }
                  //  new guiSendThread().start();
                }
            }
        });
    }

    private boolean connect(String username) {
        /*gui.getSendMessageButton().setEnabled(false);
        gui.showConnectingMessage();
        */
        if (client.connectToChatServer(username, IP_LOCAL, IP_LOCAL, PORT)) {
           // gui.showFullApp();

            /*
            gui.getEnterMessageArea().setEnabled(true);
            gui.getSendMessageButton().setEnabled(true);
            */
            return true;
        } else {
            /*
            gui.showConnectionError();

            gui.getEnterMessageArea().setEnabled(true);
            gui.getSendMessageButton().setEnabled(true);
            */
            return false;
        }

        /*
        gui.emptyMessageArea();
        */
    }

    private void sendMessage(String message) {
        /*
        gui.getSendMessageButton().setEnabled(false);
        */
        ClientMessageHandler.prepareAndSend(client, message);

        guifx.addTextToChat(client.getUser().getFullName() + ": " + message + "\n");
        guifx.clearTextField();

        /*
        gui.getEnterMessageArea().setEnabled(true);
        gui.getSendMessageButton().setEnabled(true);

        gui.emptyMessageArea();
        */
    }

    private class guiSendThread extends Thread {

        @Override
        public void run() {
            String textAreaContent = guifx.getUserTextField().getText();
            if (textAreaContent.length() == 0) {
                return;
            }

            if (client.getUser() == null) {
                connect(textAreaContent);
            } else {
                sendMessage(textAreaContent);
            }
        }
    }

    private void handlePDU(PDU pdu) {

        switch (pdu.type) {

            case PduHandler.MESSAGE_PDU: {
                PduHandler.PDU_MESSAGE msgPdu = (PduHandler.PDU_MESSAGE)pdu;

                SwingUtilities.invokeLater(() -> {
                    if (!msgPdu.sender.equals(" ")) {
                        guifx.addTextToChat(msgPdu.sender + ": ");
                    }
                    guifx.addTextToChat(msgPdu.message + "\n");
                });
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
             //   gui.updateUserlist(userlistPdu.usernames);
            }
        }
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
