import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {


    public static ArrayList<PduHandler.PDU_MESSAGE> getMessages(String username, String partnerUsername) {
        ArrayList<PduHandler.PDU_MESSAGE> pduList = new ArrayList<>();

        File file = new File("chatlog_" + username + ".txt");
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                System.out.println("adding pdu");
                PDU pdu = PduHandler.getInstance().parse_pdu(scanner.nextLine());
                PduHandler.PDU_MESSAGE msgPdu = (PduHandler.PDU_MESSAGE)pdu;

                if (msgPdu.sender.equals(partnerUsername)) {
                    System.out.println("also addingg it to the thing");
                    pduList.add((PduHandler.PDU_MESSAGE) pdu);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pduList;
    }

    public static void savePDUToFile(PDU pdu, String username) {
        BufferedWriter writer = null;
        File file = new File("chatlog_" + username + ".txt");
        try {
             writer = new BufferedWriter(new FileWriter(file, true));

        writer.write(pdu.toString() + "\n");
        writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException io) {
            io.printStackTrace();
            return;
        }
    }
}
