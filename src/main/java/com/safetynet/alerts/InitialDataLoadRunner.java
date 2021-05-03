package com.safetynet.alerts;

import com.safetynet.alerts.service.IFileParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Allow the initial load of data when application is launched
 * NB : not launched for test profile
 */
@Profile("!test")
@Slf4j
@Component
public class InitialDataLoadRunner implements CommandLineRunner {
    
    @Autowired
    IFileParserService fileParserService;

    @Override
    public void run(String... args) throws Exception {
        log.info("initial load of data");
        fileParserService.readDataFromFile();
    }
}
