import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("""
                    please enter 3 arguments: a map start id, a map end id, a destination path
                    
                    """);
            System.exit(1);
        } else if (!args[0].matches("\\d+")) {
            System.err.println("""
                    please enter a number for the first argument
                    """);
            System.exit(1);
        } else if (!args[1].matches("\\d+")) {
            System.err.println("""
                    please enter a number for the second argument
                    
                    """);
            System.exit(1);
        }
        List<Thread> threads = new LinkedList<>();
        int arg1 = Integer.parseInt(args[0]), arg2 = Integer.parseInt(args[1]);

        int startMapId = Math.max(arg1, arg2), endMapId = Math.min(arg1, arg2);

        for (int i = startMapId; i >= endMapId; i--) {
            String zipUrl;

            if (i < 1300) {
                zipUrl = "https://packs.ppy.sh/S" + i + "%20-%20Beatmap%20Pack%20%23" + i + ".7z";

            } else if (i < 1318) {

                zipUrl = "https://packs.ppy.sh/S" + i + "%20-%20Beatmap%20Pack%20%23" + i + ".zip";
            } else {
                zipUrl = "https://packs.ppy.sh/S" + i + "%20-%20osu%21%20Beatmap%20Pack%20%23" + i + ".zip";

            }
            // https://packs.ppy.sh/S1282%20-%20Beatmap%20Pack%20%231282.7z

            Thread thread = Thread.ofVirtual().start(() -> {
                try (var zipInputStream = new ZipInputStream(new URL(zipUrl).openStream())) {
                    Path destinationPath = Paths.get(args[2]);

                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        Path entryPath = destinationPath.resolve(entry.getName());

                        // Ensure parent directories are created
                        Files.createDirectories(entryPath.getParent());

                        // Copy entry content to destination
                        Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Thread " + Thread.currentThread() + " :copying zip entry");
                        // Close the entry to free up resources
                        zipInputStream.closeEntry();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            threads.addFirst(thread);

        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}