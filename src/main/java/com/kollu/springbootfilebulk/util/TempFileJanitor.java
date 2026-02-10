package com.kollu.springbootfilebulk.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class TempFileJanitor {

    @Scheduled(cron = "0 0 */3 * * *") // Runs every 3 hours
    public void cleanOldTempFiles() {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        long fourHoursAgo = System.currentTimeMillis() - (4 * 60 * 60 * 1000);

        try (Stream<Path> files = Files.walk(tempDir)) {
            files.filter(path -> path.getFileName().toString().startsWith("upload-"))
                 .filter(path -> {
                     try {
                         return Files.getLastModifiedTime(path).toMillis() < fourHoursAgo;
                     } catch (IOException e) { return false; }
                 })
                 .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                 });
        } catch (IOException e) { e.printStackTrace(); }
    }
}
