package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.safetynet.alerts.constants.ChildAlertConstants.MAX_AGE_FOR_CHILD_ALERT;

@Data
@Service
public class ChildAlertService implements IChildAlertService {

    private static final Logger logger = LogManager.getLogger(PersonInfoService.class);
    private static final DateUtil dateUtil = new DateUtil();

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * allow getting the list of child alert found in repository for given address
     * @param address
     * @return a list of child alert
     */
    @Override
    public List<ChildAlertDTO> getChildAlertByAddress(String address) {
        if (address != null && !address.equals("")) {
            try {
                //get the list of persons living at this address
                List<Person> listOfPersons = personRepository.findAllByAddress(address);

                //and populate the listOfChildAlertDTO if at least one person is under the MAX_AGE_FOR_CHILD_ALERT
                List<ChildAlertDTO> listOfChildAlertDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {

                    for (Person person : listOfPersons) {
                        int age = dateUtil.calculateAge(person.getMedicalRecord().getBirthDate());

                        if (age <= MAX_AGE_FOR_CHILD_ALERT) {
                            ChildAlertDTO childAlertDTO = new ChildAlertDTO();
                            childAlertDTO.setFirstName(person.getFirstName());
                            childAlertDTO.setLastName(person.getLastName());
                            childAlertDTO.setAge(age);
                            childAlertDTO.setListOfOtherHouseholdMembers(
                                    getListOfOtherHouseholdMembers(listOfPersons, person.getFirstName(), person.getLastName()));

                            listOfChildAlertDTO.add(childAlertDTO);
                        }
                    }

                } else {
                    logger.error("no person found for address " + address +
                            ", list of child alert is empty");
                }
                return listOfChildAlertDTO;

            } catch (Exception exception) {
                logger.error("error when getting the list of person information " +
                        "for address " + address + " : " + exception.getMessage());
                return null;
            }
        } else {
            logger.error("address must be specified to get the list of child alert");
            return null;
        }
    }

    /**
     * return the list of household members for a given lastname
     * (assuming all household members have the same lastname...)
     *
     * @param listOfPersons a list of persons
     * @param lastName      the lastname we want to filter on
     * @return a list of persons having the same given lastname
     */
    private List<Person> getListOfOtherHouseholdMembers(List<Person> listOfPersons, String firstName, String lastName) {

        List<Person> ListOfOtherHouseholdMembers = new ArrayList<>();

        for (Person person : listOfPersons) {
            if (person.getLastName().equals(lastName) && !person.getFirstName().equals(firstName)) {
                ListOfOtherHouseholdMembers.add(person);
            }
        }
        return ListOfOtherHouseholdMembers;
    }
}
