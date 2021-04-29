package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @param address the address we want to get the child alert from
     * @return a list of child alert
     */
    @Override
    public List<ChildAlertDTO> getChildAlertByAddress(String address) {
        if (address != null && !address.equals("")) {
            try {
                //get the list of persons living at this address
                List<Person> listOfPersons = personRepository.findAllByAddress(address);

                List<ChildAlertDTO> listOfChildAlertDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {

                    // complete information with calculation of their age
                    // and filter on age under the MAX_AGE_FOR_CHILD_ALERT
                    listOfPersons.forEach(person ->
                            person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate())));
                    List<Person> listOfChildren = listOfPersons.stream()
                            .filter(person -> person.getAge() <= MAX_AGE_FOR_CHILD_ALERT)
                            .collect(Collectors.toList());

                    // if at least one person is under the MAX_AGE_FOR_CHILD_ALERT, populate the listOfChildAlertDTO
                    if (!listOfChildren.isEmpty()) {
                        listOfChildren.forEach(child
                                -> listOfChildAlertDTO.add(mapToChildrenAlertDTO(child, listOfPersons)));
                    } else {
                        logger.info("no child under " + MAX_AGE_FOR_CHILD_ALERT + " found for address " + address +
                                ", list of child alert is empty");
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
     * map the child information to the ChildAlertDTO
     *
     * @param child         person information to be mapped to childAlertDTO
     * @param listOfPersons list of person living at the address
     * @return a ChildAlertDTO
     */
    private ChildAlertDTO mapToChildrenAlertDTO(Person child, List<Person> listOfPersons) {
        ModelMapper modelMapper = new ModelMapper();

        ChildAlertDTO childAlertDTO = modelMapper.map(child, ChildAlertDTO.class);
        childAlertDTO.setListOfOtherHouseholdMembers(
                getListOfOtherHouseholdMembers(listOfPersons, child.getFirstName(), child.getLastName()));

        return childAlertDTO;
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

        return listOfPersons.stream()
                .filter(person -> person.getLastName().equals(lastName) && !person.getFirstName().equals(firstName))
                .collect(Collectors.toList());
    }
}
