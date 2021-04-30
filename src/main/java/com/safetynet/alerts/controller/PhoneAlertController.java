package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.IPhoneAlertService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PhoneAlertController {

    @Autowired
    IPhoneAlertService phoneAlertService;

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlertByFireStation(@RequestParam("firestation") Integer stationNumber) {

        log.info("GET request on endpoint /phoneAlert received for fire station n°: " + stationNumber);

        List<String> returnedListOfPhoneAlert
                = phoneAlertService.getPhoneAlertByFireStation(stationNumber);

        if (returnedListOfPhoneAlert == null) {
            log.error("error when getting the phone alert for fire station n°" + stationNumber);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfPhoneAlert.isEmpty()) {
                log.warn("response to GET request on endpoint /phoneAlert for fire station n°"
                        + stationNumber + " is empty, no phone information found");
                return new ResponseEntity<>(returnedListOfPhoneAlert, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /phoneAlert sent for for fire station n° "
                        + stationNumber + " with " + returnedListOfPhoneAlert.size() + " values");
                return new ResponseEntity<>(returnedListOfPhoneAlert, HttpStatus.OK);
            }
        }
    }
}
