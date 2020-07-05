import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerModel {


    volatile ArrayList<User> userList;
    volatile ArrayList<ServerThread> serverThreads;
    User serverUser;
    ServerSocket serverSocket;

    public ServerModel() {
        serverUser = new User("Server", -1);
        userList = new ArrayList<>();
        serverThreads = new ArrayList<>();
        userList.add(serverUser);
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    private ServerThread findThreadByName(String username) {
        for (ServerThread thread : serverThreads) {
            if (thread.user.getFullName().equals(username)) {
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

    public String getUserListString(User user) {
        String userListString = "";
        int i = 0;
        for (User onlineUser : userList) {
            if (onlineUser.getId() >= 0 && !onlineUser.getFullName().equals(user.getFullName())) {
                userListString += "#" + i + " " + onlineUser.getFullName() + "\n";
                i++;
            }
        }
        return userListString;
    }

    public String getUserListString() {
        String userListString = "";
        for (User onlineUser : userList) {
            userListString += onlineUser.getFullName() + "\n";
        }
        return userListString;
    }

    private int getFirstId(String username) {
        int count = 0;
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                count++;
            }
        }
        return count;
    }

    private class ServerThread extends Thread {

        public volatile User user;
        public volatile User targetUser;

        Scanner inputScanner;
        PrintWriter writer;
        InputStream is;
        Socket socket;

        long lastUpdate;
        boolean exit;

        private ServerThread(Socket socket) {
            lastUpdate = System.currentTimeMillis();
            exit = false;

            this.socket = socket;

            try {
                is = socket.getInputStream();
                inputScanner = new Scanner(socket.getInputStream());
                setWriter(new PrintWriter(socket.getOutputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void quit() {

            disconnectChat();
            userList.remove(user);

            inputScanner.close();
            writer.close();

            try {
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverThreads.remove(this);
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
            user.setId(getFirstId(user.getUsername()));
            userList.add(user);
        }

        private void sendChatMessage(User from, String message) {
            SocketIO.sendMessage(SocketIO.TYPE_MESSAGE, from, getWriter(), message);
        }

        private void disconnectChat() {
            if (targetUser != null) {

                ServerThread otherThread = findThreadByName(targetUser.getFullName());
                if (otherThread == null) {
                    System.out.println("Wtf?");
                }

                if (otherThread.targetUser == user) {
                    otherThread.sendChatMessage(serverUser, user.getFullName() +
                            " has left the chat");
                    otherThread.targetUser = null;
                } else {
                    otherThread.sendChatMessage(serverUser, user.getFullName() +
                            " is not requesting to chat anymore");
                }

                sendChatMessage(serverUser, "Disconnected from " + targetUser.getFullName());
                targetUser = null;
            }
        }

        private void connectToUser(User otherUser) {

            if (otherUser == user) {
                sendChatMessage(serverUser, "You cannot chat with yourself!");
                return;
            }

            if (targetUser != null) {
                disconnectChat();
            }
            targetUser = otherUser;

            ServerThread otherThread = findThreadByName(otherUser.getFullName());
           if (otherThread == null) {
               return;
           }

            if (isConnectedTo(otherUser)) {
                otherThread.sendChatMessage(serverUser, "User " +
                        user.getFullName() + " has connected to the chat");

                sendChatMessage(serverUser, "You are now connected to " +
                        targetUser.getFullName() + "!");
            } else {
                sendChatMessage(serverUser, "Attempting to connect to " +
                        otherUser.getFullName() + ", please wait.");

                otherThread.sendChatMessage(serverUser, "User " +
                        user.getFullName() + " would like to start a chat.\nType: " +
                        "/accept " + user.getFullName() + " to start the chat.");
            }
        }

        private boolean isConnectedTo(User otherUser) {
            return (getTargetUser() == otherUser &&
                    findThreadByName(otherUser.getFullName()).getTargetUser() == user);
        }

        void handleCommand(String input) throws NullPointerException {

            if (input.startsWith("connect")) {
                String parts[] = input.split(" ");
                if (findThreadByName(parts[1]) != null) {
                    User otherUser = findThreadByName(parts[1]).user;
                    connectToUser(otherUser);
                } else {
                    sendChatMessage(serverUser, "No such user online :(");
                }
            } else if (input.startsWith("accept")) {
                String parts[] = input.split(" ");
                ServerThread otherThread = findThreadByName(parts[1]);
                User otherUser = otherThread.user;

                if (otherUser != null) {
                    if (otherThread.getTargetUser() == user) {
                        connectToUser(otherUser);
                    } else {
                        sendChatMessage(serverUser, "This user has not " +
                                "requested to chat with you");
                    }
                } else {
                    sendChatMessage(serverUser, "No such user online :(");
                }
            } else {
                switch (input) {
                    case "users": {
                        System.out.println("Received user request");
                        if (userList.size() == 2) {
                            sendChatMessage(serverUser, "No users online :(");
                        } else {
                            sendChatMessage(serverUser, "\n" + getUserListString(user));
                        }
                        break;
                    }
                    case "disconnect": {
                        if (targetUser != null) {
                            disconnectChat();
                        }
                        break;
                    }
                    case "quit": {
                        quit();
                        break;
                    }
                    case "commands": {
                        break;
                    }
                    default: {
                        sendChatMessage(serverUser, "Invalid command.");
                        break;
                    }
                }
            }
        }

        void handleMessaging(String input) throws NullPointerException {
            if (targetUser != null) {
                if (isConnectedTo(targetUser)) {
                    findThreadByName(targetUser.getFullName()).sendChatMessage(user, input);
                } else {
                    sendChatMessage(serverUser, targetUser.getFullName() + " has not yet connected.");
                }
            } else {
                sendChatMessage(serverUser, "No receiver. Connect using /connect [user]");
            }
        }

        void handleInput(String input) throws IllegalArgumentException {
            try {
                if (input.startsWith("/")) {
                    handleCommand(input.substring(1));
                } else {
                    handleMessaging(input);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid arguments");
            }
        }

        @Override
        public void run() {


            if (user == null) {
                readUserInfo();
            }

            while (!exit) {
                String input = null;
                try {
                    input = SocketIO.getInput(is);
                } catch (IOException e) {
                    System.out.println("Error: No response from " +
                            user.getFullName() + ". Removing");
                    disconnectChat();
                    userList.remove(user);
                }


                if (input == null) {
                    System.out.println("Unidentified message received (null) from "
                            + socket.getInetAddress());
                    break;
                } else {
                    System.out.println(user.getFullName() + ": " + input);
                    try {
                        handleInput(input);
                    } catch (IllegalArgumentException e) {
                        sendChatMessage(serverUser, "Invalid command. Type /commands for help");
                    }
                }
            }
        }
    }
}
