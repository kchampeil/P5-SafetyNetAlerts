package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
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

@Data
@Service
public class PersonInfoService implements IPersonInfoService {

    private static final Logger logger = LogManager.getLogger(PersonInfoService.class);
    private static final DateUtil dateUtil = new DateUtil();

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * allow getting the list of person information found in repository
     * for given firstname and lastname
     * @param firstName
     * @param lastName
     * @return a list of person information
     */
    @Override
    public List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName) {
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {
            try {
                //get the list of persons with firstName and lastName
                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(firstName, lastName);

                // and populate the listOfPersonInfoDTO
                List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {

                    for (Person person : listOfPersons) {
                        PersonInfoDTO personInfoDTO = new PersonInfoDTO();
                        personInfoDTO.setLastName(person.getLastName());
                        personInfoDTO.setAddress(person.getAddress());
                        personInfoDTO.setEmail(person.getEmail());
                        personInfoDTO.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                        personInfoDTO.setMedications(person.getMedicalRecord().getMedications());
                        personInfoDTO.setAllergies(person.getMedicalRecord().getAllergies());

                        listOfPersonInfoDTO.add(personInfoDTO);
                    }

                } else {
                    logger.error("no person found for firstname " + firstName +
                            " and lastname " + lastName +
                            ", list of person information is empty");
                }
                return listOfPersonInfoDTO;

            } catch (Exception exception) {
                logger.error("error when getting the list of person information " +
                        "for firstname " + firstName +
                        " and lastname " + lastName + " : " + exception.getMessage());
                return null;
            }
        } else {
            logger.error("firstname AND lastname must be specified to get the list of person information");
            return null;
        }
    }
}
