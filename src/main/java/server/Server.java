package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <storage-directory>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String directory = args[1];
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                byte[] input = new byte[65536];
                serverSocket.receive(new DatagramPacket(input, input.length));
                String receivedText = new String(input);
                String fileName = receivedText.split("\n")[0];
                PacketReceiver receiver = new PacketReceiver(directory + "/" + fileName, input);
                receiver.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
