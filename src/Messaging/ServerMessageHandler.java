package Messaging;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ServerMessageHandler extends MessageHandler {

    public ServerMessageHandler(Socket socket) {
        super(socket);
        startIO(1, 10);
    }

    public void sendHTTPResponse(PduHandler.PDU_RESOURCE_RESPONSE responsePDU) {

        if (responsePDU.response.getFileType().equals("image/png") || responsePDU.response.getFileType().equals("image/jpeg")) {
            try {
                sendRawData(responsePDU.response.getHTTPHeader());

                DataOutputStream os = new DataOutputStream(getSocket().getOutputStream());

                if (responsePDU.response.getFileType().equals("image/png")) {
                    ImageIO.write(responsePDU.response.getImage(), "png", os);
                } else {
                    ImageIO.write(responsePDU.response.getImage(), "jpg", os);
                }

                os.flush();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            sendRawData(responsePDU.response.toHTTP());
        }
        closeThreads();
    }
}
