public class ClientMessageHandler {

    public static void prepareAndSend(ClientModel client, String input) {
        String preparedMessage;
        PDU message_pdu;

        if (input.startsWith("/")) {
            message_pdu = PduHandler.getInstance().create_cmd_pdu(input.substring(1), client.getUser().getFullName());
        } else {
            message_pdu = PduHandler.getInstance().create_msg_pdu(input, client.getUser().getFullName());
        }
        preparedMessage = message_pdu.toString();
        client.enqueuePDU(message_pdu);
        //client.handleInput(preparedMessage);
    }

    public static void sendSetTarget(ClientModel client, String target) {
        PDU setTargetPdu = PduHandler.getInstance().create_set_target_pdu(target);
        client.enqueuePDU(setTargetPdu);
    }
}
