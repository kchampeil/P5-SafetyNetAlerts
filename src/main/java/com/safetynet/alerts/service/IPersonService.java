package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;

import java.util.List;

public interface IPersonService {

    /**
     * save a list of persons in DB
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    boolean saveListOfPersons(List<Person> listOfPersons);
}
