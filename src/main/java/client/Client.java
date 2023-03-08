package client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

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
            System.out.println("Reading " + storageDirectory);
            Thread.sleep(FILE_CHECK_INTERVAL + 2);
            System.out.println("Reading completed , Sync Started");
            DatagramSocket socket = new DatagramSocket(localPort);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String filePath : filesToSync.keySet()) {
                            if (filesToSync.get(filePath)) {
                                SyncFile syncFile = new SyncFile(filePath);
                                syncFile.breakDown();
                                syncFile.send(socket, port, host);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, FILE_CHECK_INTERVAL);
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
