import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
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
    private PDU_HANDLER pdu_handler;

    private void guiSendMessage() {
        String textAreaContent = gui.getEnterMessageArea().getText();

        if (textAreaContent.length() == 0) {
            return;
        }
        //System.out.println("Text: " + textAreaContent);

        if (client.getUser() == null) {
            client.createUser(textAreaContent);
            client.connectToChatServer(IP_LOCAL, IP_LOCAL, PORT);
            gui.showUsers();
        } else {
            cmh.prepareAndSend(textAreaContent);
         }

        //print back the message that you sent
        gui.printMessageInChat(client.getUser().getUsername() + ": " + textAreaContent + "\n");
        gui.emptyMessageArea();
    }
    public ClientController() {
        client = new ClientModel();
        input = new Scanner(System.in);
        pdu_handler = new PDU_HANDLER();

        SwingUtilities.invokeLater(() -> {
                gui = new GUI();
            gui.getSendMessageButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    guiSendMessage();
                }
            } );

            gui.getEnterMessageArea().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    guiSendMessage();
                }
            });

            cmh = new ClientMessageHandler(client, gui);
        });



        exit = false;
        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void enterUsername() {

        if (input.hasNextLine()) {
            client.createUser(input.nextLine());
        }
    }

    private void getInput() {
        while (!exit && !client.getShouldExit()) {
            if (input.hasNextLine()) {
                String currentInput = input.nextLine();
                client.handleInput(currentInput);

                if (currentInput.equals("/quit")) {
                    exit = true;
                    client.setShouldExit(true);
                    outputThread.shutdown();
                    System.exit(0);

                } else if (currentInput.equals("/commands")) {
                    client.printCommands();
                }
            }
        }
    }

    public void startClient() {
        //enterUsername();
        //client.connectToChatServer(IP_LOCAL, IP_LOCAL, PORT);
        //getInput();
    }

    private void handlePDU(PDU pdu) {

        switch (pdu.type) {

            case 1: {
                PDU_HANDLER.PDU_MESSAGE msgPdu = (PDU_HANDLER.PDU_MESSAGE)pdu;

                SwingUtilities.invokeLater(() -> {
                    if (!msgPdu.sender.equals(" ")) {
                        gui.printMessageInChat(msgPdu.sender + ": ");
                    }
                    gui.printMessageInChat(msgPdu.message + "\n");
                });
                break;
            }
            case 3: {
                PDU_HANDLER.PDU_CHATINFO chatInfoPdu = (PDU_HANDLER.PDU_CHATINFO)pdu;

                if (chatInfoPdu.chatPartner.equals("_null")) {
                    gui.setChatPartnerLabel(null);
                    client.setChatPartner(null);
                } else {
                    client.setChatPartner(chatInfoPdu.chatPartner);
                    gui.setChatPartnerLabel(chatInfoPdu.chatPartner);
                }
                break;
            }

            case 4: {
                PDU_HANDLER.PDU_USERLIST userlistPdu = (PDU_HANDLER.PDU_USERLIST)pdu;
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
                    PDU incomingPDU = pdu_handler.parse_pdu(messageQueue.poll());
                    handlePDU(incomingPDU);

                    //System.out.println(newMessage);

                    SwingUtilities.invokeLater(() -> {
                    //    gui.printMessageInChat(newMessage + "\n");
                    });
                }
            }
        }
    };
}
