import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientModel {

    private User user;
    private Socket sSocket;
    private PrintWriter writer;
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
        writer.close();
    }
    void handleInput(String input) {

        if (writer == null) {
            System.out.println("Server dead hehe");
            return;
        }

        writer.print(input);
        writer.flush();
    }



    public void connectToServer(String address, int port) throws IOException {
        sSocket = new Socket(InetAddress.getByName(address), port);

        writer = new PrintWriter(sSocket.getOutputStream());

        cmh = new ClientMessageHandler(writer, user);
        cmh.startInputThread(sSocket);
        cmh.sendUserInfo();
    }

    public boolean connectToChatServer(String username, String globalAddress, String localAddress, int port) {

        createUser(username);

        try {
            //try global
            connectToServer(globalAddress, port);
        } catch (ConnectException b) {

            //try local
            cmh.addToIncoming(PduHandler.getInstance().create_msg_pdu("No response. Trying to access locally...", null));

            try {
                connectToServer(localAddress, port);
            } catch (IOException io) {
                cmh.addToIncoming(PduHandler.getInstance().create_msg_pdu("No response from the chat server. Shutting down.", null));
                user = null;
                return false;
            }
        } catch (IOException e) {
            // e.printStackTrace();
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

    //TODO: Kläm in i cmh på någgot sätt

}
