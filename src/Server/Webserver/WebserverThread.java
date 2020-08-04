package Server.Webserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class WebserverThread extends Thread {

    private Socket clientSocket;
    private String dir;
    private Webs webs;
    public WebserverThread(Socket clientSocket, String dir) {

        this.clientSocket = clientSocket;
        this.dir = dir;
        webs = new Webs(dir);
    }

    @Override
    public void run() {

        if (clientSocket.isClosed()) {
            return;
        }
        try {
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            webs.parseRequest(in.readLine(), out);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
