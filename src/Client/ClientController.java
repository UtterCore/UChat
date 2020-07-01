package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController {


    private User user;
    private boolean isConnected = false;
    private Socket sSocket;
    private PrintWriter writer;
    private PrintWriter updateWriter;
    private InputThread inputThread;
    private ScheduledExecutorService updaterExec;


    public ClientController() {
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
    public int connectToServer() throws IOException {
        System.out.println("Connecting to server");
        sSocket = new Socket("localhost", 4001);
        //sSocket = new Socket("2.249.12.98", 4001);

        System.out.println("Connected");


        isConnected = true;
        writer = new PrintWriter(sSocket.getOutputStream());
        updateWriter = new PrintWriter(sSocket.getOutputStream());


        inputThread = new InputThread(sSocket);
        inputThread.start();

        sendUserInfo();

        updaterExec = Executors.newScheduledThreadPool(1);
        updaterExec.scheduleAtFixedRate(updateStatus, 0, 1, TimeUnit.SECONDS);

        return 1;
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


    private Runnable updateStatus = new Runnable() {
        @Override
        public void run() {
            //updateWriter.print("2:update");
            //updateWriter.flush();
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
                String input = getInput();
                System.out.println(input);
            }
        }

        private String getInput() {
            byte[] buffer = new byte[1024];
            int read;
            try {
                while ((read = is.read(buffer)) != -1) {
                    String input = new String(buffer, 0, read);
                    return input;
                    //hej
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
