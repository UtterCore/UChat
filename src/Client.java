import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.util.Scanner;

public class Client {

    /* Client:
    TODO: Gör en model-klass som startas av controllern. Model ska vara helt
    fristående från gui och controller (så jag kan göra en android-version hehe)
    TODO: Gör så att den kopplas till servern det första som händer så att en
    kontroll kan göras på användarnamnet så att det inte är taget
    TODO: Gör så att input kontrolleras (eventuellt i client) t.ex. när man använder
    parts[1] osv (/acccept utter)
    TODO: Gör den här main-klassen mindre. Den ska bara starta controllern
    TODO: Skapa ett GUI till client (sist av allt)
     */

     /*Server: TODO Det i controllern hör hemma i model egentligen. Gör en ny controller som hanterar
indata från konsollen (typ så man kollar vilka som är online osv,
och gör så att den startar en model när servern ska sättas igång*/

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
