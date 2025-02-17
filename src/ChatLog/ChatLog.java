package ChatLog;

import Messaging.PduHandler;

import java.util.LinkedList;
import java.util.Queue;

public class ChatLog {

    private String username;
    private String target;
    private Queue<PduHandler.PDU_MESSAGE> messageQueue;

    public ChatLog(String username) {
        this.username = username;
        messageQueue = new LinkedList<>();
    }

    public Queue<PduHandler.PDU_MESSAGE> getMessageQueue() {
        return messageQueue;
    }

    public String getUsername() {
        return username;
    }
}
