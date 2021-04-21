package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.IFireStationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FireStationController {

    private static final Logger logger = LogManager.getLogger(FireStationController.class);

    @Autowired
    private IFireStationService fireStationService;


    /**
     * Read - Get all fire stations
     * @return - An Iterable object of FireStation full filled
     */
    @GetMapping("/firestations")
    public Iterable<FireStation> getAllFireStations() {
        logger.info("GET request on endpoint /firestations received \n");
        return fireStationService.getAllFireStations();
    }
}
