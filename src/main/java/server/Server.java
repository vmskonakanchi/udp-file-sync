package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class Server extends FileManager {
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
                byte[] input = new byte[65535];
                serverSocket.receive(new DatagramPacket(input, input.length));
                String cleanedText = new String(cleanBytes(input), StandardCharsets.UTF_8);
                String fileName = cleanedText.split("\n")[0];
                PieceReceiver receiver = new PieceReceiver(directory + "/" + fileName, cleanedText.substring(fileName.length()).getBytes(StandardCharsets.UTF_8));
                receiver.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] cleanBytes(byte[] dataToWrite) {
        int i = dataToWrite.length - 1;
        while (dataToWrite[i] == 0) {
            i--;
        }
        byte[] temp = new byte[i + 1];
        System.arraycopy(dataToWrite, 0, temp, 0, i + 1);
        return temp;
    }
}
