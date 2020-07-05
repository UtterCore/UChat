public class ClientMessageHandler {

    private ClientModel client;
    private GUI gui;
    private PDU_HANDLER pdu_handler;

    public ClientMessageHandler(ClientModel client, GUI gui) {
        this.client = client;
        this.gui = gui;
        pdu_handler = new PDU_HANDLER();
    }

    public void prepareAndSend(String input) {
        String preparedMessage = null;
        boolean isClientCommand = false;

        PDU message_pdu;

        if (input.startsWith("/")) {
            isClientCommand = checkClientSideCommands(input.substring(1));
            message_pdu = pdu_handler.create_cmd_pdu(input.substring(1), client.getUser().getFullName());
        } else {
            message_pdu = pdu_handler.create_msg_pdu(input, client.getUser().getFullName());
        }
        preparedMessage = message_pdu.toString();

        if (!isClientCommand) {
            client.handleInput(preparedMessage);
        }
    }

    private boolean checkClientSideCommands(String input) {
        switch (input) {
            case "clear": {
                System.out.println("empty");
                gui.emptyMessageArea();
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
