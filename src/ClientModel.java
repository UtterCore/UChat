import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientModel {

    private User user;
    private Socket sSocket;
    private PrintWriter writer;
    private PDU_HANDLER pdu_handler;
    volatile private Queue<String> incomingMessageQueue;
    private String chatPartner;

    public ClientModel() {
        pdu_handler = new PDU_HANDLER();
        incomingMessageQueue = new LinkedList<>();
        incomingMessageQueue.add(pdu_handler.create_msg_pdu("Enter username: ", null).toString());
    }

    public Queue<String> getIncomingMessageQueue() {
        return incomingMessageQueue;
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
        writer.print(user.getUsername());
        writer.flush();
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
            incomingMessageQueue.add(pdu_handler.create_msg_pdu("No response. Trying to access locally...", null).toString());
            try {
                connectToServer(localAddress, port);
            } catch (IOException io) {
                incomingMessageQueue.add(pdu_handler.create_msg_pdu("No response from the chat server. Shutting down.", null).toString());
                user = null;
                return false;
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return true;
    }

    void createUser(String username) {
        user = new User(username, 0);
     //   incomingMessageQueue.add(pdu_handler.create_msg_pdu("Welcome " + getUser().getUsername() + "!", null).toString());
        incomingMessageQueue.add(pdu_handler.create_msg_pdu(getCommandList(), null).toString());
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
                    incomingMessageQueue.add(pdu_handler.create_msg_pdu("No response from the server. Exit application.", null).toString());
                    quit();
                    break;
                }
                incomingMessageQueue.add(input);
            }
        }
    }
}
