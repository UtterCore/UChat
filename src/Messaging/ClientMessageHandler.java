package Messaging;

import User.User;
import FileHandler.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMessageHandler extends MessageHandler {

    private ScheduledExecutorService updateExec;
    private User user;

    public ClientMessageHandler(User user, Socket sSocket) {
        super(sSocket);
        this.user = user;

        startIO(10, 10);
    }

    public void startUserListUpdater() {
        updateExec = Executors.newScheduledThreadPool(1);
        updateExec.scheduleAtFixedRate(updater, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void prepareAndSend(String input, String target) {
        PDU message_pdu;

        if (input.startsWith("/")) {
            message_pdu = PduHandler.getInstance().create_cmd_pdu(input.substring(1), user.getFullName());
        } else {
            message_pdu = PduHandler.getInstance().create_msg_pdu(input, user.getFullName(), target, false);
        }


        FileHandler.savePDUToFile(message_pdu, user.getFullName());

        enqueuePDU(message_pdu);
    }

    public void sendToMe(String input, String from) {
        enqueuePDU(PduHandler.getInstance().create_msg_pdu(input, from, from, false));
    }

    public void sendUserInfo() {
        //Messaging.PDU pdu = Messaging.PduHandler.getInstance().create_chatinfo_pdu(user.getUsername());
        PDU pdu = PduHandler.getInstance().create_login_pdu(user.getUsername(), "password");
        enqueuePDU(pdu);
    }

    public void sendIsLeaving() {
        PDU pdu = PduHandler.getInstance().create_is_leaving_pdu();

        SocketIO.sendPDU(writer, pdu);
    }

    private Runnable updater = new Runnable() {
        private void sendUserlistRequestPDU() {

            PduHandler.PDU_USERLIST_REQUEST pdu =
                    PduHandler.getInstance().create_userlist_requeust_pdu(user.getFullName());
            enqueuePDU(pdu);
        }
        @Override
        public void run() {

            if (writer != null) {
                sendUserlistRequestPDU();
            }
        }
    };
}
