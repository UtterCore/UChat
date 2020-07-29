package Messaging;

import java.util.ArrayList;

public enum ErrorMessage {

    INPUT_OK(1, "Input OK"),
    USERNAME_EMPTY(2, "Username is empty"),
    INVALID_CHARACTERS(3, "Username contains special characters"),
    USERNAME_TOO_LONG(4, "Username is too long"),
    CHATMESSAGE_EMPTY(5, "Message is empty"),
    WRONG_CREDENTIALS(6, "Wrong username or password"),

    CR_USERNAME_ALREADY_EXISTS(7, "Username already exists"),
    CR_USERNAME_EMPTY(8, "Username is empty"),
    CR_USERNAME_TOO_LONG(9, "Username is too long"),
    CR_USERNAME_SPECIAL_CHARACTERS(10, "Username contains special characters"),
    CR_PASSWORD_INVALID(11, "Invalid password"),
    CR_PASSWORD_DOES_NOT_MATCH(12, "The passwords do not match"),
    CR_INVALID_MAIL(13, "Invalid email"),

    CR_SUCCESS(14, "User created!"),
    CR_ALREADY_LOGGED_IN(15, "This user is already logged in");

    private int messageId;
    private String errorMessage;
    private ArrayList<ErrorMessage> errorMessages = new ArrayList<>();

    ErrorMessage(int messageId, String errorMessage) {
        this.messageId = messageId;
        this.errorMessage = errorMessage;
        errorMessages.add(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
