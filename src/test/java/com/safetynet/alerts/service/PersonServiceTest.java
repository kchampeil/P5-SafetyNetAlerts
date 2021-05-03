package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class PersonServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private PersonService personService;

    private Person person;

    private final static LocalDate ADULT_BIRTHDATE = LocalDate.of(1999, 9, 9);
    private final static LocalDate CHILD_BIRTHDATE = LocalDate.of(2019, 1, 1);
    private final static int ADULT_AGE = 21;

    @BeforeAll
    private static void setUp() {

    }

    @BeforeEach
    private void setUpPerTest() {
        person = new Person();
        person.setFirstName("PST_first_name");
        person.setLastName("PST_last_name");
        person.setEmail("PST_email");
        person.setCity("PST_city");
        person.setZip("PST_zip");
        person.setAddress("PST_address");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(person.getFirstName());
        medicalRecord.setLastName(person.getLastName());
        medicalRecord.setBirthDate(ADULT_BIRTHDATE);

        List<String> medications = new ArrayList<>();
        medications.add("PST_medications_1");
        medications.add("PST_medications_2");
        medications.add("PST_medications_3");
        medicalRecord.setMedications(medications);

        List<String> allergies = new ArrayList<>();
        allergies.add("PST_allergies_1");
        allergies.add("PST_allergies_2");
        medicalRecord.setAllergies(allergies);

        person.setMedicalRecord(medicalRecord);
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfPersons tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("saveListOfPersons tests")
    class saveListOfPersonsTest {
        @Test
        @DisplayName("GIVEN a consistent list of persons " +
                "THEN it is saved in repository and return code is true")
        public void saveListOfPersonsTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);

            //THEN
            assertTrue(personService.saveListOfPersons(listOfPersons));
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "THEN no data is saved in repository and return code is false")
        public void saveListOfPersonsTest_WithException() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.saveAll(listOfPersons)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertFalse(personService.saveListOfPersons(listOfPersons));
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllPersons tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllPersons tests")
    class GetAllPersonsTest {
        @Test
        @DisplayName("GIVEN persons in repository WHEN processing a GET /persons request " +
                "THEN a list of persons is returned")
        public void getAllPersonsTest_WithPersonDataInRepository() {
            //GIVEN
            List<Person> expectedListOfPersons = new ArrayList<>();
            expectedListOfPersons.add(person);
            when(personRepositoryMock.findAll()).thenReturn(expectedListOfPersons);

            //THEN
            assertEquals(expectedListOfPersons, personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an exception WHEN processing a GET /persons request " +
                "THEN null is returned")
        public void getAllPersonsTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllEmailsByCity tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllEmailsByCity tests")
    class GetAllEmailsByCityTest {
        @Test
        @DisplayName("GIVEN citizens' emails in repository for the requested city" +
                "WHEN processing a GET /communityEmail request " +
                "THEN a list of citizens' emails is returned")
        public void getAllEmailsByCityTest_WithInfoInRepository() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.findAllByCity("PST_city")).thenReturn(listOfPersons);

            //THEN
            assertEquals(1,
                    personService.getAllEmailsByCity("PST_city").size());
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity("PST_city");
        }

        @Test
        @DisplayName("GIVEN no citizens' emails in repository for the requested city " +
                "WHEN processing a GET /communityEmail request " +
                "THEN an empty list of citizens' emails is returned")
        public void getAllEmailsByCityTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByCity("PST_city_not_in_repository")).thenReturn(new ArrayList<>());

            //THEN
            assertThat(personService.getAllEmailsByCity("PST_city_not_in_repository")).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity("PST_city_not_in_repository");
        }

        @Test
        @DisplayName("GIVEN a null city name " +
                "WHEN processing a GET /communityEmail request " +
                "THEN no list is returned")
        public void getAllEmailsByCityTest_WithCityNameNull() {
            //GIVEN
            when(personRepositoryMock.findAllByCity(null)).thenReturn(null);

            //THEN
            assertThat(personService.getAllEmailsByCity(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByCity(null);
        }

        @Test
        @DisplayName("GIVEN an empty city name " +
                "WHEN processing a GET /communityEmail request " +
                "THEN no list is returned")
        public void getAllEmailsByCityTest_WithCityNameEmpty() {
            //GIVEN
            when(personRepositoryMock.findAllByCity("")).thenReturn(null);

            //THEN
            assertThat(personService.getAllEmailsByCity("")).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByCity("");
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPersonInfoByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPersonInfoByFirstNameAndLastName tests")
    class GetPersonInfoByFirstNameAndLastNameTest {

        @Test
        @DisplayName("GIVEN persons in repository for the requested firstname+lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN a list of person information is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPerson = new ArrayList<>();
            listOfPerson.add(person);

            List<PersonInfoDTO> expectedListOfPersonInfo = new ArrayList<>();
            PersonInfoDTO personInfoDTO = new PersonInfoDTO();
            personInfoDTO.setLastName(person.getLastName());
            personInfoDTO.setAddress(person.getAddress());
            personInfoDTO.setAge(ADULT_AGE);
            personInfoDTO.setEmail(person.getEmail());
            personInfoDTO.setMedications(person.getMedicalRecord().getMedications());
            personInfoDTO.setAllergies(person.getMedicalRecord().getAllergies());
            expectedListOfPersonInfo.add(personInfoDTO);

            when(personRepositoryMock.findAllByFirstNameAndLastName("TestFirstName", "TestLastName")).thenReturn(listOfPerson);

            //THEN
            assertEquals(expectedListOfPersonInfo, personService.getPersonInfoByFirstNameAndLastName("TestFirstName", "TestLastName"));
            verify(personRepositoryMock, Mockito.times(1)).findAllByFirstNameAndLastName("TestFirstName", "TestLastName");

        }

        @Test
        @DisplayName("GIVEN firstname and lastname not found in repository " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is empty")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoDataInRepository() {

            //GIVEN
            when(personRepositoryMock.findAllByFirstNameAndLastName("TestFirstName", "TestLastName")).thenReturn(null);

            //THEN
            assertThat(personService.getPersonInfoByFirstNameAndLastName("TestFirstName", "TestLastName")).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByFirstNameAndLastName("TestFirstName", "TestLastName");
        }

        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNullRequestParameters() {

            assertNull(personService.getPersonInfoByFirstNameAndLastName(null, null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);

        }

        @Test
        @DisplayName("GIVEN empty firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithEmptyRequestParameters() {

            assertNull(personService.getPersonInfoByFirstNameAndLastName("", ""));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName("", "");

        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "THEN no data is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAllByFirstNameAndLastName(anyString(), anyString()))
                    .thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.getPersonInfoByFirstNameAndLastName("", ""));
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(anyString(), anyString());

        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getChildAlertByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getChildAlertByAddress tests")
    class GetChildAlertByAddressTest {

        @Test
        @DisplayName("GIVEN children in repository for the requested address " +
                "WHEN getting child alert on address " +
                "THEN a list of child alert information is returned")
        public void getChildAlertByAddressTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPerson = new ArrayList<>();

            Person aChild = new Person();
            aChild.setFirstName("PST_first_name");
            aChild.setLastName("PST_last_name");
            aChild.setEmail("PST_email");
            aChild.setAddress("PST_address");

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setFirstName(aChild.getFirstName());
            medicalRecord.setLastName(aChild.getLastName());
            medicalRecord.setBirthDate(CHILD_BIRTHDATE);
            aChild.setMedicalRecord(medicalRecord);
            
            listOfPerson.add(aChild);

            Person hisParent = new Person();
            hisParent.setFirstName("PST_first_name_parent");
            hisParent.setLastName("PST_last_name");
            hisParent.setEmail("PST_email_parent");
            hisParent.setPhone("PST_phone_parent");
            hisParent.setAddress(aChild.getAddress());

            MedicalRecord medicalRecord2 = new MedicalRecord();
            medicalRecord2.setFirstName(hisParent.getFirstName());
            medicalRecord2.setLastName(hisParent.getLastName());
            medicalRecord2.setBirthDate(ADULT_BIRTHDATE);
            hisParent.setMedicalRecord(medicalRecord2);
            listOfPerson.add(hisParent);

            // TTR List<ChildAlertDTO> expectedListOfChildAlert = new ArrayList<>();
            //TTR expectedListOfChildAlert.add(personInfoDTO);

            when(personRepositoryMock.findAllByAddress(aChild.getAddress())).thenReturn(listOfPerson);

            //WHEN
            List<ChildAlertDTO> returnedListOfChildAlert = personService.getChildAlertByAddress(aChild.getAddress());

            //THEN
            assertEquals(1, returnedListOfChildAlert.size());
            assertEquals(aChild.getFirstName(), returnedListOfChildAlert.get(0).getFirstName());
            assertEquals(aChild.getLastName(), returnedListOfChildAlert.get(0).getLastName());
            assertEquals(1, returnedListOfChildAlert.get(0).getListOfOtherHouseholdMembers().size());
            assertEquals(hisParent.getFirstName(), returnedListOfChildAlert.get(0).getListOfOtherHouseholdMembers().get(0).getFirstName());
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress(aChild.getAddress());

        }

        @Test
        @DisplayName("GIVEN no children in repository for the requested address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is empty")
        public void getChildAlertByAddressTest_WithNoDataInRepository() {

            //GIVEN
            when(personRepositoryMock.findAllByAddress("TestAddress")).thenReturn(null);

            //THEN
            assertThat(personService.getChildAlertByAddress("TestAddress")).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress("TestAddress");
        }

        @Test
        @DisplayName("GIVEN null address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getChildAlertByAddressTest_WithNullRequestParameters() {

            assertNull(personService.getChildAlertByAddress(null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(null);

        }

        @Test
        @DisplayName("GIVEN empty address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getChildAlertByAddressTest_WithEmptyRequestParameters() {

            assertNull(personService.getChildAlertByAddress(""));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress("");

        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "THEN no data is returned")
        public void getChildAlertByAddressTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAllByAddress(anyString()))
                    .thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.getChildAlertByAddress(""));
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByAddress(anyString());

        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPhoneAlertByFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPhoneAlertByFireStation tests")
    class GetPhoneAlertByFireStationTest {
        @Test
        @DisplayName("GIVEN citizens covered by the requested fire station found in repository " +
                "WHEN asking for the phone number list " +
                "THEN a list of citizens' phone numbers covered by the fire station is returned")
        public void getPhoneAlertByFireStationTest_WithInfoInRepository() {
            //GIVEN 3 persons but only 2 distinct phone numbers
            List<Person> listOfPersons = new ArrayList<>();

            Person person1 = new Person();
            person1.setFirstName("PST_first_name_1");
            person1.setLastName("PST_last_name_1");
            person1.setPhone("33 1 23 45 67 89");
            listOfPersons.add(person1);

            Person person2 = new Person();
            person2.setFirstName("PST_first_name_2");
            person2.setLastName("PST_last_name_2");
            person2.setPhone("33 1 98 76 54 32");
            listOfPersons.add(person2);

            Person person3 = new Person();
            person3.setFirstName("PST_first_name_3");
            person3.setLastName("PST_last_name_3");
            person3.setPhone("33 1 23 45 67 89");
            listOfPersons.add(person3);

            when(personRepositoryMock.findAllByFireStation_StationNumber(3)).thenReturn(listOfPersons);

            //THEN
            assertEquals(2,
                    personService.getPhoneAlertByFireStation(3).size());
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(3);
        }

        @Test
        @DisplayName("GIVEN no citizens covered by the requested fire station found in repository " +
                "WHEN asking for the phone number list " +
                "THEN the returned list is empty")
        public void getPhoneAlertByFireStationTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(999)).thenReturn(new ArrayList<>());

            //THEN
            assertThat(personService.getPhoneAlertByFireStation(999)).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(999);
        }

        @Test
        @DisplayName("GIVEN a null fire station number " +
                "WHEN asking for the phone number list " +
                "THEN no list is returned")
        public void getPhoneAlertByFireStationTest_WithFireStationNumberNull() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(null)).thenReturn(null);

            //THEN
            assertThat(personService.getPhoneAlertByFireStation(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByFireStation_StationNumber(null);
        }

    }

}
