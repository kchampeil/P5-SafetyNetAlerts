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

    private final IFileParserService fileParserService;

    @Autowired
    public InitialDataLoadRunner(IFileParserService fileParserService) {
        this.fileParserService = fileParserService;
    }

    @Override
    public void run(String... args) {
        log.info("initial load of data");
        fileParserService.readDataFromFile();
    }
}
