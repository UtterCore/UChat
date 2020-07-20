package Messaging;

import java.net.Socket;

public class ServerMessageHandler extends MessageHandler {

    public ServerMessageHandler(Socket socket) {
        super(socket);
        startIO(1, 10);
    }


}
