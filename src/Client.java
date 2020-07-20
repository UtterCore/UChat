import GUI.GUIFX;
import javafx.application.Application;
import javafx.stage.Stage;
import Client.*;

        /*
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
