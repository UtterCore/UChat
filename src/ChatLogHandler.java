import java.util.ArrayList;
import java.util.Queue;

public class ChatLogHandler {

    private ArrayList<ChatLog> chatLogs;
    public ChatLogHandler() {
        chatLogs = new ArrayList<>();
    }

    public void addToLogs(PduHandler.PDU_MESSAGE pdu) {

        boolean exists = false;
        for (ChatLog chatLog : chatLogs) {
            if (chatLog.getUsername().equals(pdu.sender)) {
                chatLog.getMessageQueue().add(pdu);
                exists = true;
                break;
            }
        }

        if (!exists) {
            ChatLog newChatLog = new ChatLog(pdu.sender);
            newChatLog.getMessageQueue().add(pdu);
            chatLogs.add(newChatLog);
        }
    }

    public void addToLog(PduHandler.PDU_MESSAGE pdu, String username) {
        boolean exists = false;
        for (ChatLog chatLog : chatLogs) {
            if (chatLog.getUsername().equals(username)) {
                chatLog.getMessageQueue().add(pdu);
                exists = true;
                break;
            }
        }

        if (!exists) {
            ChatLog newChatLog = new ChatLog(username);
            newChatLog.getMessageQueue().add(pdu);
            chatLogs.add(newChatLog);
        }
    }

    public Queue<PduHandler.PDU_MESSAGE> getFullChatLog(String username) {
        ChatLog chatLog = findLogByUsername(username);
        if (chatLog == null) {
            return null;
        } else {
            return chatLog.getMessageQueue();
        }
    }

    public PduHandler.PDU_MESSAGE getFirstMessageFromLog(String username) {
        ChatLog chatLog = findLogByUsername(username);

        if (chatLog == null) {
            return null;
        } else {
            return chatLog.getMessageQueue().poll();
        }
    }

    public boolean hasMessages(String username) {
        if (findLogByUsername(username) == null) {
            return false;
        } else {
            return !findLogByUsername(username).getMessageQueue().isEmpty();
        }
    }

    public int getUnreadMessages(String username) {
        if (findLogByUsername(username) == null) {
            return 0;
        } else {
            return findLogByUsername(username).getMessageQueue().size();
        }
    }
    private ChatLog findLogByUsername(String username) {
        for (ChatLog chatLog : chatLogs) {
            if (chatLog.getUsername().equals(username)) {
                return chatLog;
            }
        }
        return null;
    }
}
