package Server;

import java.io.IOException;
import java.util.Scanner;
import java.util.*;
import User.*;


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

                String adminInput = in.nextLine();

                switch (adminInput) {
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
                        break;
                    }
                    case "/threads": {
                        System.out.println("Threads: ");
                        int i = 0;
                        for (ServerModel.ServerThread thread : server.getServerThreads()) {
                            if (thread.getUser() != null) {
                                System.out.println("#" + i++ + " " + thread.getUser().getFullName());
                            }
                        }
                        System.out.print("\n");
                        break;
                    }
                }

                String parts[] = adminInput.split(" ");
                if (parts.length == 2) {
                    switch (parts[0]) {
                        case "/info": {
                            User user = server.findUserByName(parts[1]);
                            if (user != null) {
                                System.out.println("User info of " + user.getFullName());
                                System.out.println(user.getInfoString());
                            } else {
                                System.out.println("User " + parts[1] + " is not online");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
