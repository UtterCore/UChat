import java.util.ArrayList;

public class PduHandler {

    public static final int MESSAGE_PDU = 1;
    public static final int COMMAND_PDU = 2;
    public static final int CHATINFO_PDU = 3;
    public static final int USERLIST_PDU = 4;
    public static final int USERLIST_REQUEST_PDU = 5;
    public static final int SET_TARGET_PDU = 6;
    public static final int IS_LEAVING_PDU = 7;

    private static PduHandler pduHandler = new PduHandler();

    private PduHandler() {
    }

    public static PduHandler getInstance() {

        return pduHandler;
    }

    public PDU_MESSAGE create_msg_pdu(String message, String sender, String target) {
        System.out.println("creating msg pdu: " + message);
        return new PDU_MESSAGE(message, sender, target);
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


    public PDU parse_pdu(String input) {

        String parts[] = input.split(";");
        if (parts.length == 0 || parts.length == 1) {
            System.out.println("Invalid pdu");
            return null;
        } else {

            switch (Integer.parseInt(parts[0])) {
                case MESSAGE_PDU: {

                    return create_msg_pdu(parts[3], parts[1], parts[2]);
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

        private PDU_MESSAGE(String message, String sender, String target) {
            type = MESSAGE_PDU;
            if (sender == null) {
                sender = " ";
            }
            this.message = message;
            this.sender = sender;
            this.target = target;
        }

        @Override
        public String toString() {
            return type + ";" + sender + ";" + target + ";" + message;
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
            return type + "; hej";
        }
    }
}
