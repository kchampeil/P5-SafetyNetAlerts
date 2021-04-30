package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    /**
     * save a list of persons in DB
     *
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfPersons(List<Person> listOfPersons) {
        try {
            personRepository.saveAll(listOfPersons);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("error when saving the list of persons in DB : " + illegalArgumentException.getMessage() + "\n");
            return false;
        }
    }


    /**
     * allow getting the list of all persons found in DB
     *
     * @return a list of Person
     */
    @Override
    public Iterable<Person> getAllPersons() {
        try {
            return personRepository.findAll();
        } catch (Exception exception) {
            log.error("error when getting the list of all persons " + exception.getMessage() + "\n");
            return null;
        }
    }


    /**
     * allow getting the list of all citizens' emails for a given city found in DB
     *
     * @param cityName of the city we want citizens' emails
     * @return a list of emails
     */
    @Override
    public List<String> getAllEmailsByCity(String cityName) {
        if (cityName != null && !cityName.equals("")) {
            try {
                List<String> listOfEmails = new ArrayList<>();

                //get the list of persons living in the city called cityName
                List<Person> listOfPersons = personRepository.findAllByCity(cityName);

                //for each person, if listOfEmails does not already contains his email, add the email in the list
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the city : " + cityName);
                    for (Person person : listOfPersons) {
                        if (!listOfEmails.contains(person.getEmail())) {
                            listOfEmails.add(person.getEmail());
                        }
                    }
                    log.info(listOfEmails.size() + " distinct emails found for the city : " + cityName);
                    return listOfEmails;
                } else {
                    log.warn("no person found for city " + cityName + ", list of emails is empty");
                    return listOfEmails;
                }

            } catch (Exception exception) {
                log.error("error when getting the list of emails for city " + cityName + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a city name must be specified to get the list of emails");
            return null;
        }
    }
}
