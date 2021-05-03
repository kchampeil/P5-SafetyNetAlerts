package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.service.IFireStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FireStationController {

    @Autowired
    private IFireStationService fireStationService;


    /**
     * Read - Get all fire stations
     *
     * @return - An Iterable object of FireStation full filled
     */
    @GetMapping("/firestations")
    public Iterable<FireStation> getAllFireStations() {
        log.info("GET request on endpoint /firestations received \n");
        return fireStationService.getAllFireStations();
    }


    /**
     * Read - Get person information about people living at the given address
     * and the number of the fire station covering this address
     *
     * @param address the address we want to get the information from
     * @return - A FireDTO filled with information
     */
    @GetMapping("/fire")
    public ResponseEntity<FireDTO> getFireStationCoverageByAddress(@RequestParam String address) {

        log.info("GET request on endpoint /fire received for address: " + address);

        FireDTO fireDTO
                = fireStationService.getFireStationCoverageByAddress(address);

        if (fireDTO==null) {
            log.error("error when getting the fire station coverage for address: " + address);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {

            if (fireDTO.getPersonCoveredDTOList()==null) {
                log.warn("response to GET request on endpoint /fire for address: "
                        + address + " is empty, no fire station coverage information found");
                return new ResponseEntity<>(fireDTO, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /fire sent for for address: "
                        + address + " with " + fireDTO.getPersonCoveredDTOList().size() + " values");
                return new ResponseEntity<>(fireDTO, HttpStatus.OK);
            }
        }
    }
}
