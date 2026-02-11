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

    @Scheduled(cron = "0 */1 * * * *") // Runs every 3 hours
    public void cleanOldTempFiles() {
    	System.out.println("*******cleanOldTempFiles *********");
       
    	String tempRoot = System.getProperty("java.io.tmpdir");
    	System.out.println("tempRoot====" + tempRoot); 
    	Path tempDir = Paths.get(tempRoot, "batch-uploads");
    	
    	System.out.println("tempDir path: " + tempDir.toAbsolutePath());
    	//It will check 30 min time spam
        long fourHoursAgo = System.currentTimeMillis() - (30 * 60 * 1000);
        try (Stream<Path> files = Files.walk(tempDir.toAbsolutePath())) {
            files.filter(path -> path.getFileName().toString().startsWith("uploadFile-"))
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
