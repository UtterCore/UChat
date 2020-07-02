import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

    public void startServer(int port) throws IOException {

        System.out.println("Starting server...");
        serverSocket = new ServerSocket(port);
        System.out.println("Server is running. Waiting for connections...");

        while (true) {
            Socket clientSocket;

            clientSocket = serverSocket.accept();
            System.out.println("Client has connected!");
            ServerThread newThread = new ServerThread(clientSocket);
            serverThreads.add(newThread);
            newThread.start();
        }
    }


    private class ServerThread extends Thread {

        public volatile User user;
        public volatile User targetUser;

        Scanner inputScanner;
        PrintWriter writer;
        InputStream is;
        Socket socket;

        long lastUpdate;

        private ServerThread(Socket socket) {
            lastUpdate = System.currentTimeMillis();

            this.socket = socket;

            try {
                is = socket.getInputStream();
                inputScanner = new Scanner(socket.getInputStream());
                setWriter(new PrintWriter(socket.getOutputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public User getTargetUser() {
            return targetUser;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public void setWriter(PrintWriter writer) {
            this.writer = writer;
        }

        private void readUserInfo() {
            try {
                user = new User(SocketIO.getInput(is), 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            userList.add(user);
        }

        private void sendChatMessage(User from, String message) {
            SocketIO.sendMessage(SocketIO.TYPE_MESSAGE, from, getWriter(), message);
        }

        private void disconnectChat() {
            if (targetUser != null) {

                ServerThread otherThread = findThreadByName(targetUser.getUsername());
                otherThread.sendChatMessage(serverUser, user.getUsername() + " has left the chat");
                otherThread.targetUser = null;

                sendChatMessage(serverUser, "Disconnected from " + targetUser.getUsername());
                targetUser = null;
            }
        }

        private void connectToUser(User otherUser) {
            targetUser = otherUser;

            ServerThread otherThread = findThreadByName(otherUser.getUsername());

            if (isConnectedTo(otherUser)) {
                otherThread.sendChatMessage(serverUser, "User " +
                        user.getUsername() + " has connected to the chat");

                sendChatMessage(serverUser, "You are now connected to " +
                        targetUser.getUsername() + "!");
            } else {
                sendChatMessage(serverUser, "Attempting to connect to " +
                        otherUser.getUsername() + ", please wait.");

                otherThread.sendChatMessage(serverUser, "User " +
                        user.getUsername() + " would like to start a chat.\nType: " +
                        "/accept " + user.getUsername() + " to start the chat.");
            }
        }

        private boolean isConnectedTo(User otherUser) {
            return (getTargetUser() == otherUser &&
                    findThreadByName(otherUser.getUsername()).getTargetUser() == user);
        }


        void handleInput(String input) {
            if (input.contains("/connect")) {
                String parts[] = input.split(" ");
                if (findThreadByName(parts[1]) != null) {
                    User otherUser = findThreadByName(parts[1]).user;
                    connectToUser(otherUser);
                } else {
                    sendChatMessage(serverUser, "No such user online :(");
                }
            } else if (input.equals("/users")) {
                System.out.println("Received user request");
                if (userList.size() == 2) {
                    sendChatMessage(serverUser, "No users online :(");
                } else {
                    for (User onlineUser : userList) {
                        if (onlineUser.getId() >= 0 && !onlineUser.getUsername().equals(user.getUsername())) {
                            sendChatMessage(serverUser, onlineUser.getUsername());
                        }
                    }
                }
            } else if (input.equals("/disconnect") && targetUser != null) {
                disconnectChat();
            } else if (input.equals("/quit")) {
                userList.remove(user);
            } else if (input.contains("/accept")) {
                String parts[] = input.split(" ");
                User otherUser = findThreadByName(parts[1]).user;
                connectToUser(otherUser);
            } else {
                if (targetUser != null) {
                    if (isConnectedTo(targetUser)) {
                        findThreadByName(targetUser.getUsername()).sendChatMessage(user, input);
                    } else {
                        sendChatMessage(serverUser, targetUser.getUsername() + " has not yet connected.");
                    }
                } else {
                    sendChatMessage(serverUser, "No receiver. Connect using /connect [user]");
                }
            }
        }

        @Override
        public void run() {
            System.out.println("Thread run");


            if (user == null) {
                readUserInfo();
            }

            while (true) {
                String input = null;
                try {
                    input = SocketIO.getInput(is);
                } catch (IOException e) {
                    System.out.println("Error: No response from " +
                            user.getUsername() + ". Removing");
                    disconnectChat();
                    userList.remove(user);
                }


                if (input == null) {
                    System.out.println("Unidentified message received (null) from "
                            + socket.getInetAddress());
                    break;
                } else {
                    System.out.println(user.getUsername() + ": " + input);
                    handleInput(input);
                }
            }
        }
    }
}
