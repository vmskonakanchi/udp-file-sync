package client;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

public class SyncFile {
    private final String fileName;
    private int progress;
    private long fileSize;
    private byte[] dataToSend;
    private final LinkedBlockingQueue<byte[]> pieces = new LinkedBlockingQueue<>();

    private boolean changed = false;

    public int getProgress() {
        return progress;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public SyncFile(String name) {
        Path path = Path.of(name);
        fileName = path.getFileName().toString();
        try {
            if (Files.exists(path)) {
                fileSize = (Files.size(path) / 1024) / 1024;
                InputStream is = new FileInputStream(path.toFile());
                dataToSend = is.readAllBytes();
            } else {
                throw new FileNotFoundException("File with the given path is not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * step - 1 break down the file size into list of 4mb blocks
     */
    public void breakDown() throws Exception {
        int start = 0;
        int end = 0;
        while (end < dataToSend.length) {
            end = start + 1024 * 1024 * 4;
            if (end > dataToSend.length) {
                end = dataToSend.length;
            }
            byte[] piece = new byte[end - start];
            System.arraycopy(dataToSend, start, piece, 0, piece.length);
            pieces.put(piece);
            start = end;
        }
        System.out.println("File is broken down into " + pieces.size() + " pieces");
    }

    /*
     * step - 2 send the list of 4mb blocks to the client
     */
    public void send(DatagramSocket socket, int serverPort, InetAddress serverAddress) {
        if (socket.isClosed()) {
            System.out.println("Socket is closed or pieces are empty");
            return;
        }
        // send the file size to the client
        synchronized (pieces) {
            while (!pieces.isEmpty()) {
                // send the piece to the client
                byte[] piece = pieces.poll();
                PieceSender sender = new PieceSender(fileName, piece, serverPort, serverAddress, socket);
                sender.start();
                progress++;
            }
        }
    }
}
