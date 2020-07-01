package Server;

import Client.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerController {

    volatile ArrayList<User> userList;
    volatile ArrayList<ServerThread> serverThreads;
    User serverUser;
    ServerSocket serverSocket;

    public ServerController() {
        serverUser = new User("Server", -1);
        userList = new ArrayList<>();
        serverThreads = new ArrayList<>();
        userList.add(serverUser);
    }

    private ServerThread findThreadByName(String username) {
        for (ServerThread thread : serverThreads) {
            if (thread.user.getUsername().equals(username)) {
                return thread;
            }
        }
        return null;
    }
    public void startServer() throws IOException {

        while (true) {
            Socket clientSocket;

            serverSocket = new ServerSocket(4001, 0, InetAddress.getByName("localhost"));
            //serverSocket = new ServerSocket(4001, 0);
            System.out.println("Server created at port 4001 with address: " + InetAddress.getLocalHost());

            clientSocket = serverSocket.accept();
            System.out.println("Client has connected!");
            ServerThread newThread = new ServerThread(clientSocket);
            serverThreads.add(newThread);
            newThread.start();
            serverSocket.close();
        }
    }


    private class ServerThread extends Thread {

        public volatile User user;
        public volatile User targetUser;
        Scanner inputScanner;
        PrintWriter out;
        InputStream is;

        private ServerThread(Socket socket) {
            try {
                is = socket.getInputStream();
                inputScanner = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void readUserInfo() {
            user = new User(getInput(), 0);
            userList.add(user);
        }

        private String getInput() {
            byte[] buffer = new byte[1024];
            int read;
            try {
                while ((read = is.read(buffer)) != -1) {
                    String input = new String(buffer, 0, read);
                    return input;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void sendMessage(User sender, String message) {
            out.print(sender.getUsername() + ": " + message);
            out.flush();
        }
        @Override
        public void run() {
            System.out.println("Thread run");

            if (user == null) {
                readUserInfo();
            }

            while (true) {
                String input = getInput();
                System.out.println(user.getUsername() + ": " + input);

                if (input.contains("/connect")) {
                    String parts[] = input.split(" ");
                    if (findThreadByName(parts[1]) != null) {
                        targetUser = findThreadByName(parts[1]).user;
                    } else {
                        sendMessage(serverUser, "No such user online :(");
                    }
                } else if (input.equals("/users")) {
                    System.out.println("Received user request");
                    if (userList.size() == 2) {
                        sendMessage(serverUser, "No users online :(");
                    } else {
                        for (User onlineUser : userList) {
                            if (onlineUser.getId() >= 0 && !onlineUser.getUsername().equals(user.getUsername())) {
                                sendMessage(serverUser, onlineUser.getUsername());
                            }
                        }
                    }
                } else {
                    if (targetUser != null) {
                        findThreadByName(targetUser.getUsername()).sendMessage(user, input);
                    } else {
                        sendMessage(serverUser, "No receiver. Connect using /connect [user]");
                    }
                }
            }
        }
    }
}
