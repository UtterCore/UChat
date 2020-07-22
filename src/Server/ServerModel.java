package Server;

import Messaging.*;
import User.User;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {

    volatile private ArrayList<User> userList;
    volatile private ArrayList<ServerThread> serverThreads;
    private ServerSocket serverSocket;

    public ServerModel() {
        userList = new ArrayList<>();
        serverThreads = new ArrayList<>();
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    private ServerThread findThreadByName(String username) {
        for (ServerThread thread : serverThreads) {
            if (thread.user != null) {
                if (thread.user.getFullName().equals(username)) {
                    return thread;
                }
            }
        }
        return null;
    }

    private User findUserByName(String username) {
        for (User user : userList) {
            if (user.getFullName().equals(username)) {
                return user;
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

    public ArrayList<ServerThread> getServerThreads() {
        return serverThreads;
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

    public class ServerThread extends Thread {

        private volatile User user;
        private Socket socket;
        private ServerMessageHandler smh;

        long lastUpdate;
        boolean exit;

        private ServerThread(Socket socket) {
            lastUpdate = System.currentTimeMillis();
            exit = false;

            this.socket = socket;
            smh = new ServerMessageHandler(socket);
        }

        public User getUser() {
            return user;
        }

        public ServerMessageHandler getSmh() {
            return smh;
        }

        private void quit() {

            userList.remove(user);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverThreads.remove(this);
        }

        private void sendUserListPDU() {

            ArrayList<String> userlistString = new ArrayList<>();
            for (User user : userList) {
                userlistString.add(user.getFullName());
            }

            PDU userlistPDU = PduHandler.getInstance().create_userlist_pdu(userlistString);

            smh.enqueuePDU(userlistPDU);
        }

        boolean checkCredentials(String username, String password) {

            return (UserJSONHandler.userExists(username, password));
        }

        void handleCreateUser(String username, String password) {

            User newUser = new User(username, password);
            newUser.setId(getFirstId(username));
            userList.add(user);

            UserJSONHandler.saveUserToFile(user);
        }

        void handleLogin(String username, String password) {

            int status = 0;
            boolean loginAccepted = checkCredentials(username, password);

            if (loginAccepted) {
                status = 1;
            }

            PduHandler.PDU_LOGIN_RESPONSE loginResponse = PduHandler.getInstance().create_login_response(status);

            if (loginAccepted) {
                smh.enqueuePDU(loginResponse);
                user = UserJSONHandler.getUserFromFile(username);
                userList.add(user);
            } else {
                smh.sendAndClose(loginResponse);
                smh = null;
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                System.out.println("login failed, exit true");
                exit = true;
            }
        }

        void handleInput(PDU incomingPDU) throws IllegalArgumentException {

                switch (incomingPDU.type) {

                    case PduHandler.MESSAGE_PDU: {
                        PduHandler.PDU_MESSAGE messagePDU = (PduHandler.PDU_MESSAGE)incomingPDU;

                        if (findThreadByName(messagePDU.target) != null) {
                            findThreadByName(messagePDU.target).getSmh().enqueuePDU(messagePDU);
                        } else {
                            System.out.println("ERROR: No user with name: " +
                                    messagePDU.target + " in thread list");
                        }
                        break;
                    }
                    case PduHandler.USERLIST_REQUEST_PDU: {
                        sendUserListPDU();
                        break;
                    }
                    case PduHandler.IS_LEAVING_PDU: {
                        System.out.println(user.getFullName() + " is leaving.");
                        userList.remove(user);
                        exit = true;
                        break;
                    }
                    case PduHandler.LOGIN_REQUEST_PDU: {
                        PduHandler.PDU_LOGIN loginPDU = (PduHandler.PDU_LOGIN)incomingPDU;
                        handleLogin(loginPDU.username, loginPDU.password);
                        break;
                    }
                    default: {
                        System.out.println("Received unknown pdu: " + incomingPDU.toString());
                        break;
                    }
                }
        }

        @Override
        public void run() {

            while (!exit) {

                //System.out.println("Thread is alive!!");
                if (!smh.getIncomingPDUQueue().isEmpty()) {
                    handleInput(smh.getIncomingPDUQueue().poll());
                }
            }
            System.out.println("Serverthread closing");
            serverThreads.remove(this);
        }
    }
}
