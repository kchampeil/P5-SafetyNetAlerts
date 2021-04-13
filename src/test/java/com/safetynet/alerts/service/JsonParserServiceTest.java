package com.safetynet.alerts.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JsonParserServiceTest {

    @Autowired
    private JsonParserService jsonParserService;

    @Test
    public void testJsonParserService() {
        jsonParserService.readDataFromJsonFile();
    }

}