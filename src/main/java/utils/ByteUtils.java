package utils;

public class ByteUtils {

    public static byte[] cleanBytes(byte[] dataToWrite) {
        int i = dataToWrite.length - 1;
        while (dataToWrite[i] == 0) {
            i--;
        }
        byte[] temp = new byte[i + 1];
        System.arraycopy(dataToWrite, 0, temp, 0, i + 1);
        return temp;
    }
}
