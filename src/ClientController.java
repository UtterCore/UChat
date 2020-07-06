import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private GUI gui;
    private ScheduledExecutorService outputThread;

    public ClientController() {
        client = new ClientModel();

        SwingUtilities.invokeLater(() -> {
            gui = new GUI();

            gui.getSendMessageButton().addActionListener(new submitMessageListener());
            gui.getEnterMessageArea().addActionListener(new submitMessageListener());
        });

        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void connect(String username) {
        gui.getSendMessageButton().setEnabled(false);
        gui.showConnectingMessage();

        if (client.connectToChatServer(username, IP_LOCAL, IP_LOCAL, PORT)) {
            gui.showFullApp();

            gui.getEnterMessageArea().setEnabled(true);
            gui.getSendMessageButton().setEnabled(true);

        } else {
            gui.showConnectionError();

            gui.getEnterMessageArea().setEnabled(true);
            gui.getSendMessageButton().setEnabled(true);
        }

        gui.emptyMessageArea();
    }

    private void sendMessage(String message) {
        gui.getSendMessageButton().setEnabled(false);

        ClientMessageHandler.prepareAndSend(client, message);
        gui.printMessageInChat(client.getUser().getUsername() + ": " + message + "\n");

        gui.getEnterMessageArea().setEnabled(true);
        gui.getSendMessageButton().setEnabled(true);

        gui.emptyMessageArea();
    }

    private class guiSendThread extends Thread {

        @Override
        public void run() {
            String textAreaContent = gui.getEnterMessageArea().getText();

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


    public class submitMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new guiSendThread().start();
        }
    }

    private void handlePDU(PDU pdu) {

        switch (pdu.type) {

            case PduHandler.MESSAGE_PDU: {
                PduHandler.PDU_MESSAGE msgPdu = (PduHandler.PDU_MESSAGE)pdu;

                SwingUtilities.invokeLater(() -> {
                    if (!msgPdu.sender.equals(" ")) {
                        gui.printMessageInChat(msgPdu.sender + ": ");
                    }
                    gui.printMessageInChat(msgPdu.message + "\n");
                });
                break;
            }
            case PduHandler.CHATINFO_PDU: {
                PduHandler.PDU_CHATINFO chatInfoPdu = (PduHandler.PDU_CHATINFO)pdu;

                if (chatInfoPdu.chatPartner.equals("_null")) {
                    gui.setChatPartnerLabel(null);
                    client.setChatPartner(null);
                } else {
                    client.setChatPartner(chatInfoPdu.chatPartner);
                    gui.setChatPartnerLabel(chatInfoPdu.chatPartner);
                }
                break;
            }

            case PduHandler.USERLIST_PDU: {
                PduHandler.PDU_USERLIST userlistPdu = (PduHandler.PDU_USERLIST)pdu;
                gui.updateUserlist(userlistPdu.usernames);
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
