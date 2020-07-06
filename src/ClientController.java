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
    private ClientMessageHandler cmh;
    private Scanner input;
    private boolean exit;
    private ScheduledExecutorService outputThread;

    private class guiSendThread extends Thread {


        @Override
        public void run() {
            String textAreaContent = gui.getEnterMessageArea().getText();

            if (textAreaContent.length() == 0) {
                return;
            }

            gui.emptyMessageArea();

            //gui.getEnterMessageArea().setEnabled(false);
            gui.getSendMessageButton().setEnabled(false);

            gui.showConnectingMessage();
            if (client.getUser() == null) {
                if (client.connectToChatServer(textAreaContent, IP_LOCAL, IP_LOCAL, PORT)) {
                    gui.showFullApp();

                    gui.getEnterMessageArea().setEnabled(true);
                    gui.getSendMessageButton().setEnabled(true);

                } else {
                    gui.showConnectionError();

                    gui.getEnterMessageArea().setEnabled(true);
                    gui.getSendMessageButton().setEnabled(true);
                }
            } else {
                cmh.prepareAndSend(textAreaContent);
                gui.printMessageInChat(client.getUser().getUsername() + ": " + textAreaContent + "\n");

                gui.getEnterMessageArea().setEnabled(true);
                gui.getSendMessageButton().setEnabled(true);
            }

            gui.emptyMessageArea();
        }
    }


    public class submitMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new guiSendThread().start();
        }
    }

    public ClientController() {
        client = new ClientModel();
        input = new Scanner(System.in);

        SwingUtilities.invokeLater(() -> {
                gui = new GUI();

            gui.getSendMessageButton().addActionListener(new submitMessageListener());
            gui.getEnterMessageArea().addActionListener(new submitMessageListener());

            cmh = new ClientMessageHandler(client, gui);
        });

        exit = false;
        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
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

        Queue<String> messageQueue;
        @Override
        public void run() {

            if (messageQueue == null) {
                if (client.getIncomingMessageQueue() != null) {
                    messageQueue = client.getIncomingMessageQueue();
                }
            } else {
                if (!messageQueue.isEmpty()) {
                    PDU incomingPDU = PduHandler.getInstance().parse_pdu(messageQueue.poll());
                    handlePDU(incomingPDU);
                }
            }
        }
    };
}
