import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.BindException;
import java.net.ConnectException;
import java.util.Scanner;

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
