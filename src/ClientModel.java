import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;

public class ClientModel {

    private User user;
    private Socket sSocket;
    private String chatPartner;
    private ChatLogHandler chatLogHandler;
    private ClientMessageHandler cmh;

    public ClientModel() {
        chatLogHandler = new ChatLogHandler();
    }

    public ChatLogHandler getChatLogHandler() {
        return chatLogHandler;
    }

    public Queue<PDU> getIncomingMessageQueue() {
        return cmh.getIncomingPDUQueue();
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public void setChatPartner(String chatPartner) {
        this.chatPartner = chatPartner;
    }

    public void quit() {
        try {
            sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //writer.close();
    }

    public void connectToServer(String address, int port) throws IOException {
        sSocket = new Socket(InetAddress.getByName(address), port);

        cmh = new ClientMessageHandler(user, sSocket);
        cmh.startIO();
        cmh.sendUserInfo();
    }

    public boolean connectToChatServer(String username, String globalAddress, String localAddress, int port) {

        createUser(username);

        try {
            //try global
            connectToServer(globalAddress, port);
        } catch (ConnectException b) {

            //try local
            cmh.sendToMe("No response. Trying to access locally...", null);
            try {
                connectToServer(localAddress, port);
            } catch (IOException io) {
                cmh.sendToMe("No response from chat server. Shutting down", null);
                user = null;
                return false;
            }
        } catch (IOException e) {
             e.printStackTrace();
        }


        return true;
    }

    private void createUser(String username) {
        user = new User(username, 0);
    }

    public User getUser() {
        return user;
    }

    public void sendMessage(String message) {
        cmh.prepareAndSend(message);
    }

    public void setTarget(String username) {
        cmh.sendSetTarget(username);
    }

    public void sendIsLeaving() {
        cmh.sendIsLeaving();
    }
}
