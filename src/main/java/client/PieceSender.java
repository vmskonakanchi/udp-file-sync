package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PieceSender extends Thread {

    private final byte[] piece;

    private final int serverPort;
    private final InetAddress serverAddress;
    private final DatagramSocket socket;

    public PieceSender(byte[] piece, int serverPort, InetAddress serverAddress, DatagramSocket socket) {
        this.piece = piece;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DatagramPacket packet = new DatagramPacket(piece, piece.length, serverAddress, serverPort);
            System.out.println("Sending piece of size " + piece.length);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
