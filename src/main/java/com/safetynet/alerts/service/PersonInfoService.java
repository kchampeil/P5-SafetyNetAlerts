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
     *
     * @param firstName the firstname we want to get the person information from
     * @param lastName the lastname we want to get the person information from
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

                    listOfPersons.forEach(person
                            -> listOfPersonInfoDTO.add(mapToPersonInfoDTO(person)));

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

    /**
     * map the person information to the PersonInfoDTO
     *
     * @param person person information to be mapped to personInfoDTO
     * @return a PersonInfoDTO
     */
    private PersonInfoDTO mapToPersonInfoDTO(Person person) {
        PersonInfoDTO personInfoDTO = new PersonInfoDTO();
        personInfoDTO.setLastName(person.getLastName());
        personInfoDTO.setAddress(person.getAddress());
        personInfoDTO.setEmail(person.getEmail());
        personInfoDTO.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
        personInfoDTO.setMedications(person.getMedicalRecord().getMedications());
        personInfoDTO.setAllergies(person.getMedicalRecord().getAllergies());

        return personInfoDTO;
    }
}
