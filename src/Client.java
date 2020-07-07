import javafx.application.Application;
import javafx.stage.Stage;

        /*
        TODO: JavaFX
        TODO: Buttons for sending/reponding to chat requests
        TODO: Friend list
        TODO: Make outputqueue for server
        TODO: Save chats locally (JSON)!
        TODO: (later) Save user data with SQL
         */


public class Client extends Application {


    public static void main(String args[]) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new ClientController(new GUIFX(primaryStage));
    }
}
