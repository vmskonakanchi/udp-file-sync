package server;

import utils.ByteUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PieceReceiver extends Thread {
    private byte[] dataToWrite;
    private final String filePath;

    public PieceReceiver(String filePath, byte[] dataToWrite) {
        this.filePath = filePath;
        this.dataToWrite = dataToWrite;
    }

    @Override
    public void run() {
        Path tempFilePath = Path.of(filePath);
        //TODO:add code to create directory if not exists
        try {
            if (Files.exists(tempFilePath)) {
                dataToWrite = ByteUtils.cleanBytes(dataToWrite);
                RandomAccessFile rFile = new RandomAccessFile(tempFilePath.toFile(), "rws");
                if (rFile.length() > 0) {
                    rFile.setLength(0);
                }
                rFile.seek(rFile.length());
                rFile.write(dataToWrite);
                rFile.close();
            } else {
                Files.createFile(tempFilePath);
                dataToWrite = ByteUtils.cleanBytes(dataToWrite);
                RandomAccessFile rFile = new RandomAccessFile(tempFilePath.toFile(), "rws");
                rFile.seek(rFile.length());
                rFile.write(dataToWrite);
                rFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
