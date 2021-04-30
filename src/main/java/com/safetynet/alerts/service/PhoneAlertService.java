package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
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
public class PhoneAlertService implements IPhoneAlertService {
    
    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     * allow getting the list of all phone numbers for citizens
     * covered by a given fire station found in repository
     *
     * @param stationNumber the fire station number we want to get the citizen' phone numbers from
     * @return a list of phone numbers
     */
    @Override
    public List<String> getPhoneAlertByFireStation(Integer stationNumber) {
        if (stationNumber != null) {
            try {
                List<String> listOfPhoneNumbers = new ArrayList<>();

                //get the list of persons living in the area of the fire station
                List<Person> listOfPersons = personRepository.findAllByFireStation_StationNumber(stationNumber);

                //for each person, if listOfPhoneNumbers does not already contains this phone number,
                // add the phone number in the list
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the area covered by fire station n°: " + stationNumber);
                    for (Person person : listOfPersons) {
                        if (!listOfPhoneNumbers.contains(person.getPhone())) {
                            listOfPhoneNumbers.add(person.getPhone());
                        }
                    }
                    log.info(listOfPhoneNumbers.size()
                            + " distinct phone numbers found for the area covered by fire station n°: " + stationNumber);
                } else {
                    log.warn("no person found in the area covered by fire station n°: "
                            + stationNumber
                            + ", list of phone numbers is empty");
                }
                return listOfPhoneNumbers;


            } catch (Exception exception) {
                log.error("error when getting the list of phone numbers for the area covered by fire station n°: "
                        + stationNumber + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a fire station number must be specified to get the list of phone numbers");
            return null;
        }
    }

}
