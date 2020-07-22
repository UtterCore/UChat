package ChatLog;

import FileHandler.FileHandler;
import Messaging.PduHandler;
import User.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChatLogHandler {

    private ArrayList<ChatLog> chatLogs;
    private User owner;
    public ChatLogHandler(User owner) {
        chatLogs = new ArrayList<>();
        this.owner = owner;
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

    private void addQueueToLog(ArrayList<PduHandler.PDU_MESSAGE> messageList, String chatPartner) {
        Queue<PduHandler.PDU_MESSAGE> messageQueue = new LinkedList<>(messageList);

        while (!messageQueue.isEmpty()) {
            addToLog(messageQueue.poll(), chatPartner);
        }
    }
    public void recreateLogsFromFile(ArrayList<String> userlist) {
        for (String user : userlist) {
            if (findLogByUsername(user) == null) {
                addQueueToLog(FileHandler.getMessages(owner.getFullName(), user), user);
            }
        }
    }

    public void addToLog(PduHandler.PDU_MESSAGE pdu, String chatPartner) {

        ChatLog log = findLogByUsername(chatPartner);

        if (log == null) {
            ChatLog newChatLog = new ChatLog(chatPartner);
            newChatLog.getMessageQueue().add(pdu);
            chatLogs.add(newChatLog);
        } else {
            log.getMessageQueue().add(pdu);
        }
    }

    public Queue<PduHandler.PDU_MESSAGE> getFullChatLog(String chatPartner) {
        ChatLog chatLog = findLogByUsername(chatPartner);

        if (chatLog == null) {
            return null;
        } else {
            return chatLog.getMessageQueue();
        }
    }

    public PduHandler.PDU_MESSAGE getFirstMessageFromLog(String chatPartner) {
        ChatLog chatLog = findLogByUsername(chatPartner);

        if (chatLog == null) {
            return null;
        } else {
            return chatLog.getMessageQueue().poll();
        }
    }

    public boolean hasMessages(String chatPartner) {
        if (findLogByUsername(chatPartner) == null) {
            return false;
        } else {
            return !findLogByUsername(chatPartner).getMessageQueue().isEmpty();
        }
    }

    public int getUnreadMessages(String chatPartner) {
        int unreadMessages = -1;

        if (findLogByUsername(chatPartner) != null) {
            Queue<PduHandler.PDU_MESSAGE> log = new LinkedList<>(findLogByUsername(chatPartner).getMessageQueue());
            unreadMessages = 0;
            while (!log.isEmpty()) {
                PduHandler.PDU_MESSAGE message = log.poll();
                if (!message.isRead) {
                    unreadMessages++;
                }
            }
        }

        return unreadMessages;
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
