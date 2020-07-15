package GUI;/*
 * Class: GUI
 * Date: 10/1 2019
 * Author: Erik Rostö
 * Description: Responsible for drawing upp the GUI.
 */
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GUI {
    private JFrame window;
    private JButton sendMessageButton;
    private JScrollPane chatScrollpane;
    private JTextArea chatArea;
    private JTextField enterMessageArea;

    private JPanel coverPanel;

    private JPanel usersPanel;
    private JTextArea usersArea;
    private JScrollPane usersScrollpane;

    private JPanel chatCoverPanel;

    private JPanel chatPanel;
    private JPanel enterMessagePanel;
    private JPanel chattingWith;
    private JLabel chattingWithLabel;

    private JLabel connectingLabel;


    private void buildUsersPanel() {
        usersPanel = new JPanel(new BorderLayout());
        usersArea = new JTextArea();
        usersScrollpane = new JScrollPane(usersArea);

        usersScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        usersPanel.add(usersScrollpane, BorderLayout.CENTER);
        usersArea.setPreferredSize(new Dimension(100, 300));
        usersArea.setEditable(false);
        usersArea.setWrapStyleWord(true);
        usersArea.setLineWrap(true);
        usersPanel.setVisible(false);
    }

    private void buildChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatScrollpane = new JScrollPane(chatArea);

        chatScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatArea.setEditable(false);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Color.gray));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
    }

    private void buildEnterMessagePanel() {
        sendMessageButton = new JButton("Send");
        enterMessagePanel = new JPanel(new BorderLayout());
        enterMessageArea = new JTextField();

        enterMessagePanel.add(enterMessageArea, BorderLayout.CENTER);
        enterMessagePanel.add(sendMessageButton, BorderLayout.EAST);
    }

    private void buildChattingWithPanel() {
        chattingWith = new JPanel(new BorderLayout());
        chattingWith.setBorder(new EmptyBorder(10, 10, 10, 10));
        chattingWithLabel = new JLabel("");
        chattingWith.add(chattingWithLabel, BorderLayout.CENTER);

        connectingLabel = new JLabel();
        chattingWith.add(connectingLabel, BorderLayout.EAST);
    }
    public GUI() {
        window = new JFrame("UChat");

        window.setLayout(new BorderLayout());

        window.setMinimumSize(new Dimension(300, 150));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        coverPanel = new JPanel(new BorderLayout());
        coverPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        buildUsersPanel();

        buildChattingWithPanel();

        buildChatPanel();


        chatCoverPanel = new JPanel(new BorderLayout());

        buildEnterMessagePanel();
        //chatScrollpane.add(chatArea);

        chatPanel.add(chattingWith, BorderLayout.NORTH);
        chatCoverPanel.add(chatScrollpane, BorderLayout.CENTER);
        chatPanel.add(chatCoverPanel, BorderLayout.CENTER);
        chatPanel.add(enterMessagePanel, BorderLayout.SOUTH);
        coverPanel.add(chatPanel, BorderLayout.CENTER);
        chatCoverPanel.add(usersPanel, BorderLayout.EAST);
        window.add(coverPanel, BorderLayout.CENTER);


        chatCoverPanel.setVisible(false);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        enterMessageArea.grabFocus();

        chattingWithLabel.setText("Enter username: ");
        new ButtonActivatorThread().start();
    }

    public void setChatPartnerLabel(String chatPartner) {
        if (chatPartner == null) {
            chatPartner = "";
        }
        chattingWithLabel.setText(chatPartner);
    }

    public JFrame getWindow() {
        return window;
    }

    public void showFullApp() {
        showUsers();
        showChat();

        window.setMinimumSize(new Dimension(550, 400));
        window.setLocationRelativeTo(null);
        clearChat();
        connectingLabel.setVisible(false);
        chattingWithLabel.setText("");
    }

    public void showConnectionError() {
        connectingLabel.setText("Client failed to connect");
    }

    public void showConnectingMessage() {
        connectingLabel.setText("Connecting...");
    }
    public void clearChat() {
        chatArea.setText(null);
        window.revalidate();
    }
    private void showUsers() {
        usersPanel.setVisible(true);
        window.revalidate();
    }

    private void showChat() {
        chatCoverPanel.setVisible(true);
        window.revalidate();
    }

    public void updateUserlist(ArrayList<String> userlist) {

        usersArea.setText(null);
        window.revalidate();

        String userlistString = "";
        for (String user : userlist) {
            userlistString += user + "\n";
        }

        usersArea.setText(userlistString);
    }
    public void emptyMessageArea() {
        enterMessageArea.setText(null);

        window.revalidate();
    }
    public JButton getSendMessageButton() {
        return sendMessageButton;
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    public JTextField getEnterMessageArea() {
        return enterMessageArea;
    }

    public void printMessageInChat(String message) {
        chatArea.append(message);
        chatScrollpane.getVerticalScrollBar().setValue(chatScrollpane.getVerticalScrollBar().getMaximum());
    }

    private class ButtonActivatorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (enterMessageArea.getText() != null) {
                    sendMessageButton.setEnabled(enterMessageArea.getText().length() > 0);
                }
            }
        }
    }
}
