package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class PieceSender extends Thread {
    private final byte[] piece;
    private final int serverPort;
    private final InetAddress serverAddress;
    private final DatagramSocket socket;
    private final String fileName;

    public PieceSender(String fileName, byte[] piece, int serverPort, InetAddress serverAddress, DatagramSocket socket) {
        this.fileName = fileName;
        this.piece = piece;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            int fileNameLength = (fileName + "\n").getBytes(StandardCharsets.UTF_8).length + piece.length;
            byte[] dataToSend = new byte[fileNameLength];
            System.arraycopy((fileName + "\n").getBytes(StandardCharsets.UTF_8), 0, dataToSend, 0, (fileName + "\n").getBytes(StandardCharsets.UTF_8).length);
            System.arraycopy(piece, 0, dataToSend, (fileName + "\n").getBytes(StandardCharsets.UTF_8).length, piece.length);
            DatagramPacket packet = new DatagramPacket(dataToSend, fileNameLength, serverAddress, serverPort);
            System.out.println("Sending piece of size " + fileNameLength);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
