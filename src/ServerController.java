import java.io.IOException;
import java.util.Scanner;


public class ServerController {

    private ServerModel server;
    public ServerController() {
        new ServerInputThread().start();
        server = new ServerModel();
    }

    public void startServer() {
        try {
            server.startServer(4001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerInputThread extends Thread {

        Scanner in;
        ServerInputThread() {
            in = new Scanner(System.in);
        }
        @Override
        public void run() {
            while (in.hasNextLine()) {

                switch (in.nextLine()) {
                    case "/close": {
                        System.out.println("Closing server");
                        break;
                    }
                    case "/users": {
                        System.out.println(server.getUserListString());
                        break;
                    }
                    case "/online": {
                        System.out.println(server.getUserList().size() + " users online");
                    }
                }
            }
        }
    }
}
