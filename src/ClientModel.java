import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientModel {

    private boolean shouldExit;
    private User user;
    private boolean isConnected = false;
    private Socket sSocket;
    private PrintWriter writer;
    volatile private Queue<String> incomingMessageQueue;

    public ClientModel() {
        incomingMessageQueue = new LinkedList<>();
        incomingMessageQueue.add("Hello! Enter username: ");
    }

    public Queue<String> getIncomingMessageQueue() {
        return incomingMessageQueue;
    }

    private void quit() {
        try {
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isConnected = false;
        writer.close();
        shouldExit = true;
    }
    public boolean getShouldExit() {
        return shouldExit;
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    void handleInput(String input) {

        writer.print(input);
        writer.flush();
    }

    private void sendUserInfo() {
        writer.print(user.getUsername());
        writer.flush();
    }
    public void connectToServer(String address, int port) throws IOException {
        incomingMessageQueue.add("Connecting to server...");
        sSocket = new Socket(InetAddress.getByName(address), port);
        incomingMessageQueue.add("Connected");

        isConnected = true;
        writer = new PrintWriter(sSocket.getOutputStream());

        new InputThread(sSocket).start();

        sendUserInfo();
    }

    public boolean connectToChatServer(String globalAddress, String localAddress, int port) {
        try {
            //try global
            connectToServer(globalAddress, port);
        } catch (ConnectException b) {

            //try local
            incomingMessageQueue.add("No response. Trying to access locally");
            try {
                connectToServer(localAddress, port);
            } catch (IOException io) {
                incomingMessageQueue.add("No response from the chat server. Shutting down.");
                return false;
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return true;
    }

    void createUser(String username) {
        user = new User(username, 0);
        incomingMessageQueue.add("Welcome " + getUser().getUsername() + "!");
        incomingMessageQueue.add(getCommandList());
    }

    public User getUser() {
        return user;
    }

    public void printCommands() {
        incomingMessageQueue.add(getCommandList());
    }
    public String getCommandList() {
        return("Commands:\n" +
                "/commands - prints this list\n" +
                "/users - prints a list of online users\n" +
                "/connect [username] - attempts to start a chat with " +
                "a user\n" +
                "/disconnect - disconnects from a chat\n" +
                "/quit - exits application\n");
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
                    incomingMessageQueue.add("No response from server. Exit application.");
                    quit();
                    break;
                }
                incomingMessageQueue.add(input);
            }
        }
    }
}
