package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.IPersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {

    private static final Logger logger = LogManager.getLogger(PersonController.class);

    @Autowired
    private IPersonService personService;


    /**
     * Read - Get all persons
     *
     * @return - An Iterable object of Person
     */
    @GetMapping("/persons")
    public Iterable<Person> getAllPersons() {
        logger.info("GET request on endpoint /persons received \n");
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

        logger.info("GET request on endpoint /communityEmail received for city " + cityName);

        List<String> returnedListOfEmails = personService.getAllEmailsByCity(cityName);

        if (returnedListOfEmails == null) {
            logger.error("error when getting the list of emails for city " + cityName);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfEmails.isEmpty()) {
                logger.warn("response to GET request on endpoint /communityEmail for city "
                        + cityName + " is empty, no person found");
                return new ResponseEntity<>(returnedListOfEmails, HttpStatus.NOT_FOUND);

            } else {
                logger.info("response to GET request on endpoint /communityEmail sent for city "
                        + cityName + " with " + returnedListOfEmails.size() + " values");
                return new ResponseEntity<>(returnedListOfEmails, HttpStatus.OK);
            }
        }
    }

}
