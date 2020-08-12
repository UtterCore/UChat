package Server;

import Messaging.PduHandler;
import Messaging.SocketIO;
import Server.Webserver.Response;
import Server.Webserver.Webs;
import User.User;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.ArrayList;

public class API {
    private ServerModel model;
    public API(ServerModel model) {
        this.model = model;
    }

    public void handleRequest(String request, ServerModel.ServerThread thread) {

        String parts[] = request.split(" ");

        switch (parts[0]) {

            case "GET": {

                PduHandler.PDU_RESOURCE_RESPONSE responsePDU = PduHandler.getInstance().create_resource_response_pdu();

                if (Webs.getInstance().isWebpage(Webs.getInstance().getResource(request))) {
                    try {
                        responsePDU.response = Webs.getInstance().parseRequest(request);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {



                    switch (Webs.getInstance().getResource(request)) {
                        case "/usersonline": {

                            ArrayList<String> userlistString = new ArrayList<>();
                            for (User user : model.getUserList()) {
                                userlistString.add(user.getFullName());
                            }

                            if (thread.getUser() != null) { // TODO: friend list here later
                                thread.handleInput(PduHandler.getInstance().create_userlist_requeust_pdu(thread.getUser().getFullName()));
                            } else {
                                System.out.println("Not allowed");
                                thread.handleInput(PduHandler.getInstance().create_userlist_requeust_pdu(null));
                                return;
                            }
                            break;
                        }

                        case "/allusers": {

                            ArrayList<String> userlistString = new ArrayList<>();
                            for (User user : model.getAllUsers()) {
                                userlistString.add(user.getFullName());
                            }

                            responsePDU.response = new Response();
                            responsePDU.response.setStatus("200 OK");
                            responsePDU.response.setFileType("application/json");
                            responsePDU.response.setBody(PduHandler.getInstance().create_userlist_pdu(userlistString).toJSON().toString());
                            responsePDU.response.setLength(model.getUserListString().length());

                            break;
                        }
                        default: {
                            try {
                                responsePDU.response = Webs.getInstance().parseResource("404.html");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
                thread.getSmh().sendHTTPResponse(responsePDU);
                break;
            }
            case "POST": {

                String p[] = request.split("\n");
                String json = p[p.length - 1];
                //System.out.println("Received POST: " + json);

                //Handle the input received from post
                thread.handleInput(PduHandler.getInstance().parse_pdu(json));
                break;
            }
        }
    }
}
