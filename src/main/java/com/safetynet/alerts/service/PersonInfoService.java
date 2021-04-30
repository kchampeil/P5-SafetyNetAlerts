package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PersonInfoService implements IPersonInfoService {

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
     * @param lastName  the lastname we want to get the person information from
     * @return a list of person information
     */
    @Override
    public List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName) {
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {
            try {
                //get the list of persons with firstName and lastName
                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(firstName, lastName);

                // complete information with calculation of their age
                // and populate the listOfPersonInfoDTO
                List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    listOfPersons.forEach(person -> {
                        person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                        listOfPersonInfoDTO.add(mapToPersonInfoDTO(person));
                    });

                } else {
                    log.error("no person found for firstname " + firstName +
                            " and lastname " + lastName +
                            ", list of person information is empty");
                }
                return listOfPersonInfoDTO;

            } catch (Exception exception) {
                log.error("error when getting the list of person information " +
                        "for firstname " + firstName +
                        " and lastname " + lastName + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("firstname AND lastname must be specified to get the list of person information");
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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Person.class, PersonInfoDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getMedicalRecord().getMedications(), PersonInfoDTO::setMedications);
            mapper.map(src -> src.getMedicalRecord().getAllergies(), PersonInfoDTO::setAllergies);
        });

        return modelMapper.map(person, PersonInfoDTO.class);
    }
}
