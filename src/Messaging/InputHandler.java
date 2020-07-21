package Messaging;

public class InputHandler {

    public static ErrorMessage checkChatInput(String input) {

        if (input.length() == 0) {
            return ErrorMessage.CHATMESSAGE_EMPTY;
        }
        return ErrorMessage.INPUT_OK;
    }

    public static ErrorMessage checkUsername(String username) {
        if (username == null) {
            return ErrorMessage.USERNAME_EMPTY;
        }
        if (username.length() == 0) {
            return ErrorMessage.USERNAME_EMPTY;
        }

        if (username.length() > 12) {
            return ErrorMessage.USERNAME_TOO_LONG;
        }


        char usernameArray[] = username.toCharArray();
        for (int i = 0; i < username.length(); i++) {
            if (usernameArray[i] < 48) {
                return ErrorMessage.INVALID_CHARACTERS;
            }

            if (usernameArray[i] > 57 && usernameArray[i] < 65) {
                return ErrorMessage.INVALID_CHARACTERS;
            }

            if (usernameArray[i] > 90 && usernameArray[i] < 97) {
                return ErrorMessage.INVALID_CHARACTERS;
            }

            if (usernameArray[i] > 122) {
                return ErrorMessage.INVALID_CHARACTERS;
            }
        }

        return ErrorMessage.INPUT_OK;
    }

    public static String sanitizeString(String input) {

        return input;
    }
}
