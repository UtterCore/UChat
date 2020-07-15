package GUI;

import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

public class GUIFX {

    public static final int LOGIN_FAILED = 1;
    public static final int WRONG_CREDENTIALS = 2;

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

    Stage chatStage;

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

    public void showLoginError(int errorType) {
        switch (errorType) {
            case WRONG_CREDENTIALS: {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Wrong username or password");
                break;
            }
        }
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
        grid.getStyleClass().add("friendlist_background");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        actiontarget = new Text();
        grid.add(actiontarget, 1, 2);

        Text scenetitle = new Text("Welcome");
        scenetitle.setFill(Color.LIGHTBLUE);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("User name: ");
        userNameLabel.setTextFill(Color.WHITE);
        grid.add(userNameLabel, 0, 1);

        userTextField = new TextField();
        userTextField.getStyleClass().add("chat_textarea");
        grid.add(userTextField, 1, 1);

        userSubmitButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(userSubmitButton);
        grid.add(hbBtn, 1, 3);


        Scene scene = new Scene(grid, 300, 275);


        scene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void lockLoginScreen() {
        userSubmitButton.setDisable(true);
        userTextField.setDisable(true);
    }

    public void unlockLoginScreen() {
        userSubmitButton.setDisable(false);
        userTextField.setDisable(false);
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
        clearChat();
        chattingWith.setText(username);
    }

    public void setChatCloseEvent(EventHandler<WindowEvent> event) {
        chatStage.setOnCloseRequest(event);
    }
    public void hideLogin() {
        stage = null;
    }
    public void showChat(String username) {


        if (chatStage != null) {
            chatStage.close();
            chatStage = null;
        }
        chatStage = new Stage();


        chatStage.setTitle("UChat");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("friendlist_background");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        chattingWith = new Label(username);
        chattingWith.setTextFill(Color.LIGHTBLUE);
        grid.add(chattingWith, 1, 0);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setFocusTraversable(false);
        chatArea.getStyleClass().add("chat_textarea");

        grid.add(chatArea, 1, 1);

        chatField = new TextField();
        chatField.getStyleClass().add("chat_textarea");

        grid.add(chatField, 1, 2);

        chatSubmitButton = new Button("Send");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(chatSubmitButton);
        grid.add(hbBtn, 1, 3);

        chatField.requestFocus();

        Scene chatScene = new Scene(grid,400, 300);


        chatScene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());

        chatStage.setScene(chatScene);
        chatStage.show();
    }

    public void clearChat() {
        chatArea.clear();
    }

    public VBox buildFriendlistItem(String name, int unreadMessages, boolean isChatting) {
        VBox friendlistItem = new VBox(5);
        friendlistItem.getStyleClass().add("friendlist_item");

        Text friendName = new Text();
        friendName.getStyleClass().add("friendlist_item_text");
        friendName.setFill(Color.WHITE);

        if (unreadMessages == 0) {
            friendName.setText(name);
        } else {
            friendName.setText(name + " (" + unreadMessages + ")");
        }
        if (!isChatting) {
            friendName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        } else {
            friendName.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        }

        friendlistItem.getChildren().add(friendName);

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

    public void sendEmptyFriendlist() {
        Text emptyText = new Text("No friends online :(");
        emptyText.getStyleClass().add("friendlist_item_text");
        emptyText.setFill(Color.WHITE);
        friendsBox.getChildren().add(emptyText);
    }
    public void showFriendlist(String username) {
        stage.setTitle("UChat - Friends");

        FLBox = new VBox();
        FLBox.getStyleClass().add("friendlist_background");

        FLBox.setPadding(new Insets(0, 0, 20, 0));

        friendsBox = new VBox();

        VBox FLTopBox = new VBox();
        FLTopBox.getStyleClass().add("friendlist_topbar");

        Text textUsername = new Text(username);
        textUsername.setFill(Color.LIGHTBLUE);
        textUsername.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));

        Text friendsTopText = new Text("Friends");
        friendsTopText.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        friendsTopText.setFill(Color.WHITE);

        FLTopBox.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        FLTopBox.setPadding(new Insets(10, 10, 10, 10));
        FLTopBox.getChildren().add(textUsername);
        FLTopBox.getChildren().add(friendsTopText);

        FLBox.getChildren().add(FLTopBox);
        FLBox.getChildren().add(friendsBox);

        Scene friendlistScene = new Scene(FLBox, 200, 300);

        friendlistScene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());
        stage.setScene(friendlistScene);
      //  stage.show();
    }

    public void clearFriendlist() {

        friendsBox.getChildren().clear();
    }

    public VBox addToFriendList(String username, int unreadMessages, boolean isChatting) {
        VBox newFriendItem = buildFriendlistItem(username, unreadMessages, isChatting);

        friendsBox.getChildren().add(newFriendItem);
        return newFriendItem;
    }
}
