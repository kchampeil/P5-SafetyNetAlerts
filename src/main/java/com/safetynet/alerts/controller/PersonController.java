package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.IPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PersonController {

    @Autowired
    private IPersonService personService;


    /**
     * Read - Get all persons
     *
     * @return - An Iterable object of Person
     */
    @GetMapping("/persons")
    public Iterable<Person> getAllPersons() {
        log.info("GET request on endpoint /persons received \n");
        return personService.getAllPersons();
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
            log.error("error when getting the list of emails for city " + cityName);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfEmails.isEmpty()) {
                log.warn("response to GET request on endpoint /communityEmail for city "
                        + cityName + " is empty, no person found");
                return new ResponseEntity<>(returnedListOfEmails, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /communityEmail sent for city "
                        + cityName + " with " + returnedListOfEmails.size() + " values");
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
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {

        log.info("GET request on endpoint /personInfo received for person(s) named : " + firstName + " " + lastName);
        List<PersonInfoDTO> returnedListOfPersonInfo
                = personService.getPersonInfoByFirstNameAndLastName(firstName, lastName);

        if (returnedListOfPersonInfo == null) {
            log.error("error when getting the person information for " + firstName + " " + lastName);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfPersonInfo.isEmpty()) {
                log.warn("response to GET request on endpoint /personInfo for person "
                        + firstName + " " + lastName + " is empty, no person information found");
                return new ResponseEntity<>(returnedListOfPersonInfo, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /personInfo sent for person(s) "
                        + firstName + " " + lastName + " with " + returnedListOfPersonInfo.size() + " values");
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


    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlertByFireStation(@RequestParam("firestation") Integer stationNumber) {

        log.info("GET request on endpoint /phoneAlert received for fire station n°: " + stationNumber);

        List<String> returnedListOfPhoneAlert
                = personService.getPhoneAlertByFireStation(stationNumber);

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
