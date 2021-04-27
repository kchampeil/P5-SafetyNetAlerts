package com.safetynet.alerts;

import com.safetynet.alerts.service.IFileParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Allow the initial load of data when application is launched
 * NB : not launched for test profile
 */
@Profile("!test")
@Component
public class InitialDataLoadRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoadRunner.class);

    @Autowired
    IFileParserService fileParserService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("initial load of data");
        fileParserService.readDataFromFile();
    }
}
