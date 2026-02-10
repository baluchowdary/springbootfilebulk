package com.kollu.springbootfilebulk.util;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileDeletionTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Get the path from JobParameters
        String filePath = (String) chunkContext.getStepContext()
                .getJobParameters().get("fullPath");

        if (filePath != null) {
            Path path = Paths.get(filePath);
            if (Files.deleteIfExists(path)) {
                System.out.println("**********Tasklet: Deleted file " + filePath);
            }
        }

        return RepeatStatus.FINISHED;
    }
}