package Client;

import ChatLog.ChatLogHandler;
import FileHandler.FileHandler;
import Messaging.ClientMessageHandler;
import Messaging.PDU;
import Messaging.PduHandler;
import User.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private ArrayList<String> currentUserlist;

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

    public ArrayList<String> getCurrentUserlist() {
        return currentUserlist;
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

    public void resetFriendlist() {
        currentUserlist = null;
    }
    public boolean updateFriendList(ArrayList<String> userlist) {
        if (currentUserlist == null) {
            currentUserlist = new ArrayList<>(userlist);
            return true;
        } else {
            if (currentUserlist.equals(userlist)) {
                //no update
                return false;
            }
        }
        currentUserlist = new ArrayList<>(userlist);
        return true;
    }

    public void loadMessages(String username) {
        ArrayList<PduHandler.PDU_MESSAGE> oldMessages = FileHandler.getMessages(user.getFullName(), username);

        for (PduHandler.PDU_MESSAGE oldPdu : oldMessages) {
            chatLogHandler.addToLog(oldPdu, username);
        }
    }

    public void saveMessage(PDU message) {
        FileHandler.savePDUToFile(message, getUser().getFullName());
    }

    public void getAllOldMessages(ArrayList<String> userlist) {
        chatLogHandler.recreateLogsFromFile(userlist);
    }

    public void updateChatLogs(ArrayList<String> users) {
        for (String user : users) {
            loadMessages(user);
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

    public void sendFile(String filename, String target) {
        BufferedImage image = null;

        try {

            image = ImageIO.read(new File(filename));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);

            byte[] bytes = baos.toByteArray();

            //ImageIO.write(image, "jpg", new File("./nyhund.jpg"));
            cmh.prepareAndSend(bytes, target);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewImage(byte[] data) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("output.jpg") );
        System.out.println("image created");
    }

    public void sendIsLeaving() {
        if (cmh != null) {
            cmh.sendIsLeaving();
            cmh = null;
        }
    }
}
