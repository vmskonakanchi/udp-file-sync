package client;


import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class FileManager {

    private static final long FILE_CHECK_INTERVAL = 1000 * 10;

    protected static final BlockingQueue<String> filesToSync = new LinkedBlockingQueue<>();
    private static final Map<String, Long> fileOffset = new ConcurrentHashMap<>();

    //crawl the directory and add files to the queue
    public static void crawlDirectory(String directory) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                crawl(directory);
            }
        }, 0, FILE_CHECK_INTERVAL);
    }

    private static void crawl(String directory) {
        try {
            Path path = Path.of(directory);
            if (Files.exists(path)) {
                Files.walk(path).filter(Files::isRegularFile).forEach(file -> {
                    try {
                        RandomAccessFile rFile = new RandomAccessFile(file.toFile(), "r");
                        long recentFileOffset = rFile.length();
                        if (filesToSync.contains(file.toString())) { // checking if file exists
                            Long currentFileOffset = fileOffset.computeIfAbsent(file.toString(), k -> 0L);
                            if (recentFileOffset > currentFileOffset || recentFileOffset < currentFileOffset) {
                                //data changed - so add the file to fileSync Queue
                                System.out.println("data changed in : " + file);
                                filesToSync.put(file.toString());
                                fileOffset.replace(file.toString(), recentFileOffset);
                            }
                        } else {
                            //there is no file
                            filesToSync.put(file.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
