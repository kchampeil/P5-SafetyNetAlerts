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

    /**
     * allow getting the list of all persons found in DB
     * @return a list of Person
     */
    Iterable<Person> getAllPersons();

    /**
     * allow getting the list of all citizens' emails for a given city found in DB
     * @param cityName of the city we want citizens' emails
     * @return a list of emails
     */
    List<String> getAllEmailsByCity(String cityName);
}
