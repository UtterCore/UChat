import java.util.ArrayList;

public class PDU_HANDLER {

    public PDU_MESSAGE create_msg_pdu(String message, String sender) {

        return new PDU_MESSAGE(message, sender);
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

    public PDU parse_pdu(String input) {

        String parts[] = input.split(";");
        if (parts.length == 0 || parts.length == 1) {
            System.out.println("Invalid pdu");
            return null;
        } else {

            switch (Integer.parseInt(parts[0])) {
                case 1: {
                 //   System.out.println("Message pdu found");

                    //create msg pdu

                    return create_msg_pdu(parts[2], parts[1]);
                }
                case 2: {
                   // System.out.println("Command pdu found");
                    return create_cmd_pdu(parts[2], parts[1]);
                }
                case 3: {

                 //   System.out.println("ChatInfo pdu found");
                    return create_chatinfo_pdu(parts[1]);
                }

                case 4: {
                    ArrayList<String> userlist = new ArrayList<>();

                    for (int i = 2; i < parts.length; i++) {
                        userlist.add(parts[i]);
                    }

                    return create_userlist_pdu(userlist);
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

        private PDU_MESSAGE(String message, String sender) {
            type = 1;
            if (sender == null) {
                sender = " ";
            }
            this.message = message;
            this.sender = sender;
        }

        @Override
        public String toString() {
            return type + ";" + sender + ";" + message;
        }
    }

    public class PDU_COMMAND extends PDU {

        public String command;
        public String sender;

        private PDU_COMMAND(String command, String sender) {
            type = 2;
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
            type = 3;
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
            type = 4;
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

}
