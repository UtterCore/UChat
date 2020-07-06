import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) {

        /*
        TODO: Buttons for sending/reponding to chat requests
        TODO: Friend list
        TODO: Make outputqueue for server
        TODO: Save chats locally (JSON)
        TODO: (later) Save user data with SQL!
         */
        new ClientController();
    }
}
