import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientModel {

    private User user;
    private Socket sSocket;
    private PrintWriter writer;
    volatile private Queue<PDU> incomingPDUQueue;
    volatile private Queue<PDU> outgoingPDUQueue;
    private String chatPartner;
    private ScheduledExecutorService updateExec;
    private ScheduledExecutorService outgoingThread;

    public ClientModel() {
        incomingPDUQueue = new LinkedList<>();
        outgoingPDUQueue = new LinkedList<>();
        incomingPDUQueue.add(PduHandler.getInstance().create_msg_pdu("Enter username: ", null));
    }

    public Queue<PDU> getIncomingMessageQueue() {
        return incomingPDUQueue;
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public void setChatPartner(String chatPartner) {
        this.chatPartner = chatPartner;
    }

    private void quit() {
        try {
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.close();
    }
    void handleInput(String input) {

        if (writer == null) {
            System.out.println("Server dead hehe");
            return;
        }

        writer.print(input);
        writer.flush();
    }

    private void sendUserInfo() {
        PDU pdu = PduHandler.getInstance().create_chatinfo_pdu(getUser().getUsername());

        enqueuePDU(pdu);
    }

    public void connectToServer(String address, int port) throws IOException {
      //  incomingMessageQueue.add(pdu_handler.create_msg_pdu("Connecting to server... ", null).toString());
        sSocket = new Socket(InetAddress.getByName(address), port);
       // incomingMessageQueue.add(pdu_handler.create_msg_pdu("Connected", null).toString());

        writer = new PrintWriter(sSocket.getOutputStream());

        new InputThread(sSocket).start();

        sendUserInfo();
    }

    public boolean connectToChatServer(String username, String globalAddress, String localAddress, int port) {

        createUser(username);

        try {
            //try global
            connectToServer(globalAddress, port);
        } catch (ConnectException b) {

            //try local
            incomingPDUQueue.add(PduHandler.getInstance().create_msg_pdu("No response. Trying to access locally...", null));
            try {
                connectToServer(localAddress, port);
            } catch (IOException io) {
                incomingPDUQueue.add(PduHandler.getInstance().create_msg_pdu("No response from the chat server. Shutting down.", null));
                user = null;
                return false;
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }

        updateExec = Executors.newScheduledThreadPool(1);
        updateExec.scheduleAtFixedRate(updater, 0, 1000, TimeUnit.MILLISECONDS);

        outgoingThread = Executors.newScheduledThreadPool(1);
        outgoingThread.scheduleAtFixedRate(outputThread, 0, 10, TimeUnit.MILLISECONDS);

        return true;
    }

    private void createUser(String username) {
        user = new User(username, 0);
        incomingPDUQueue.add(PduHandler.getInstance().create_msg_pdu(getCommandList(), null));
    }


    public User getUser() {
        return user;
    }

    public String getCommandList() {
        return("Commands:\n" +
                "/commands - prints this list\n" +
                "/users - prints a list of online users\n" +
                "/connect [username] - attempts to start a chat with " +
                "a user\n" +
                "/disconnect - disconnects from a chat\n");
    }

    public void enqueuePDU(PDU pdu) {
        outgoingPDUQueue.add(pdu);
    }

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
                    quit();
                    break;
                }
                incomingPDUQueue.add(PduHandler.getInstance().parse_pdu(input));
            }
        }
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
                    PduHandler.getInstance().create_userlist_requeust_pdu(getUser().getFullName());
            enqueuePDU(pdu);
        }
        @Override
        public void run() {

            sendUserlistRequestPDU();
        }
    };
}
