public class ClientMessageHandler {

    private ClientModel client;
    private GUI gui;

    public ClientMessageHandler(ClientModel client, GUI gui) {
        this.client = client;
        this.gui = gui;
    }

    public void prepareAndSend(String input) {
        String preparedMessage = null;
        boolean isClientCommand = false;

        PDU message_pdu;

        if (input.startsWith("/")) {
            isClientCommand = checkClientSideCommands(input.substring(1));
            message_pdu = PduHandler.getInstance().create_cmd_pdu(input.substring(1), client.getUser().getFullName());
        } else {
            message_pdu = PduHandler.getInstance().create_msg_pdu(input, client.getUser().getFullName());
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
                gui.clearChat();
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
