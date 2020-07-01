package Server;

import java.io.IOException;

public class Server {
    public static void main(String args[]) {

        ServerController server = new ServerController();


        System.out.println("Server");

        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
