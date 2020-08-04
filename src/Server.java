import Server.*;

public class Server {
    public static void main(String args[]) {


        /*
        new Thread(() -> {
            try {
                System.out.println("starting webserver!!");
                new Webserver().startServer(80, "./resources");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        */
        new ServerController().startServer();
    }
}
