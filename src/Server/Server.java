package Server;

import java.io.IOException;

public class Server {
    public static void main(String args[]) {


        try {
            new ServerController().startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
