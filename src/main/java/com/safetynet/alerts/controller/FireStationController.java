package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
import com.safetynet.alerts.service.IFireStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<Iterable<FireStationDTO>> getAllFireStations() {
        log.info("GET request on endpoint /firestations received");

        List<FireStationDTO> listOfFireStationsDTO = (List<FireStationDTO>) fireStationService.getAllFireStations();

        if (listOfFireStationsDTO.isEmpty()) {
            log.warn("response to GET request on endpoint /firestations is empty, " +
                    "no fire station found \n");
            return new ResponseEntity<>(listOfFireStationsDTO, HttpStatus.NOT_FOUND);

        } else {
            log.info("response to GET request on endpoint /firestations sent with "
                    + listOfFireStationsDTO.size() + " values \n");
            return new ResponseEntity<>(listOfFireStationsDTO, HttpStatus.OK);
        }
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

        if (fireDTO == null) {
            log.error("error when getting the fire station coverage for address: " + address + " \n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {

            if (fireDTO.getPersonCoveredDTOList() == null) {
                log.warn("response to GET request on endpoint /fire for address: "
                        + address + " is empty, no fire station coverage information found \n");
                return new ResponseEntity<>(fireDTO, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /fire sent for for address: "
                        + address + " with " + fireDTO.getPersonCoveredDTOList().size() + " values \n");
                return new ResponseEntity<>(fireDTO, HttpStatus.OK);
            }
        }
    }


    /**
     * Read - Get person information about people covered by fire stations
     * for a given list of station number et grouped by station number and address
     *
     * @param listOfStationNumbers the address we want to get the information from
     * @return - A list of FloodDTO filled with information
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<List<FloodDTO>> getFloodByStationNumbers(@RequestParam("stations") List<Integer> listOfStationNumbers) {

        log.info("GET request on endpoint /flood/stations received for station numbers: " + listOfStationNumbers);

        List<FloodDTO> listOfFloodDTO
                = fireStationService.getFloodByStationNumbers(listOfStationNumbers);

        if (listOfFloodDTO == null) {
            log.error("error when getting the flood for stations: " + listOfStationNumbers + " \n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {

            if (listOfFloodDTO.isEmpty()) {
                log.warn("response to GET request on endpoint /flood/stations for stations: "
                        + listOfStationNumbers + " is empty, no flood information found \n");
                return new ResponseEntity<>(listOfFloodDTO, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /flood/stations sent for for stations: "
                        + listOfStationNumbers + " with " + listOfFloodDTO.size() + " values \n");
                return new ResponseEntity<>(listOfFloodDTO, HttpStatus.OK);
            }
        }
    }


    /**
     * Create - Post a new fire station
     *
     * @param fireStationDTOToAdd to add to repository
     * @return the added FireStationDTO
     */
    @PostMapping(value = "/firestation")
    public ResponseEntity<FireStationDTO> addFireStation(@RequestBody FireStationDTO fireStationDTOToAdd) {

        log.info("POST request on endpoint /firestation received for fire station n°"
                + fireStationDTOToAdd.getStationNumber());

        try {
            FireStationDTO addedFireStation = fireStationService.addFireStation(fireStationDTOToAdd);


            if (addedFireStation != null) {
                log.info("new address/fire station mapping has been saved for fire station n°"
                        + fireStationDTOToAdd.getStationNumber()
                        + " with id: " + addedFireStation.getFireStationId() + " \n");
                return new ResponseEntity<>(addedFireStation, HttpStatus.CREATED);

            } else {
                log.error("new address/fire station mapping has not been saved for fire station: "
                        + fireStationDTOToAdd + " \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            //TOASK comment remonter le message de l'exception ?
        }
    }

}
