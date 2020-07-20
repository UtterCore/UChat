package Messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class MessageHandler {

    private ScheduledExecutorService incomingThread;
    private ScheduledExecutorService outgoingThread;
    protected Socket socket;
    protected PrintWriter writer;
    protected Queue<PDU> outgoingQueue;
    protected Queue<PDU> incomingQueue;

    public MessageHandler(Socket socket) {
        this.socket = socket;

        outgoingQueue = new LinkedList<>();
        incomingQueue = new LinkedList<>();
    }

    public synchronized void enqueuePDU(PDU pdu) {
        outgoingQueue.add(pdu);
    }

    public synchronized Queue<PDU> getIncomingPDUQueue() {
        return incomingQueue;
    }

    public void startIO(int inputDelay, int outputDelay) {

        try {
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        outgoingThread = Executors.newScheduledThreadPool(1);
        outgoingThread.scheduleAtFixedRate(outputThread, 0, outputDelay, TimeUnit.MILLISECONDS);

        incomingThread = Executors.newScheduledThreadPool(1);
        incomingThread.scheduleAtFixedRate(inputThread, 0, inputDelay, TimeUnit.MILLISECONDS);
    }

    public void closeThreads() {
        outgoingThread.shutdownNow();
        incomingThread.shutdownNow();
    }

    private Runnable outputThread = new Runnable() {

        @Override
        public void run() {

            if (!outgoingQueue.isEmpty()) {
                if (writer == null) {
                    System.out.println("dead?");
                    return;
                }
                //System.out.println("Sending pdu: " + outgoingQueue.peek().toString());
                SocketIO.sendPDU(writer, outgoingQueue.poll());
            }
        }
    };

    private Runnable inputThread = new Runnable() {
        @Override
        public void run() {
            String input = null;
            try {
                input = SocketIO.getInput(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (input != null) {
                PDU incomingPDU = PduHandler.getInstance().parse_pdu(input);
                //System.out.println("Receiving pdu: " + incomingPDU.toString());
                incomingQueue.add(incomingPDU);
            }

        }
    };
}
