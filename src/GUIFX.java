import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;

public class GUIFX {

    private Stage stage;
    private Button submitButton;
    private TextField userTextField;
    private Text actiontarget;


    private TextField chatField;
    private TextArea chatArea;

    public GUIFX(Stage stage) {

        this.stage = stage;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public TextField getUserTextField() {
        return userTextField;
    }

    public TextField getChatField() {
        return chatField;
    }


    public void showUsernameError() {
        actiontarget.setFill(Color.FIREBRICK);
        actiontarget.setText("Invalid username");
    }

    public void showConnectionError() {
        actiontarget.setFill(Color.FIREBRICK);
        actiontarget.setText("Could not connect to chat server");
    }

    public void clearError() {
        actiontarget.setText("");
        actiontarget.setFill(Color.BLACK);
    }

    public void showConnecting() {
        actiontarget.setText("Connecting...");
        actiontarget.setFill(Color.BLACK);
    }
    public void showLogin() {

        Stage primaryStage = stage;

        primaryStage.setTitle("UChat Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        actiontarget = new Text();
        grid.add(actiontarget, 1, 2);

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("User name: ");
        grid.add(userNameLabel, 0, 1);

        userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        submitButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submitButton);
        grid.add(hbBtn, 1, 3);


        Scene scene = new Scene(grid, 300, 275);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public TextArea getChatArea() {
        return chatArea;
    }

    public void addTextToChat(String text) {
        chatArea.appendText(text);
    }

    public void clearTextField() {
        chatField.setText("");
    }
    public void showChat() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        chatArea = new TextArea();
        chatArea.setEditable(false);
        grid.add(chatArea, 1, 1);

        chatField = new TextField();
        grid.add(chatField, 1, 2);

        submitButton = new Button("Send");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submitButton);
        grid.add(hbBtn, 1, 3);

        chatField.requestFocus();

        Scene chatScene = new Scene(grid,400, 300);
        stage.setScene(chatScene);

    }

    public void showFXML() {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("ChatLayout.fxml"));

        try {
            VBox box = loader.<VBox>load();
            stage.setScene(new Scene(box, 400, 400));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
