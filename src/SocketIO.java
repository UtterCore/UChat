import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;

public class SocketIO {

    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_UPDATE = 2;

    public static String getInput(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
            while ((read = is.read(buffer)) != -1) {
                String input = new String(buffer, 0, read);
                return input;
            }
        return null;
    }

    public static void sendMessage(int type, User sender, PrintWriter writer, String message) {
        writer.print(sender.getFullName() + ": " + message);
        writer.flush();
    }
}
