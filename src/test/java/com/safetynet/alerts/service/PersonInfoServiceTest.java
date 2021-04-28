package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class PersonInfoServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private PersonInfoService personInfoService;

    private static PersonInfoDTO personInfoDTO;
    private static Person person;

    private final static LocalDate PERSON_BIRTHDATE = LocalDate.of(1999, 9, 9);
    private final static int PERSON_AGE = 21;


    @BeforeAll
    private static void setUp() {

        person = new Person();
        person.setFirstName("PIST_first_name");
        person.setLastName("PIST_last_name");
        person.setEmail("PIST_email");
        person.setAddress("PIST_address");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(person.getFirstName());
        medicalRecord.setLastName(person.getLastName());
        medicalRecord.setBirthDate(PERSON_BIRTHDATE);

        List<String> medications = new ArrayList<>();
        medications.add("PIST_medications_1");
        medications.add("PIST_medications_2");
        medications.add("PIST_medications_3");
        medicalRecord.setMedications(medications);

        List<String> allergies = new ArrayList<>();
        allergies.add("PIST_allergies_1");
        allergies.add("PIST_allergies_2");
        medicalRecord.setAllergies(allergies);

        person.setMedicalRecord(medicalRecord);

        personInfoDTO = new PersonInfoDTO();
        personInfoDTO.setLastName(person.getLastName());
        personInfoDTO.setAddress(person.getAddress());
        personInfoDTO.setAge(PERSON_AGE);
        personInfoDTO.setEmail(person.getEmail());
        personInfoDTO.setMedications(person.getMedicalRecord().getMedications());
        personInfoDTO.setAllergies(person.getMedicalRecord().getAllergies());
    }

    @BeforeEach
    private void setUpPerTest() {

    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPersonInfoByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPersonInfoByFirstNameAndLastName tests")
    class getPersonInfoByFirstNameAndLastNameTest {

        @Test
        @DisplayName("GIVEN persons in repository for the requested firstname+lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN a list of person information is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPerson = new ArrayList<>();
            listOfPerson.add(person);

            List<PersonInfoDTO> expectedListOfPersonInfo = new ArrayList<>();
            expectedListOfPersonInfo.add(personInfoDTO);

            when(personRepositoryMock.findAllByFirstNameAndLastName("TestFirstName", "TestLastName")).thenReturn(listOfPerson);

            //THEN
            assertEquals(expectedListOfPersonInfo, personInfoService.getPersonInfoByFirstNameAndLastName("TestFirstName", "TestLastName"));
            verify(personRepositoryMock, Mockito.times(1)).findAllByFirstNameAndLastName("TestFirstName", "TestLastName");

        }

        @Test
        @DisplayName("GIVEN firstname and lastname not found in repository " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoDataInRepository() {

            //GIVEN
            when(personRepositoryMock.findAllByFirstNameAndLastName("TestFirstName", "TestLastName")).thenReturn(null);

            //THEN
            assertNull(personInfoService.getPersonInfoByFirstNameAndLastName("TestFirstName", "TestLastName"));
            verify(personRepositoryMock, Mockito.times(1)).findAllByFirstNameAndLastName("TestFirstName", "TestLastName");
        }

        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNullRequestParameters() {

            assertNull(personInfoService.getPersonInfoByFirstNameAndLastName(null, null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);

        }

        @Test
        @DisplayName("GIVEN empty firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithEmptyRequestParameters() {

            assertNull(personInfoService.getPersonInfoByFirstNameAndLastName("", ""));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName("", "");

        }

    }
}
