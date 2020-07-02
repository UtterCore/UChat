import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController {


    private boolean shouldExit;
    private User user;
    private boolean isConnected = false;
    private Socket sSocket;
    private PrintWriter writer;

    public ClientController() {
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

    void handleInput(String input) {

        if (isConnected) {
            writer.print(input);
            writer.flush();
        } else {
            switch (input) {
                case "/commands": {
                    System.out.println(getCommandList());
                }
            }
        }
    }

    private void sendUserInfo() {
        writer.print(user.getUsername());
        writer.flush();
    }
    public void connectToServer(String address, int port) throws IOException {
        System.out.println("Connecting to server...");
        sSocket = new Socket(InetAddress.getByName(address), port);
        System.out.println("Connected");

        isConnected = true;
        writer = new PrintWriter(sSocket.getOutputStream());

        new InputThread(sSocket).start();

        sendUserInfo();
    }

    void createUser(String username) {
        user = new User(username, 0);
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
                    System.out.println("No response from server. Exit application.");
                    quit();
                    break;
                }
                System.out.println(input);
            }
        }
    }
}
