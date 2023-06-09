package com.mycmsms4batchjob.configuration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JobCompletionListener extends JobExecutionListenerSupport {

    @Value("${output.folder}") // Specify the output folder location in application.properties
    private String outputFolder;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            try {
                // Move the file to the output folder
                String sourceFilePath = "src/main/resources/preprocess/customers.csv";
                String targetFilePath = outputFolder + "/customers.csv";
                Path source = new File(sourceFilePath).toPath();
                Path target = new File(targetFilePath).toPath();
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File moved successfully to: " + targetFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
