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

        if (input.startsWith("/")) {
            isClientCommand = checkClientSideCommands(input.substring(1));
        }

        preparedMessage = input;
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
