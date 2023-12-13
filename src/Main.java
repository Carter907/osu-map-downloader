import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import java.util.function.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.math.*;
import java.nio.*;
import java.sql.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.time.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("""
                    please enter 3 arguments: a map start id, a map end id, a destination path
                    (note: ids are traversed from latest to oldest meaning your start id should
                    be greater than your end id)
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
        int startMapId = Integer.parseInt(args[0]), endMapId = Integer.parseInt(args[1]);

        for (int i = startMapId; i >= endMapId; i--) {
            String zipUrl = "https://packs.ppy.sh/S" + i + "%20-%20osu%21%20Beatmap%20Pack%20%23" + i + ".zip";

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