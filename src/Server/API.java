package Server;

import Messaging.PduHandler;
import Server.Webserver.Response;
import Server.Webserver.Webs;
import User.User;

import java.util.ArrayList;

public class API {
    private ServerModel model;
    public API(ServerModel model) {
        this.model = model;
    }

    public void parseRequest(String request, ServerModel.ServerThread thread) {

        String parts[] = request.split(" ");

        switch (parts[0]) {

            case "GET": {

                switch (Webs.getInstance().getResource(request)) {
                    case "/userlist": {

                        ArrayList<String> userlistString = new ArrayList<>();
                        for (User user : model.getUserList()) {
                            userlistString.add(user.getFullName());
                        }

                        PduHandler.PDU_RESOURCE_RESPONSE responsePDU = PduHandler.getInstance().create_resource_response_pdu();

                        responsePDU.response = new Response();
                        responsePDU.response.setStatus("200 OK");
                        responsePDU.response.setFileType("application/json");
                        responsePDU.response.setBody(PduHandler.getInstance().create_userlist_pdu(userlistString).toJSON().toString());
                        responsePDU.response.setLength(model.getUserListString().length());
                        thread.getSmh().sendHTTPResponse(responsePDU);

                        break;
                    }
                }
                break;
            }
            case "POST": {

                break;
            }
        }
    }
}
