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

    @Override
    public List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName) {
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {
            try {
                //get the list of persons with firstName and lastName
                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(firstName, lastName);

                if (listOfPersons != null && !listOfPersons.isEmpty()) {

                    List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();

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

                    return listOfPersonInfoDTO;

                } else {
                    logger.error("no person found for firstname " + firstName +
                            " and lastname " + lastName +
                            ", cannot return the list of person information");
                    return null;
                }

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