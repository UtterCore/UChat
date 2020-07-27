package Client;

import ChatLog.ChatLogHandler;
import FileHandler.FileHandler;
import Messaging.ClientMessageHandler;
import Messaging.PDU;
import Messaging.PduHandler;
import User.User;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

public class ClientModel {

    private User user;
    private Socket sSocket;
    private String chatPartner;
    private ChatLogHandler chatLogHandler;
    private ClientMessageHandler cmh;

    public ClientModel() {
    }

    public ChatLogHandler getChatLogHandler() {
        return chatLogHandler;
    }

    public Queue<PDU> getIncomingMessageQueue() {
        if (cmh != null) {
            return cmh.getIncomingPDUQueue();
        }
        return null;
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public void setChatPartner(String chatPartner) {
        this.chatPartner = chatPartner;
    }

    public void quit() {
        if (sSocket == null) {
            return;
        }
        try {
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOldMessages(String username) {
        ArrayList<PduHandler.PDU_MESSAGE> oldMessages = FileHandler.getMessages(user.getFullName(), username);

        for (PduHandler.PDU_MESSAGE oldPdu : oldMessages) {
            chatLogHandler.addToLog(oldPdu, username);
        }
    }

    public void getAllOldMessages(ArrayList<String> userlist) {
        chatLogHandler.recreateLogsFromFile(userlist);
    }

    public void updateChatLogs(ArrayList<String> users) {
        for (String user : users) {
            getOldMessages(user);
        }
    }

    public void shutDownConnection() {
        cmh.closeThreads();
        try {
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sSocket = null;
        chatLogHandler = null;
    }


    public boolean connectToChatServer(String username, String password, String address, int port) {
        createUser(username, null, password);

        try {
            sSocket = new Socket(InetAddress.getByName(address), port);

            cmh = new ClientMessageHandler(user, sSocket);
            cmh.sendUserInfo();

        } catch (IOException b) {
            System.out.println("wrong something");
            user = null;
            cmh = null;
            return false;
        }

        return true;
    }

    public boolean connectToChatServer(String username, String password, String email, String address, int port) {
        createUser(username, password, email);

        try {
            sSocket = new Socket(InetAddress.getByName(address), port);

            cmh = new ClientMessageHandler(user, sSocket);
            cmh.sendRegister();

        } catch (IOException b) {
            System.out.println("wrong something");
            user = null;
            cmh = null;
            return false;
        }

        return true;
    }

    public void killUser() {
        user = null;
    }
    public void login() {
        cmh.startUserListUpdater();
    }

    private void createUser(String username, String email, String password) {
        user = new User(username, email, password);
        chatLogHandler = new ChatLogHandler(user);
    }

    public User getUser() {
        return user;
    }

    public void sendMessage(String message, String target) {
        cmh.prepareAndSend(message, target);
    }

    /*
    public void setTarget(String username) {
        cmh.sendSetTarget(username);
    }
    */

    public void sendIsLeaving() {
        if (cmh != null) {
            cmh.sendIsLeaving();
            cmh = null;
        }
    }
}
