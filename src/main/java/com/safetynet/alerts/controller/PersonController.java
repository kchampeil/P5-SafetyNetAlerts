package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.IPersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    private static final Logger logger = LogManager.getLogger(PersonController.class);

    @Autowired
    private IPersonService personService;


    /**
     * Read - Get all persons
     * @return - An Iterable object of Person full filled
     */
    @GetMapping("/persons")
    public Iterable<Person> getAllPersons() {
        logger.info("GET request on endpoint /persons received \n");
        return personService.getAllPersons();
    }

}
