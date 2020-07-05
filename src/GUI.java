/*
 * Class: GUI
 * Date: 10/1 2019
 * Author: Erik Rostö
 * Description: Responsible for drawing upp the GUI.
 */

import sun.misc.JavaLangAccess;

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

public class GUI {
    private JFrame window;
    private JButton sendMessageButton;
    private JScrollPane chatScrollpane;
    private JTextArea chatArea;
    private JTextField enterMessageArea;
    private JPanel chatPanel;
    private JPanel enterMessagePanel;
    private JPanel chattingWith;
    private JLabel chattingWithLabel;


    public GUI() {
        window = new JFrame("UChat");

        window.setLayout(new BorderLayout());

        window.setMinimumSize(new Dimension(450, 300));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        chattingWith = new JPanel(new BorderLayout());
        chattingWith.setBorder(new EmptyBorder(10, 10, 10, 10));
        chattingWithLabel = new JLabel("");
        chattingWith.add(chattingWithLabel, BorderLayout.CENTER);
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        chatArea = new JTextArea();

        chatScrollpane = new JScrollPane(chatArea);
        chatScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        chatArea.setEditable(false);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Color.gray));

        chatArea.setMargin(new Insets(10, 10, 10, 10));


        sendMessageButton = new JButton("Send");
        enterMessagePanel = new JPanel(new BorderLayout());

        enterMessageArea = new JTextField();
        enterMessagePanel.add(enterMessageArea, BorderLayout.CENTER);
        enterMessagePanel.add(sendMessageButton, BorderLayout.EAST);
        //chatScrollpane.add(chatArea);


        chatPanel.add(chattingWith, BorderLayout.NORTH);
        chatPanel.add(chatScrollpane, BorderLayout.CENTER);
        chatPanel.add(enterMessagePanel, BorderLayout.SOUTH);

        window.add(chatPanel, BorderLayout.CENTER);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        enterMessageArea.grabFocus();

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
