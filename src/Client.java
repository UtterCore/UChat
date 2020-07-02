import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.util.Scanner;

public class Client {

    public static final String IP_GLOBAL = "2.249.12.98";
    public static final String IP_LOCAL = "192.168.1.70";
    public static void main(String args[]) {

        ClientController client = new ClientController();

        Scanner input = new Scanner(System.in);
        boolean exit = false;

        System.out.println("Client");

        System.out.println("Hello! Enter username: ");
        if (input.hasNextLine()) {
            client.createUser(input.nextLine());
        }

        System.out.println("Welcome " + client.getUser().getUsername() + "!");
        System.out.println(client.getCommandList());

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

        while (!exit && !client.getShouldExit()) {
            //System.out.print("> ");
            if (input.hasNextLine()) {
                client.handleInput(input.nextLine());
            }
        }
    }

}
