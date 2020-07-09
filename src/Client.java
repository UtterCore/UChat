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


    ClientController controller;

    public static void main(String args[]) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        controller = new ClientController(new GUIFX(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop!");
        controller.quit();
    }
}
