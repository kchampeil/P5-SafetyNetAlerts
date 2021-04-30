package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.service.IChildAlertService;
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
public class ChildAlertController {

    @Autowired
    private IChildAlertService childAlertService;

    /**
     * Read - Get child alert for a given address
     *
     * @param address the address we want to get the child alert from
     * @return - A list of ChildAlertDTO
     */
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildAlertDTO>> getChildAlertByAddress(@RequestParam String address) {

        log.info("GET request on endpoint /childAlert received for address : " + address);
        List<ChildAlertDTO> returnedListOfChildAlert
                = childAlertService.getChildAlertByAddress(address);

        if (returnedListOfChildAlert == null) {
            log.error("error when getting the child alert for address " + address);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfChildAlert.isEmpty()) {
                log.warn("response to GET request on endpoint /childAlert for address "
                        + address + " is empty, no child found at this address");
                return new ResponseEntity<>(returnedListOfChildAlert, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /childAlert sent for address "
                        + address + " with " + returnedListOfChildAlert.size() + " values");
                return new ResponseEntity<>(returnedListOfChildAlert, HttpStatus.OK);
            }
        }

    }
}
