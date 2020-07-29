package Messaging;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

public class PduHandler {

    public static final int MESSAGE_PDU = 1;
    public static final int COMMAND_PDU = 2;
    public static final int CHATINFO_PDU = 3;
    public static final int USERLIST_PDU = 4;
    public static final int USERLIST_REQUEST_PDU = 5;
    public static final int SET_TARGET_PDU = 6;
    public static final int IS_LEAVING_PDU = 7;
    public static final int LOGIN_REQUEST_PDU = 8;
    public static final int CREATE_USER_REQUEST_PDU = 9;
    public static final int LOGIN_RESPONSE_PDU = 10;
    public static final int CREATE_USER_RESPONSE_PDU = 11;
    public static final int IMAGE_MESSAGE_PDU = 12;

    private static PduHandler pduHandler = new PduHandler();

    private PduHandler() {
    }

    public static PduHandler getInstance() {

        return pduHandler;
    }

    public PDU_MESSAGE create_msg_pdu(String message, String sender, String target, boolean isRead) {
        return new PDU_MESSAGE(message, sender, target, isRead);
    }

    public PDU_COMMAND create_cmd_pdu(String command, String sender) {

        return new PDU_COMMAND(command, sender);
    }

    public PDU_CHATINFO create_chatinfo_pdu(String chatPartner) {

        return new PDU_CHATINFO(chatPartner);
    }

    public PDU_USERLIST create_userlist_pdu(ArrayList<String> userlist) {
        return new PDU_USERLIST(userlist);
    }

    public PDU_USERLIST_REQUEST create_userlist_requeust_pdu(String sender) {
        return new PDU_USERLIST_REQUEST(sender);
    }

    public PDU_SET_TARGET create_set_target_pdu(String target) {
        return new PDU_SET_TARGET(target);
    }

    public PDU_IS_LEAVING create_is_leaving_pdu() {
       return new PDU_IS_LEAVING();
    }

    public PDU_LOGIN create_login_pdu(String username, String password) {
        return new PDU_LOGIN(username, password);
    }

    public PDU_CREATE_USER create_create_user_pdu(String username, String email, String password) {
        return new PDU_CREATE_USER(username, email, password);
    }

    public PDU_LOGIN_RESPONSE create_login_response(int status) {
        return new PDU_LOGIN_RESPONSE(status);
    }

    public PDU_CREATE_USER_RESPONSE create_cr_user_response(int status) {
        System.out.println("creating user response: " + status);
        return new PDU_CREATE_USER_RESPONSE(status);
    }

    public PDU_IMAGE_MESSAGE create_img_msg_pdu(byte[] imageData, String sender, String target, boolean isRead) {
        return new PDU_IMAGE_MESSAGE(imageData, sender, target, isRead);
    }


    public PDU parse_pdu(String input) {

        String parts[] = input.split(";");
        if (parts.length == 0 || parts.length == 1) {
            System.out.println("Received unparseable pdu: " + input);
            return null;
        } else {

            switch (Integer.parseInt(parts[0])) {
                case MESSAGE_PDU: {

                    return create_msg_pdu(parts[4], parts[1], parts[2], Boolean.parseBoolean(parts[3]));
                }
                case COMMAND_PDU: {
                   // System.out.println("Command pdu found");
                    return create_cmd_pdu(parts[2], parts[1]);
                }
                case CHATINFO_PDU: {

                 //   System.out.println("ChatInfo pdu found");
                    return create_chatinfo_pdu(parts[1]);
                }

                case USERLIST_PDU: {
                    ArrayList<String> userlist = new ArrayList<>();

                    for (int i = 2; i < parts.length; i++) {
                        userlist.add(parts[i]);
                    }

                    return create_userlist_pdu(userlist);
                }

                case USERLIST_REQUEST_PDU: {

                    return create_userlist_requeust_pdu(parts[1]);
                }

                case SET_TARGET_PDU: {

                    return create_set_target_pdu(parts[1]);
                }
                case IS_LEAVING_PDU: {
                    return create_is_leaving_pdu();
                }
                case LOGIN_REQUEST_PDU: {
                    return create_login_pdu(parts[1], parts[2]);
                }
                case CREATE_USER_REQUEST_PDU: {
                    return create_create_user_pdu(parts[1], parts[2], parts[3]);
                }
                case LOGIN_RESPONSE_PDU: {
                    return create_login_response(Integer.parseInt(parts[1]));
                }
                case CREATE_USER_RESPONSE_PDU: {
                    return create_cr_user_response(Integer.parseInt(parts[1]));
                }
                case IMAGE_MESSAGE_PDU: {
                    return create_img_msg_pdu(parts[4].getBytes(), parts[1], parts[2], Boolean.parseBoolean(parts[3]));
                }
                default: {
                    System.out.println("Invalid pdu type??");

                    return null;
                }
            }
        }
    }

    public class PDU_MESSAGE extends PDU {

        public String message;
        public String sender;
        public String target;
        public boolean isRead;

        private PDU_MESSAGE(String message, String sender, String target, boolean isRead) {
            type = MESSAGE_PDU;
            if (sender == null) {
                sender = " ";
            }
            this.message = message;
            this.sender = sender;
            this.target = target;
            this.isRead = isRead;
        }

        @Override
        public String toString() {
            return type + ";" + sender + ";" + target + ";" + String.valueOf(isRead) + ";" + message;
        }
    }

    public class PDU_COMMAND extends PDU {

        public String command;
        public String sender;

        private PDU_COMMAND(String command, String sender) {
            type = COMMAND_PDU;
            this.command = command;
            this.sender = sender;
        }

        @Override
        public String toString() {
            return type + ";" + sender + ";" + command;
        }
    }

    public class PDU_CHATINFO extends PDU {

        public String chatPartner;

        private PDU_CHATINFO(String chatPartner) {
            type = CHATINFO_PDU;
            this.chatPartner = chatPartner;
        }

        @Override
        public String toString() {
            return type + ";" + chatPartner;
        }
    }

    public class PDU_USERLIST extends PDU {

        public int nrOfUsers;
        public ArrayList<String> usernames;

        private PDU_USERLIST(ArrayList<String> usernames) {
            type = USERLIST_PDU;
            this.nrOfUsers = usernames.size();
            this.usernames = usernames;
        }

        @Override
        public String toString() {
            String userlistString = "";
            for (String user : usernames) {
                userlistString += user + ";";
            }

            return type + ";" + nrOfUsers + ";" + userlistString;
        }
    }

    public class PDU_USERLIST_REQUEST extends PDU {

        public String sender;

        private PDU_USERLIST_REQUEST(String sender) {
            type = USERLIST_REQUEST_PDU;
            this.sender = sender;
        }

        @Override
        public String toString() {
            return String.valueOf(USERLIST_REQUEST_PDU) + ";" + sender;
        }
    }

    public class PDU_SET_TARGET extends PDU {

        public String target;

        private PDU_SET_TARGET(String target) {
            type = SET_TARGET_PDU;
            this.target = target;
        }

        @Override
        public String toString() {
            return type + ";" + target;
        }
    }

    public class PDU_IS_LEAVING extends PDU {

        private PDU_IS_LEAVING() {
            type = IS_LEAVING_PDU;
        }

        @Override
        public String toString() {
            return type + "; null";
        }
    }

    public class PDU_LOGIN extends PDU {
        public String username;
        public String password;

        private PDU_LOGIN(String username, String password) {
            type = LOGIN_REQUEST_PDU;
            this.username = username;
            this.password = password;
        }

        @Override
        public String toString() {
            return type + ";" + username + ";" + password;
        }
    }

    public class PDU_CREATE_USER extends PDU {
        public String username;
        public String email;
        public String password;

        private PDU_CREATE_USER(String username, String email, String password) {
            type = CREATE_USER_REQUEST_PDU;
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        public String toString() {
            return type + ";" + username + ";" + email + ";" + password;
        }
    }

    public class PDU_LOGIN_RESPONSE extends PDU {
        public int status;

        private PDU_LOGIN_RESPONSE(int status) {
            type = LOGIN_RESPONSE_PDU;
            this.status = status;
        }

        @Override
        public String toString() {
            return type + ";" + status;
        }
    }

    public class PDU_CREATE_USER_RESPONSE extends PDU {
        public int status;

        private PDU_CREATE_USER_RESPONSE(int status) {
            type = CREATE_USER_RESPONSE_PDU;
            this.status = status;
        }

        @Override
        public String toString() {
            return type + ";" + status;
        }
    }

    public class PDU_IMAGE_MESSAGE extends PDU {

        public byte[] imageData;
        public String sender;
        public String target;
        public boolean isRead;

        private PDU_IMAGE_MESSAGE(byte[] imageData, String sender, String target, boolean isRead) {
            type = IMAGE_MESSAGE_PDU;
            if (sender == null) {
                sender = " ";
            }
            this.imageData = imageData;
            this.sender = sender;
            this.target = target;
            this.isRead = isRead;
        }

        @Override
        public String toString() {
            return type + ";" + sender + ";" + target + ";" + String.valueOf(isRead) + ";" + imageData;
        }
    }
}
