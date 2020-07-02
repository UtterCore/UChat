import java.io.IOException;

public class Server {
    public static void main(String args[]) {


        try {
            new ServerController().startServer(4001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
