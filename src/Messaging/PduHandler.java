package Messaging;

import Server.Webserver.Response;
import Server.Webserver.Webs;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;

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
    public static final int RESOURCE_RESPONSE_PDU = 13;
    public static final int RESOURCE_REQUEST_PDU = 14;

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
        return new PDU_CREATE_USER_RESPONSE(status);
    }

    public PDU_IMAGE_MESSAGE create_img_msg_pdu(byte[] imageData, String sender, String target, boolean isRead) {
        return new PDU_IMAGE_MESSAGE(imageData, sender, target, isRead);
    }

    public PDU_RESOURCE_RESPONSE create_resource_response_pdu() {
        return new PDU_RESOURCE_RESPONSE();
    }
    public PDU_RESOURCE_REQUEST create_resource_request_pdu(String resource) {
        return new PDU_RESOURCE_REQUEST(resource);
    }

    private PDU parse_json(String input) {

        JSONParser parser = new JSONParser();

        Object o;
        try {
            o = parser.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject incomingJSON = (JSONObject)o;

        if (incomingJSON.get("type") == null) {
            System.out.println("Received unparseable pdu: " + input);
            return null;
        } else {

            int type = Integer.parseInt("" + incomingJSON.get("type"));
            switch (type) {
                case MESSAGE_PDU: {

                    return create_msg_pdu((String)incomingJSON.get("message"), (String)incomingJSON.get("sender"), (String)incomingJSON.get("target"), false);
                }
                case COMMAND_PDU: {
                    // System.out.println("Command pdu found");
                    return create_cmd_pdu((String)incomingJSON.get("command"), (String)incomingJSON.get("sender"));
                }
                case CHATINFO_PDU: {

                    //   System.out.println("ChatInfo pdu found");
                    return create_chatinfo_pdu((String)incomingJSON.get("chatPartner"));
                }

                case USERLIST_PDU: {
                    ArrayList<String> userlist = new ArrayList<>();

                    JSONArray userArray = (JSONArray)incomingJSON.get("userlist");
                    ArrayList<String> al = new ArrayList(userArray);
                    for (String username : al) {
                        userlist.add(username);
                    }

                    return create_userlist_pdu(userlist);
                }

                case USERLIST_REQUEST_PDU: {

                    return create_userlist_requeust_pdu((String)incomingJSON.get("sender"));
                }

                case SET_TARGET_PDU: {

                    return create_set_target_pdu((String)incomingJSON.get("target"));
                }
                case IS_LEAVING_PDU: {
                    return create_is_leaving_pdu();
                }
                case LOGIN_REQUEST_PDU: {
                    return create_login_pdu((String)incomingJSON.get("username"), (String)incomingJSON.get("password"));
                }
                case CREATE_USER_REQUEST_PDU: {
                    return create_create_user_pdu((String)incomingJSON.get("username"), (String)incomingJSON.get("password"), (String)incomingJSON.get("email"));
                }
                case LOGIN_RESPONSE_PDU: {
                    return create_login_response(Integer.parseInt("" + incomingJSON.get("status")));
                }
                case CREATE_USER_RESPONSE_PDU: {
                    return create_cr_user_response(Integer.parseInt("" + incomingJSON.get("status")));
                }
                case IMAGE_MESSAGE_PDU: {
                    return create_img_msg_pdu(((String)incomingJSON.get("imageData")).getBytes(), (String)incomingJSON.get("sender"), (String)incomingJSON.get("target"), false);
                }
                default: {
                    System.out.println("Invalid pdu type??");

                    return null;
                }
            }
        }
    }


    private PDU parse_get(String input) {

        System.out.println("Received GET");
        String parts[] = input.split(" ");
        return create_resource_request_pdu(input);
    }

    private PDU parse_post(String input) {
        PDU parsedPDU = null;

        return parsedPDU;
    }

    public PDU parse_pdu(String input) {

        //System.out.println("Input: " + input);

        String parts[] = input.split(" ");

        switch (parts[0]) {
            case "GET": {
                return parse_get(input);
            }
            case "POST": {
                return parse_post(input);
            }
            default: {
                return parse_json(input);
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("message", message);
            jsonObject.put("sender", sender);
            jsonObject.put("target", target);
            jsonObject.put("isRead", isRead);

            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("command", command);
            jsonObject.put("sender", sender);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("chatPartner", chatPartner);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("nrOfUsers", nrOfUsers);

            JSONArray jsonArray = new JSONArray();
            for (String user : usernames) {
                jsonArray.add(user);
            }

            jsonObject.put("userlist", jsonArray);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("sender", sender);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("target", target);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("status", status);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("status", status);
            return jsonObject;
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

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("imageData", imageData);
            jsonObject.put("sender", sender);
            jsonObject.put("target", target);
            jsonObject.put("isRead", isRead);
            return jsonObject;
        }
    }

    public class PDU_RESOURCE_RESPONSE extends PDU {

        public Response response;

        public PDU_RESOURCE_RESPONSE() {
            type = RESOURCE_RESPONSE_PDU;
        }
        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("body", response.getBody());
            return jsonObject;
        }

        public String toHTTP() {
            return response.toHTTP();
        }
    }

    public class PDU_RESOURCE_REQUEST extends PDU {

        public String resource;

        public PDU_RESOURCE_REQUEST(String resource) {
            type = RESOURCE_REQUEST_PDU;
            this.resource = resource;
        }
        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resource", resource);
            return jsonObject;
        }
    }
}
