package client;

import server.PacketReceiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Scanner;

public class Client extends FileManager {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java Client <local-port> <storage-directory> <host>");
            System.exit(1);
        }
        String hostString = args[2].split(":")[0];
        int port = Integer.parseInt(args[2].split(":")[1]);
        InetAddress host = InetAddress.getByName(hostString);
        int localPort = Integer.parseInt(args[0]);
        String storageDirectory = args[1];
        crawlDirectory(storageDirectory);
        try {
            DatagramSocket socket = new DatagramSocket(localPort);
            System.out.println("Client started on port " + localPort);
            while (true) {
                if (!filesToSync.isEmpty()) {
                    String filePath = filesToSync.poll();
                    if (filePath != null) {
                        byte[] fileName = cleanBytes(Path.of(filePath).getFileName().toString().getBytes());
                        System.out.println("Sending file " + filePath);
                        DatagramPacket packet = new DatagramPacket(fileName, fileName.length, host, port);
                        socket.send(packet);
                        SyncFile syncFile = new SyncFile(filePath);
                        syncFile.breakDown();
                        syncFile.send(socket, port, host);
                    }
                }
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
