package org.project.utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class IntelliJLineCounter {
    private static AtomicLong totalLines = new AtomicLong(0);
    private static AtomicLong fileCount = new AtomicLong(0);
    private static AtomicLong folderCount = new AtomicLong(0);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java IntelliJLineCounter <top-folder-path>");
            return;
        }

        Path topFolder = Paths.get(args[0]);
        if (!Files.exists(topFolder) || !Files.isDirectory(topFolder)) {
            System.out.println("Invalid folder path: " + topFolder);
            return;
        }

        try {
            Files.walkFileTree(topFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().equals("target")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    folderCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileCount.incrementAndGet();
                    if (isIntelliJProjectFile(file.getFileName().toString())) {
                        totalLines.addAndGet(countLines(file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("📊 Scan Summary:");
            System.out.println("Total folders visited: " + folderCount);
            System.out.println("Total files scanned: " + fileCount);
            System.out.println("Total lines in IntelliJ project files: " + totalLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isIntelliJProjectFile(String fileName) {
        return fileName.endsWith(".java") ||
                fileName.endsWith(".kt") ||
                fileName.endsWith(".xml") ||
                fileName.endsWith(".gradle") ||
                fileName.endsWith(".properties") ||
                fileName.endsWith(".gitignore") ||
                fileName.endsWith(".md");
    }

    private static long countLines(Path filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return reader.lines().count();
        } catch (IOException e) {
            System.err.println("Couldn't read file: " + filePath);
            return 0;
        }
    }
}