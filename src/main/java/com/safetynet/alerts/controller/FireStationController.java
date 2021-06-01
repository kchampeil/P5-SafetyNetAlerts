package com.safetynet.alerts.controller;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
import com.safetynet.alerts.service.IFireStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class FireStationController {

    private final IFireStationService fireStationService;

    @Autowired
    public FireStationController(IFireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }


    /**
     * Read - Get all fire stations
     *
     * @return - An Iterable object of FireStation full filled
     */
    @GetMapping(value = "/firestations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<FireStationDTO>> getAllFireStations() {
        log.info("GET request on endpoint /firestations received");

        List<FireStationDTO> listOfFireStationsDTO = (List<FireStationDTO>) fireStationService.getAllFireStations();
        log.info("response to GET request on endpoint /firestations sent with "
                + listOfFireStationsDTO.size() + " values \n");
        return new ResponseEntity<>(listOfFireStationsDTO, HttpStatus.OK);
    }


    /**
     * Read - Get person information about people living at the given address
     * and the number of the fire station covering this address
     *
     * @param address the address we want to get the information from
     * @return - A FireDTO filled with information
     */
    @GetMapping(value = "/fire", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FireDTO> getFireStationCoverageByAddress(@RequestParam String address) {

        log.info("GET request on endpoint /fire received for address: " + address);

        FireDTO fireDTO
                = fireStationService.getFireStationCoverageByAddress(address);

        if (fireDTO == null) {
            log.error("error when getting the fire station coverage for address: " + address + " \n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            log.info("response to GET request on endpoint /fire sent for for address: "
                    + address + " with " + fireDTO.getPersonCoveredDTOList().size() + " values \n");
            return new ResponseEntity<>(fireDTO, HttpStatus.OK);
        }
    }


    /**
     * Read - Get person information about people covered by fire stations
     * for a given list of station number et grouped by station number and address
     *
     * @param listOfStationNumbers the address we want to get the information from
     * @return - A list of FloodDTO filled with information
     */
    @GetMapping(value = "/flood/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FloodDTO>> getFloodByStationNumbers(@RequestParam("stations") List<Integer> listOfStationNumbers) {

        log.info("GET request on endpoint /flood/stations received for station numbers: " + listOfStationNumbers);

        List<FloodDTO> listOfFloodDTO
                = fireStationService.getFloodByStationNumbers(listOfStationNumbers);

        if (listOfFloodDTO == null) {
            log.error("error when getting the flood for stations: " + listOfStationNumbers + " \n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            log.info("response to GET request on endpoint /flood/stations sent for for stations: "
                    + listOfStationNumbers + " with " + listOfFloodDTO.size() + " values \n");
            return new ResponseEntity<>(listOfFloodDTO, HttpStatus.OK);
        }
    }


    /**
     * Create - Post a new fire station
     *
     * @param fireStationDTOToAdd to add to repository
     * @return the added FireStationDTO
     */
    @PostMapping(value = "/firestation", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FireStationDTO> addFireStation(@RequestBody FireStationDTO fireStationDTOToAdd) {

        log.info("POST request on endpoint /firestation received for fire station n°"
                + fireStationDTOToAdd.getStationNumber());

        try {
            Optional<FireStationDTO> addedFireStationDTO = fireStationService.addFireStation(fireStationDTOToAdd);

            if (addedFireStationDTO.isPresent()) {
                log.info("address/fire station mapping has been updated for fire station n°"
                        + fireStationDTOToAdd.getStationNumber()
                        + " with id: " + addedFireStationDTO.get().getFireStationId() + " \n");
                return new ResponseEntity<>(addedFireStationDTO.get(), HttpStatus.CREATED);

            } else {
                log.error("address/fire station mapping has not been updated for fire station: "
                        + fireStationDTOToAdd + " \n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (AlreadyExistsException alreadyExistsException) {
            log.error(alreadyExistsException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.CONFLICT, alreadyExistsException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Update - Put a new station number for a given address
     *
     * @param fireStationDTOToUpdate to update in repository
     * @return the updated FireStationDTO
     */
    @PutMapping(value = "/firestation", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FireStationDTO> updateFireStation(@RequestBody FireStationDTO fireStationDTOToUpdate) {

        log.info("PUT request on endpoint /firestation received for address: "
                + fireStationDTOToUpdate.getAddress());

        try {
            Optional<FireStationDTO> updatedFireStationDTO = fireStationService.updateFireStation(fireStationDTOToUpdate);

            if (updatedFireStationDTO.isPresent()) {
                log.info("new station number has been saved for address : "
                        + fireStationDTOToUpdate.getAddress()
                        + " at id: " + updatedFireStationDTO.get().getFireStationId() + " \n");
                return new ResponseEntity<>(updatedFireStationDTO.get(), HttpStatus.OK);

            } else {
                log.error("new station number has not been saved for address: "
                        + fireStationDTOToUpdate.getAddress() + " \n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (DoesNotExistException doesNotExistException) {
            log.error(doesNotExistException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, doesNotExistException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Delete - Delete a fire station for a given address
     *
     * @param address the address we want to delete the fire station relationships
     * @return Http status
     */
    @DeleteMapping(value = "/firestation/address")
    public ResponseEntity<?> deleteFireStationByAddress(@RequestParam String address) {

        log.info("DELETE request on endpoint /firestation/address received for address: " + address);

        try {
            FireStation deletedFireStation = fireStationService.deleteFireStationByAddress(address);

            if (deletedFireStation != null) {
                log.info("fire station with id :" + deletedFireStation.getFireStationId()
                        + " and station number: " + deletedFireStation.getStationNumber()
                        + " has been deleted for address " + address + " \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            } else {
                log.info("No fire station has been deleted for address " + address + " \n");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (DoesNotExistException doesNotExistException) {
            log.error(doesNotExistException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, doesNotExistException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Delete - Delete a fire station for a given station number
     *
     * @param stationNumber the station number of the fire station we want to delete
     * @return Http status
     */
    @DeleteMapping(value = "/firestation/station")
    public ResponseEntity<?> deleteFireStationByStationNumber(@RequestParam("stationNumber") Integer stationNumber) {

        log.info("DELETE request on endpoint /firestation/station received for station n° " + stationNumber);

        try {
            List<FireStation> deletedFireStations = fireStationService.deleteFireStationByStationNumber(stationNumber);

            if (deletedFireStations != null && !deletedFireStations.isEmpty()) {
                log.info(deletedFireStations.size() + " fire stations have been deleted for station n°" + stationNumber + " \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            } else {
                log.info("No fire station has been deleted for station n°" + stationNumber + " \n");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (DoesNotExistException doesNotExistException) {
            log.error(doesNotExistException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, doesNotExistException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
