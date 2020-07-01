package Client;

import java.io.IOException;
import java.util.Scanner;

public class Client {

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
            client.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!exit) {
            //System.out.print("> ");
            if (input.hasNextLine()) {
                client.handleInput(input.nextLine());
            }
        }
    }

}
