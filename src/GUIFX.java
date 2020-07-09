
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GUIFX {

    private Stage stage;
    private Stage friendlistStage;

    private Button userSubmitButton;
    private Button chatSubmitButton;
    private TextField userTextField;
    private Text actiontarget;


    private TextField chatField;
    private TextArea chatArea;
    private TextArea fListArea;

    private VBox friendsBox;
    private VBox FLBox;

    private Label chattingWith;

    public GUIFX(Stage stage) {

        this.stage = stage;
    }

    public Button getUserSubmitButton() {
        return userSubmitButton;
    }

    public Button getChatSubmitButton() {
        return chatSubmitButton;
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

        userSubmitButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(userSubmitButton);
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

    public void openChatWith(String username) {
        System.out.println("open chat with");
        chattingWith.setText("");
        chattingWith.setText(username);
    }
    public void showChat() {


        stage.setTitle("UChat");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        chattingWith = new Label("Nobody");
        grid.add(chattingWith, 1, 0);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        grid.add(chatArea, 1, 1);

        chatField = new TextField();
        chatField.setFocusTraversable(false);
        grid.add(chatField, 1, 2);

        chatSubmitButton = new Button("Send");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(chatSubmitButton);
        grid.add(hbBtn, 1, 3);

        chatField.requestFocus();

        Scene chatScene = new Scene(grid,400, 300);
        stage.setScene(chatScene);

    }


    public VBox buildFriendlistItem(String name, int unreadMessages) {
        VBox friendlistItem = new VBox(5);
        if (unreadMessages == 0) {
            friendlistItem.getChildren().add(new Label(name));
        } else {
            friendlistItem.getChildren().add(new Label(name + " (" + unreadMessages + ")"));
        }
        friendlistItem.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        friendlistItem.setPadding(new Insets(0, 10, 0, 10));

        friendlistItem.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    System.out.println("click on " + name);
                }
            }
        });

        return friendlistItem;
    }
    public void showFriendlist() {
        friendlistStage = new Stage();
        friendlistStage.setTitle("UChat - Friends");

        FLBox = new VBox();
        FLBox.setPadding(new Insets(20, 0, 20, 0));

        friendsBox = new VBox();

        FLBox.getChildren().add(new Label("Friends"));
        FLBox.getChildren().add(friendsBox);

        Scene friendlistScene = new Scene(FLBox, 200, 300);
        friendlistStage.setScene(friendlistScene);
        friendlistStage.show();
    }

    public void updateFriendlist(ArrayList<String> friends) {


        friendsBox.getChildren().clear();
    }

    public VBox addToFriendList(String username, int unreadMessages) {
        VBox newFriendItem = buildFriendlistItem(username, unreadMessages);

        friendsBox.getChildren().add(newFriendItem);
        return newFriendItem;
    }
}
