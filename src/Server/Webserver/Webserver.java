package Server.Webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver {

    private ServerSocket serverSocket;
    private String dir;
    private WaitThread waitThread;

    public void startServer(int port, String dir) throws IOException {


        this.dir = dir;
        System.out.println("Starting web server on port " + port + "\n" +
                "Serving directory: " + dir);
        serverSocket = new ServerSocket(port);

        waitThread = new WaitThread();
        waitThread.start();
    }

    public void stopServer() throws IOException {
        System.out.println("Stopping server");
        waitThread.shouldStop = true;
        serverSocket.close();
    }

    private class WaitThread extends Thread {

        volatile boolean shouldStop;
        volatile Socket clientSocket;

        @Override
        public void run() {
            while (!shouldStop) {
                try {
                     clientSocket = serverSocket.accept();
                    new WebserverThread(clientSocket, dir).start();
                } catch (IOException e) {
                    //Socket exception if the socket is closed from server class
                }
            }
        }
    }


}
