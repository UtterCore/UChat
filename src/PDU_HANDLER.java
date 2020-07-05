
public class PDU_HANDLER {

    public PDU_MESSAGE create_msg_pdu(String message, String sender) {

        return new PDU_MESSAGE(message, sender);
    }

    public class PDU_MESSAGE extends PDU {

        private String message;
        private String sender;

        private PDU_MESSAGE(String message, String sender) {
            type = 1;
            this.message = message;
            this.sender = sender;
        }
    }

}
