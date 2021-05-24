package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    /**
     * save a list of persons in DB
     *
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    Iterable<Person> saveListOfPersons(List<Person> listOfPersons);

    /**
     * allow getting the list of all persons found in DB
     *
     * @return a list of Person
     */
    Iterable<PersonDTO> getAllPersons();

    /**
     * allow getting the list of all citizens' emails for a given city found in DB
     *
     * @param cityName of the city we want citizens' emails
     * @return a list of emails
     */
    List<String> getAllEmailsByCity(String cityName);

    /**
     * allow getting the list of person information found in repository
     * for given firstname and lastname
     * If other persons have the same lastname, they will be in the list.
     *
     * @param firstName the firstname we want to get the person information from
     * @param lastName  the lastname we want to get the person information from
     * @return a list of person information
     */
    List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName);

    /**
     * allow getting the list of child alert found in repository for given address
     *
     * @param address the address we want to get the child alert from
     * @return a list of child alert
     */
    List<ChildAlertDTO> getChildAlertByAddress(String address);

    /**
     * allow getting the list of all phone numbers for citizens
     * covered by a given fire station found in repository
     *
     * @param stationNumber the fire station number we want to get the citizen' phone numbers from
     * @return a list of phone numbers
     */
    List<String> getPhoneAlertByFireStation(Integer stationNumber);

    /**
     * allow getting the list of information of all citizens
     * covered by a given fire station found in repository
     * completed with a count of adults and children
     *
     * @param stationNumber the fire station number we want to get the citizen' information from
     * @return a list of information of all citizens covered by a given fire station found in repository
     * completed with a count of adults and children
     */
    FireStationCoverageDTO getFireStationCoverageByStationNumber(Integer stationNumber);

    /**
     * save a new person in the repository
     *
     * @param personToAdd a new person to add
     * @return the added Person
     * @throws AlreadyExistsException      if the person to add already exists in repository
     * @throws MissingInformationException if there are missing properties for the person to save
     */
    Optional<PersonDTO> addPerson(PersonDTO personToAdd) throws Exception, AlreadyExistsException, MissingInformationException;

    /**
     * update a person of a given firstname+lastname
     *
     * @param personDTOToUpdate a person to update
     * @return the updated person
     * @throws DoesNotExistException       if the person to update does not exist in repository
     * @throws MissingInformationException if there are missing properties for the person to update
     */
    Optional<PersonDTO> updatePerson(PersonDTO personDTOToUpdate) throws DoesNotExistException, MissingInformationException;

    /**
     * delete the person for the given firstname+lastname in the repository
     *
     * @param firstName the firstname of the person we want to delete
     * @param lastName the lastname of the person we want to delete
     * @return the deleted person
     * @throws DoesNotExistException       if no person has been found for the given firstname+lastname
     * @throws MissingInformationException if no firstname+lastname has been given
     */
    Person deletePersonByFirstNameAndLastName(String firstName, String lastName) throws DoesNotExistException, MissingInformationException;
}
