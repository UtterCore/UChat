import GUI.GUIFX;
import Server.Webserver.Webserver;
import javafx.application.Application;
import javafx.stage.Stage;
import Client.*;

import java.io.IOException;

        /*
        TODO: IMG: Change all (or some) PDUs to JSON to be able to send files (yikes)
        TODO: Find out why the CPU goes to 100 when serving to a few http clients

        TODO: Timeout for server threads (if a user crashes etc)
        TODO: Unread message indicator
        TODO: Sanitize input from ";" (login, chat etc)
        TODO: Create/log in to old account
        TODO: (later) Save user data with SQL
         */


public class Client extends Application {


    private ClientController controller;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        controller = new ClientController(new GUIFX(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        controller.quit();
    }
}
