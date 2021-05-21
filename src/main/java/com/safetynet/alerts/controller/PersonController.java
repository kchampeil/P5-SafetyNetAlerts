package com.safetynet.alerts.controller;

import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.IPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@Slf4j
@RestController
public class PersonController {

    private final IPersonService personService;

    @Autowired
    public PersonController(IPersonService personService) {
        this.personService = personService;
    }


    /**
     * Read - Get all persons
     *
     * @return - An Iterable object of Person
     */
    @GetMapping("/persons")
    public ResponseEntity<Iterable<PersonDTO>> getAllPersons() {
        log.info("GET request on endpoint /persons received");

        List<PersonDTO> listOfPersonsDTO = (List<PersonDTO>) personService.getAllPersons();

        //TODO-review ne garder que le else (et changer tests associer)
        if (listOfPersonsDTO.isEmpty()) {
            log.warn("response to GET request on endpoint /persons is empty, " +
                    "no person found \n");
            return new ResponseEntity<>(listOfPersonsDTO, HttpStatus.NOT_FOUND);

        } else {
            log.info("response to GET request on endpoint /persons sent with "
                    + listOfPersonsDTO.size() + " values \n");
            return new ResponseEntity<>(listOfPersonsDTO, HttpStatus.OK);
        }
    }


    /**
     * Read - Get all emails for a given city
     *
     * @param cityName of the city we want citizens' emails
     * @return - A list of emails
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getAllEmailsByCity(@RequestParam("city") String cityName) {

        log.info("GET request on endpoint /communityEmail received for city " + cityName);

        List<String> returnedListOfEmails = personService.getAllEmailsByCity(cityName);

        if (returnedListOfEmails == null) {
            log.error("error when getting the list of emails for city " + cityName + "\n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfEmails.isEmpty()) {
                log.warn("response to GET request on endpoint /communityEmail for city "
                        + cityName + " is empty, no person found \n");
                return new ResponseEntity<>(returnedListOfEmails, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /communityEmail sent for city "
                        + cityName + " with " + returnedListOfEmails.size() + " values \n");
                return new ResponseEntity<>(returnedListOfEmails, HttpStatus.OK);
            }
        }
    }


    /**
     * Read - Get information on persons for a given firstname and lastname
     * If other persons have the same lastname, they will be in the list.
     *
     * @param firstName of the person(s) we want related information
     * @param lastName  of the person(s) we want related information
     * @return - A list of PersonInfoDTO
     */
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByFirstNameAndLastName(
            @RequestParam String firstName, @RequestParam String lastName) {

        log.info("GET request on endpoint /personInfo received for person(s) named : " + firstName + " " + lastName);
        List<PersonInfoDTO> returnedListOfPersonInfo
                = personService.getPersonInfoByFirstNameAndLastName(firstName, lastName);

        if (returnedListOfPersonInfo == null) {
            log.error("error when getting the person information for " + firstName + " " + lastName + "\n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfPersonInfo.isEmpty()) {
                log.warn("response to GET request on endpoint /personInfo for person "
                        + firstName + " " + lastName + " is empty, no person information found \n");
                return new ResponseEntity<>(returnedListOfPersonInfo, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /personInfo sent for person(s) "
                        + firstName + " " + lastName + " with " + returnedListOfPersonInfo.size() + " values \n");
                return new ResponseEntity<>(returnedListOfPersonInfo, HttpStatus.OK);
            }
        }

    }


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
                = personService.getChildAlertByAddress(address);

        if (returnedListOfChildAlert == null) {
            log.error("error when getting the child alert for address " + address + "\n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfChildAlert.isEmpty()) {
                log.warn("response to GET request on endpoint /childAlert for address "
                        + address + " is empty, no child found at this address \n");
                return new ResponseEntity<>(returnedListOfChildAlert, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /childAlert sent for address "
                        + address + " with " + returnedListOfChildAlert.size() + " values \n");
                return new ResponseEntity<>(returnedListOfChildAlert, HttpStatus.OK);
            }
        }

    }


    /**
     * Read - Get unique phone numbers of people covered by a given fire station
     *
     * @param stationNumber of the fire station
     * @return - A list of phone numbers
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlertByFireStation(@RequestParam("firestation") Integer stationNumber) {

        log.info("GET request on endpoint /phoneAlert received for fire station n°: " + stationNumber + "\n");

        List<String> returnedListOfPhoneAlert
                = personService.getPhoneAlertByFireStation(stationNumber);

        if (returnedListOfPhoneAlert == null) {
            log.error("error when getting the phone alert for fire station n°" + stationNumber + "\n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfPhoneAlert.isEmpty()) {
                log.warn("response to GET request on endpoint /phoneAlert for fire station n°"
                        + stationNumber + " is empty, no phone information found \n");
                return new ResponseEntity<>(returnedListOfPhoneAlert, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /phoneAlert sent for for fire station n° "
                        + stationNumber + " with " + returnedListOfPhoneAlert.size() + " values \n");
                return new ResponseEntity<>(returnedListOfPhoneAlert, HttpStatus.OK);
            }
        }
    }


    /**
     * Read - Get person information about people covered by a given fire station
     * and the number of adults and children concerned
     *
     * @param stationNumber the station number of the fire station we want to get the information from
     * @return - A FireStationCoverageDTO filled with information
     */
    @GetMapping("/firestation")
    public ResponseEntity<FireStationCoverageDTO> getFireStationCoverageByAddress(@RequestParam("stationNumber") Integer stationNumber) {

        log.info("GET request on endpoint /firestation received for fire station n°: " + stationNumber + "\n");

        FireStationCoverageDTO fireStationCoverageDTO
                = personService.getFireStationCoverageByStationNumber(stationNumber);

        if (fireStationCoverageDTO == null) {
            log.error("error when getting the fire station coverage for fire station n°: " + stationNumber + "\n");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {

            if (fireStationCoverageDTO.getPersonCoveredContactsDTOList() == null) {
                log.warn("response to GET request on endpoint /firestation for fire station n°: "
                        + stationNumber + " is empty, no fire station coverage information found \n");
                return new ResponseEntity<>(fireStationCoverageDTO, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /firestation sent for for fire station n°: "
                        + stationNumber + " with " + fireStationCoverageDTO.getPersonCoveredContactsDTOList().size() + " values \n");
                return new ResponseEntity<>(fireStationCoverageDTO, HttpStatus.OK);
            }
        }
    }


    /**
     * Create - Post a new person
     *
     * @param personDTOToAdd to add to repository
     * @return the added PersonDTO
     */

    /*TODO-review @GetMapping(value="/apir/tartanpion",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)*/
    @PostMapping(value = "/person")
    public ResponseEntity<PersonDTO> addPerson(@RequestBody PersonDTO personDTOToAdd) {

        log.info("POST request on endpoint /person received for person "
                + personDTOToAdd.getFirstName() + " " + personDTOToAdd.getLastName());

        try {
            PersonDTO addedPerson = personService.addPerson(personDTOToAdd);

            if (addedPerson != null) {
                log.info("new person " + personDTOToAdd.getFirstName() + personDTOToAdd.getLastName() + " has been saved "
                        + " with id: " + addedPerson.getPersonId() + "\n");
                return new ResponseEntity<>(addedPerson, HttpStatus.CREATED);
            } else {
                log.error("new person " + personDTOToAdd + " has not been saved\n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error(e.getMessage() + "\n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    /**
     * Update - Put a person for a given firstname+lastname
     *
     * @param personDTOToUpdate to update in repository
     * @return the added PersonDTO
     */
    @PutMapping(value = "/person")
    public ResponseEntity<PersonDTO> updatePerson(@RequestBody PersonDTO personDTOToUpdate) {

        log.info("PUT request on endpoint /person received for person "
                + personDTOToUpdate.getFirstName() + " " + personDTOToUpdate.getLastName());

        try {
            PersonDTO updatedPersonDTO = personService.updatePerson(personDTOToUpdate);

            if (updatedPersonDTO != null) {
                log.info("Person " + personDTOToUpdate.getFirstName() + personDTOToUpdate.getLastName() + " has been updated "
                        + " with id: " + updatedPersonDTO.getPersonId() + "\n");
                return new ResponseEntity<>(updatedPersonDTO, HttpStatus.OK);
            } else {
                log.error("Person " + personDTOToUpdate + " has not been updated \n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    /**
     * Delete - Delete a person for a firstname+lastname
     *
     * @param firstName the firstname of the person we want to delete
     * @param lastName  the lastname of the person we want to delete
     * @return Http status
     */
    @DeleteMapping(value = "/person")
    public ResponseEntity<?> deletePersonByFirstNameAndLastName(
            @RequestParam String firstName, @RequestParam String lastName) {

        log.info("DELETE request on endpoint /person received for person: " + firstName + " " + lastName);

        try {
            Person deletedPerson = personService.deletePersonByFirstNameAndLastName(firstName, lastName);

            if (deletedPerson != null) {
                log.info("Person with id :" + deletedPerson.getPersonId()
                        + " has been deleted for " + deletedPerson.getFirstName()
                        + " " + deletedPerson.getLastName() + " \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            } else {
                log.info("No person has been deleted for : " + firstName + " " + lastName + " \n");
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
