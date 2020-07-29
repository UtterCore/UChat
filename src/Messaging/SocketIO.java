package Messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class SocketIO {

    public static String getInput(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        String input;

            while ((read = is.read(buffer)) != -1) {
                input = new String(buffer, 0, read);
                return input;
            }
        return null;
    }

    public static void sendPDU(PrintWriter writer, PDU pdu) {
        writer.print(pdu.toString());
        writer.flush();
    }
}
