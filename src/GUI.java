/*
 * Class: GUI
 * Date: 10/1 2019
 * Author: Erik Rostö
 * Description: Responsible for drawing upp the GUI.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
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
    private JList<String> channelList;
    private JTable programTable;
    private JButton sendMessageButton;
    private JPanel tablePanel;
    private boolean channelTableIsOpen;
    private JScrollPane chatScrollpane;
    private JMenuItem menuItemUpdate;
    private JTextArea chatArea;
    private JTextField enterMessageArea;
    private JPanel chatPanel;
    private JPanel enterMessagePanel;


    public GUI() {
        window = new JFrame("UChat");

        window.setLayout(new BorderLayout());

        window.setMinimumSize(new Dimension(450, 300));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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


        sendMessageButton = new JButton("Send");
        enterMessagePanel = new JPanel(new BorderLayout());
        enterMessageArea = new JTextField();
        enterMessagePanel.add(enterMessageArea, BorderLayout.CENTER);
        enterMessagePanel.add(sendMessageButton, BorderLayout.EAST);
        //chatScrollpane.add(chatArea);


        chatPanel.add(chatScrollpane, BorderLayout.CENTER);
        chatPanel.add(enterMessagePanel, BorderLayout.SOUTH);

        window.add(chatPanel, BorderLayout.CENTER);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
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
}
