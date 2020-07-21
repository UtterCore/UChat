package Messaging;

public enum ErrorMessage {

    INPUT_OK(1, "Input OK"),
    USERNAME_EMPTY(2, "Username is empty"),
    INVALID_CHARACTERS(3, "Username contains special characters"),
    USERNAME_TOO_LONG(4, "Username is too long"),
    CHATMESSAGE_EMPTY(5, "Message is empty");

    private int messageId;
    private String errorMessage;

    ErrorMessage(int messageId, String errorMessage) {
        this.messageId = messageId;
        this.errorMessage = errorMessage;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
