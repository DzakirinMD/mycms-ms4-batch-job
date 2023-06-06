package com.mycmsms4batchjob.configuration;

import com.mycmsms4batchjob.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilesUploadConfig {

    @Autowired
    FilesStorageService storageService;

    @Bean
    public void run() throws Exception {
        // comment to make the file persist
        storageService.deleteAll();
        storageService.init();
    }

}
