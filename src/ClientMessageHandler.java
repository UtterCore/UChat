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

    public ClientMessageHandler(PrintWriter writer, User user) {
        this.writer = writer;
        this.user = user;

        incomingPDUQueue = new LinkedList<>();
        outgoingPDUQueue = new LinkedList<>();

        outgoingThread = Executors.newScheduledThreadPool(1);
        outgoingThread.scheduleAtFixedRate(outputThread, 0, 10, TimeUnit.MILLISECONDS);

        updateExec = Executors.newScheduledThreadPool(1);
        updateExec.scheduleAtFixedRate(updater, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void addToIncoming(PDU pdu) {
        incomingPDUQueue.add(pdu);
    }
    public Queue<PDU> getIncomingPDUQueue() {
        return incomingPDUQueue;
    }

    public void startInputThread(Socket sSocket) {
        new InputThread(sSocket).start();
    }

    public void prepareAndSend(String input) {
        PDU message_pdu;

        if (input.startsWith("/")) {
            message_pdu = PduHandler.getInstance().create_cmd_pdu(input.substring(1), user.getFullName());
        } else {
            message_pdu = PduHandler.getInstance().create_msg_pdu(input, user.getFullName());
        }
        enqueuePDU(message_pdu);
    }

    public void sendSetTarget(String target) {
        PDU setTargetPdu = PduHandler.getInstance().create_set_target_pdu(target);
        enqueuePDU(setTargetPdu);
    }

    public void sendUserInfo() {
        PDU pdu = PduHandler.getInstance().create_chatinfo_pdu(user.getUsername());

        enqueuePDU(pdu);
    }

    public void sendIsLeaving() {
        PDU pdu = PduHandler.getInstance().create_is_leaving_pdu();

        SocketIO.sendPDU(writer, pdu);
    }


    public void enqueuePDU(PDU pdu) {
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

            sendUserlistRequestPDU();
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
                    incomingPDUQueue.add(PduHandler.getInstance().create_msg_pdu("No response from the server. Exit application.", null));
                    //quit();
                    break;
                }
                incomingPDUQueue.add(PduHandler.getInstance().parse_pdu(input));
            }
        }
    }
}
