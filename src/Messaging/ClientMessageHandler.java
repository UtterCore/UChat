package Messaging;

import User.User;
import FileHandler.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMessageHandler {

    private PrintWriter writer;
    private ScheduledExecutorService outgoingThread;
    private ScheduledExecutorService updateExec;
    volatile private Queue<PDU> incomingPDUQueue;
    volatile private Queue<PDU> outgoingPDUQueue;
    private User user;
    private Socket sSocket;

    public ClientMessageHandler(User user, Socket sSocket) {
        this.user = user;
        this.sSocket = sSocket;
        this.writer = null;

        incomingPDUQueue = new LinkedList<>();
        outgoingPDUQueue = new LinkedList<>();

        try {
            writer = new PrintWriter(sSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        outgoingThread = Executors.newScheduledThreadPool(1);
        outgoingThread.scheduleAtFixedRate(outputThread, 0, 10, TimeUnit.MILLISECONDS);


    }

    public void startUserListUpdater() {
        updateExec = Executors.newScheduledThreadPool(1);
        updateExec.scheduleAtFixedRate(updater, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public synchronized Queue<PDU> getIncomingPDUQueue() {
        return incomingPDUQueue;
    }

    public void startIO() {
        new InputThread(sSocket).start();
    }

    public void prepareAndSend(String input, String target) {
        PDU message_pdu;

        System.out.println("prepareandsend input " + input);
        if (input.startsWith("/")) {
            message_pdu = PduHandler.getInstance().create_cmd_pdu(input.substring(1), user.getFullName());
        } else {
            message_pdu = PduHandler.getInstance().create_msg_pdu(input, user.getFullName(), target);
        }


        FileHandler.savePDUToFile(message_pdu, user.getFullName());

        enqueuePDU(message_pdu);
    }

    public void sendToMe(String input, String from) {
        enqueuePDU(PduHandler.getInstance().create_msg_pdu(input, from, from));
    }

    public void sendSetTarget(String target) {
        PDU setTargetPdu = PduHandler.getInstance().create_set_target_pdu(target);
        enqueuePDU(setTargetPdu);
    }

    public void sendUserInfo() {
        //Messaging.PDU pdu = Messaging.PduHandler.getInstance().create_chatinfo_pdu(user.getUsername());
        PDU pdu = PduHandler.getInstance().create_login_pdu(user.getUsername(), "password");
        enqueuePDU(pdu);
    }

    public void sendIsLeaving() {
        PDU pdu = PduHandler.getInstance().create_is_leaving_pdu();

        SocketIO.sendPDU(writer, pdu);
    }


    public synchronized void enqueuePDU(PDU pdu) {
        outgoingPDUQueue.add(pdu);
    }

    private Runnable outputThread = new Runnable() {

        @Override
        public void run() {

            if (!outgoingPDUQueue.isEmpty()) {
                if (writer == null) {
                    System.out.println("Server dead?");
                    return;
                }
                SocketIO.sendPDU(writer, outgoingPDUQueue.poll());
            }
        }
    };

    private Runnable updater = new Runnable() {
        private void sendUserlistRequestPDU() {

            PduHandler.PDU_USERLIST_REQUEST pdu =
                    PduHandler.getInstance().create_userlist_requeust_pdu(user.getFullName());
            enqueuePDU(pdu);
        }
        @Override
        public void run() {

            if (writer != null) {
                sendUserlistRequestPDU();
            }
        }
    };

    private class InputThread extends Thread {

        InputStream is;
        private InputThread(Socket socket) {
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {

            while (true) {
                String input;
                try {
                    input = SocketIO.getInput(is);
                } catch (IOException e) {
                   // incomingPDUQueue.add(Messaging.PduHandler.getInstance().create_msg_pdu("No response from the server. Exit application.", null, user.getFullName()));
                    //quit();
                    System.out.println("Exception, no response from server");
                    break;
                }
                if (input != null) {
                    incomingPDUQueue.add(PduHandler.getInstance().parse_pdu(input));
                }
            }
        }
    }
}
