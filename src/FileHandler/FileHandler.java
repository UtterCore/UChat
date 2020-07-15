package FileHandler;

import Messaging.PDU;
import Messaging.PduHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {


    private static String getFileName(String username) {
        return "chatlog_" + username + ".txt";
    }

    public static ArrayList<PduHandler.PDU_MESSAGE> getMessages(String username, String partnerUsername) {
        ArrayList<PduHandler.PDU_MESSAGE> pduList = new ArrayList<>();

        String filename = getFileName(username);

        File file = new File(filename);
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                PDU pdu = PduHandler.getInstance().parse_pdu(scanner.nextLine());
                PduHandler.PDU_MESSAGE msgPdu = (PduHandler.PDU_MESSAGE)pdu;

                if (msgPdu.sender.equals(partnerUsername)) {
                    pduList.add((PduHandler.PDU_MESSAGE) pdu);
                }
                if (msgPdu.sender.equals(username) && msgPdu.target.equals(partnerUsername)) {
                    System.out.println(msgPdu.toString());
                    pduList.add((PduHandler.PDU_MESSAGE) pdu);
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("No saved chat logs.");
        }
        return pduList;
    }

    public static void savePDUToFile(PDU pdu, String username) {
        BufferedWriter writer = null;
        File file = new File(getFileName(username));
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
