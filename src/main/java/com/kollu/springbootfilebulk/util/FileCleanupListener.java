package com.kollu.springbootfilebulk.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FileCleanupListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Retrieve the path from the Job Parameters
        String pathString = jobExecution.getJobParameters().getString("tempFileFullPath");
        
        if (pathString != null) {
            try {
                Path path = Paths.get(pathString);
                // Delete the file only if it exists
                boolean deleted = Files.deleteIfExists(path);
                
                if (deleted) {
                    System.out.println("CLEANUP: Successfully deleted temp file: " + pathString);
                }
            } catch (IOException e) {
                System.err.println("CLEANUP ERROR: Could not delete temp file: " + pathString);
                e.printStackTrace();
            }
        }
    }
}
