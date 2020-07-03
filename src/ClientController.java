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
        enterUsername();
        client.connectToChatServer(IP_LOCAL, IP_LOCAL, PORT);
        getInput();
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
}
