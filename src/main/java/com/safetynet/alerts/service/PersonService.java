package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class PersonService implements IPersonService {

    private static final Logger logger = LogManager.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    /**
     * save a list of persons in DB
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfPersons(List<Person> listOfPersons) {
             try {
                personRepository.saveAll(listOfPersons);
                return true;
            } catch (IllegalArgumentException e) {
                logger.error("error when saving the list of persons in DB : " + e.getMessage() + "\n");
                return false;
            }
    }
}
