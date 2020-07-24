package GUI;

import Messaging.ErrorMessage;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

//TODO: Empty friends-grejen måste bli remove:ad när någon vän finns!!
public class GUIFX {

    public static final int LOGIN_SUCCESS = 1;
    public static final int WRONG_CREDENTIALS = 2;

    private Stage stage;
    private Stage friendlistStage;

    private Button userSubmitButton;
    private Button chatSubmitButton;
    private TextField userTextField;
    private PasswordField userPasswordField;
    private Text actiontarget;
    private Hyperlink registerLink;

    private TextField createUsernameField;
    private TextField createEmailField;
    private PasswordField createPasswordField;
    private PasswordField createPasswordFieldRepeat;
    private Button createUserSubmitButton;
    private Button createBackButton;

    private TextField chatField;
    private TextArea chatArea;
    private TextArea fListArea;

    private VBox friendsBox;
    private VBox FLBox;

    private Text emptyFriendsText;

    private Label chattingWith;

    private boolean partnerOnline;

    Stage chatStage;

    ArrayList<Friend> friends;

    public GUIFX(Stage stage) {
        friends = new ArrayList<>();
        this.stage = stage;
        emptyFriendsText = new Text("No friends online :(");
        emptyFriendsText.setFill(Color.WHITE);
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

    public PasswordField getUserPasswordField() {
        return userPasswordField;
    }

    public Hyperlink getRegisterLink() {
        return registerLink;
    }

    public Button getCreateUserSubmitButton() {
        return createUserSubmitButton;
    }

    public Button getCreateBackButton() {
        return createBackButton;
    }

    public TextField getCreateUsernameField() {
        return createUsernameField;
    }

    public PasswordField getCreatePasswordField() {
        return createPasswordField;
    }

    public PasswordField getCreatePasswordFieldRepeat() {
        return createPasswordFieldRepeat;
    }

    public TextField getCreateEmailField() {
        return createEmailField;
    }

    public void showRegisterError(ErrorMessage errorMessage) {
        actiontarget.setFill(Color.FIREBRICK);
        actiontarget.setText(errorMessage.getErrorMessage());
    }

    public void showSuccessMessage(ErrorMessage errorMessage) {
        actiontarget.setFill(Color.SPRINGGREEN);
        actiontarget.setText(errorMessage.getErrorMessage());
    }

    public void showUsernameError(String errorMessage) {
        actiontarget.setFill(Color.FIREBRICK);
        actiontarget.setText(errorMessage);
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
        grid.add(actiontarget, 1, 4);

        Text scenetitle = new Text("Welcome");
        scenetitle.setFill(Color.LIGHTBLUE);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("Username: ");
        userNameLabel.setTextFill(Color.WHITE);
        grid.add(userNameLabel, 0, 1);

        userTextField = new TextField();
        userTextField.getStyleClass().add("chat_textarea");
        grid.add(userTextField, 1, 1);

        Label passwordLabel = new Label("Password: ");
        passwordLabel.setTextFill(Color.WHITE);
        grid.add(passwordLabel, 0, 2);

        userPasswordField = new PasswordField();
        userPasswordField.getStyleClass().add("chat_textarea");
        grid.add(userPasswordField, 1, 2);

        registerLink = new Hyperlink("Register");

        userSubmitButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(registerLink);
        hbBtn.getChildren().add(userSubmitButton);
        grid.add(hbBtn, 1, 3);



        Scene scene = new Scene(grid, 300, 275);


        scene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showRegister() {

        Stage primaryStage = stage;

        primaryStage.setTitle("UChat Register user");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("friendlist_background");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        Text scenetitle = new Text("Create user");
        scenetitle.setFill(Color.LIGHTBLUE);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("Username: ");
        userNameLabel.setTextFill(Color.WHITE);
        grid.add(userNameLabel, 0, 1);
        createUsernameField = new TextField();

        Label emailLabel = new Label("Email: ");
        emailLabel.setTextFill(Color.WHITE);
        grid.add(emailLabel, 0, 2);
        createEmailField = new TextField();

        Label passwordLabel = new Label("Password: ");
        passwordLabel.setTextFill(Color.WHITE);
        grid.add(passwordLabel, 0, 3);
        createPasswordField = new PasswordField();

        Label passwordRepeatLabel = new Label("Repeat password: ");
        passwordRepeatLabel.setTextFill(Color.WHITE);
        grid.add(passwordRepeatLabel, 0, 4);
        createPasswordFieldRepeat = new PasswordField();

        createUsernameField.getStyleClass().add("chat_textarea");
        createEmailField.getStyleClass().add("chat_textarea");
        createPasswordField.getStyleClass().add("chat_textarea");
        createPasswordFieldRepeat.getStyleClass().add("chat_textarea");

        grid.add(createUsernameField, 1, 1);
        grid.add(createEmailField, 1, 2);
        grid.add(createPasswordField, 1, 3);
        grid.add(createPasswordFieldRepeat, 1, 4);

        createBackButton = new Button("Back");
        createUserSubmitButton = new Button("Register");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(createBackButton);
        hbBtn.getChildren().add(createUserSubmitButton);
        grid.add(hbBtn, 1, 5);


        Scene scene = new Scene(grid, 300, 275);


        scene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void lockLoginScreen() {
        userSubmitButton.setDisable(true);
        userPasswordField.setDisable(true);
        userTextField.setDisable(true);
        registerLink.setDisable(true);
    }

    public void unlockLoginScreen() {
        userSubmitButton.setDisable(false);
        userPasswordField.setDisable(false);
        userTextField.setDisable(false);
        registerLink.setDisable(false);
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

        setPartnerOnline(true);

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

    public void chatSetOffline(String username) {
        if (!partnerOnline) {
            return;
        }
        setPartnerOnline(false);
        chattingWith.setText(username + " (offline)");
        chatField.setDisable(true);
    }

    public void chatSetOnline(String username) {

        if (partnerOnline) {
            return;
        }
        setPartnerOnline(true);
        chattingWith.setText(username);
        chatField.setDisable(false);
    }

    public void setPartnerOnline(boolean partnerOnline) {
        this.partnerOnline = partnerOnline;
    }

    public boolean getPartnerOnline() {
        return partnerOnline;
    }
    public void clearChat() {
        chatArea.clear();
    }

    public void sendEmptyFriendlist() {

        friends = new ArrayList<>();
        emptyFriendsText.setVisible(true);
        //Text emptyText = new Text("No friends online :(");
        //emptyText.getStyleClass().add("friendlist_item_text");
        //emptyText.setFill(Color.WHITE);
        //friendsBox.getChildren().add(emptyText);
    }
    public void showFriendlist(String username) {
        stage.setTitle("UChat - Friends");

        FLBox = new VBox();
        FLBox.getStyleClass().add("friendlist_background");

        FLBox.setPadding(new Insets(0, 0, 20, 0));

        friendsBox = new VBox();

        //friendsBox.getChildren().add(emptyFriendsText);

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

    public class Friend {
        public String username;
        public String visibleName;
        public int unreadMessages;
        public boolean isChatting;
        public VBox vbox;
        public Text friendName;

        public Friend(String username) {
            this.username = username;
            this.unreadMessages = unreadMessages;
            this.isChatting = isChatting;
            visibleName = username;

            friendName = new Text(visibleName);

            vbox = new VBox(5);
            vbox.getStyleClass().add("friendlist_item");

            friendName.getStyleClass().add("friendlist_item_text");
            friendName.setFill(Color.WHITE);

            vbox.getChildren().add(friendName);

            vbox.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
            vbox.setPadding(new Insets(0, 10, 0, 10));
        }

        public void changeVisibleName(String newName) {
            System.out.println("changing text to: " + newName);
            visibleName = newName;
            friendName.setText(newName);
         //   vbox = buildFriendlistItem(friendName, unreadMessages, isChatting);
        }

        public void resetName() {
            visibleName = username;
            changeVisibleName(visibleName);
            vbox.getStyleClass().clear();
            vbox.getStyleClass().add("friendlist_item");
            friendName.setFill(Color.WHITE);
        }
    }
    public void clearFriendlist() {

        //friendsBox.getChildren().clear();
        //friends = new ArrayList<>();
    }

    public void setHasSentMessages(String username) {
        for (Friend friend : friends) {
            if (friend.username.equals(username)) {

              //  friend.friendName.getStyleClass().add("friendlist_has_sent_message");
                friend.friendName.setFill(Color.ORANGE);
                friend.vbox.getStyleClass().clear();
                friend.vbox.getStyleClass().add("friendlist_item_unread");
            }
        }
    }

    public void resetName(String username) {
        for (Friend friend : friends) {
            if (friend.username.equals(username)) {
                friend.resetName();
            }
        }
    }

    public Friend findFriendWithUsername(String username) {
        for (Friend friend : friends) {
            if (friend.username.equals(username)) {

                return friend;
            }
        }
        return null;
    }

    private boolean isInList(Friend user, ArrayList<String> friendList) {

        for (String friend : friendList) {
            if (user.username.equals(friend)) {
                return true;
            }
        }
        return false;
    }

    /*TODO: check if the friend is already in the list. then there is no need to
      create a new object (it still doesn't work. Friendlistupdate
      resets colour change!!!
      */
    public void addFriendsToList(ArrayList<String> newFriends, String username) {

        emptyFriendsText.setVisible(false);
        emptyFriendsText.setDisable(true);

        for (Friend friend : friends) {
            if (!isInList(friend, newFriends)) {
                removeFriendFromList(friend);
            }
        }

        for (String newFriend : newFriends) {
            if (!newFriend.equals(username)) {
                addToFriendList(newFriend);
            }
        }
    }

    public void removeFriendFromList(Friend friend) {

        friends.remove(friend);
        friendsBox.getChildren().remove(friend.vbox);
    }
    public VBox addToFriendList(String username) {


        if (findFriendWithUsername(username) == null) {
            Friend friend = new Friend(username);

            friends.add(friend);

            friendsBox.getChildren().add(friend.vbox);
            return friend.vbox;
        } else {
            return findFriendWithUsername(username).vbox;
        }
    }
}
