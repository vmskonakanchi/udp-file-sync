package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PacketReceiver extends Thread {
    private byte[] dataToWrite;

    private final String filePath;

    public PacketReceiver(String filePath, byte[] dataToWrite) {
        this.filePath = filePath;
        this.dataToWrite = dataToWrite;
    }

    private void cleanBytes() {
        int i = dataToWrite.length - 1;
        while (dataToWrite[i] == 0) {
            i--;
        }
        byte[] temp = new byte[i + 1];
        System.arraycopy(dataToWrite, 0, temp, 0, i + 1);
        dataToWrite = temp;
    }

    @Override
    public void run() {
        Path tempFilePath = Path.of(filePath);
        //TODO:add code to create directory if not exists
        try {
            if (Files.exists(tempFilePath)) {
                cleanBytes();
                RandomAccessFile rFile = new RandomAccessFile(tempFilePath.toFile(), "rw");
                rFile.seek(rFile.length());
                rFile.write(dataToWrite);
                rFile.close();
            } else {
                Files.createFile(tempFilePath);
                cleanBytes();
                RandomAccessFile rFile = new RandomAccessFile(tempFilePath.toFile(), "rw");
                rFile.seek(rFile.length());
                rFile.write(dataToWrite);
                rFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
