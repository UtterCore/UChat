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

    private ClientModel client;
    private Scanner input;
    private boolean exit;
    private ScheduledExecutorService outputThread;

    public ClientController() {
        client = new ClientModel();
        input = new Scanner(System.in);
        exit = false;
        outputThread = Executors.newScheduledThreadPool(1);
        outputThread.scheduleAtFixedRate(getOutput, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void sendWelcomeMessage() {
        System.out.println("Hello! Enter username: ");
        if (input.hasNextLine()) {
            client.createUser(input.nextLine());
        }

        System.out.println("Welcome " + client.getUser().getUsername() + "!");
        System.out.println(client.getCommandList());
    }

    private void connectToChatServer() {
        try {
            //try global
            client.connectToServer(IP_LOCAL, 4001);
        } catch (ConnectException b) {
            //try local

            System.out.println("No response. Trying to access locally");
            try {
                client.connectToServer(IP_LOCAL, 4001);
            } catch (IOException io) {
                System.out.println("No response from the chat server. Shutting down.");
                exit = true;
            }
        } catch (IOException e) {
            // e.printStackTrace();
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
                    System.out.println(messageQueue.poll());
                }
            }
        }
    };

    public void startClient() {

        System.out.println("Client");

        sendWelcomeMessage();

        connectToChatServer();

        getInput();
    }
}
