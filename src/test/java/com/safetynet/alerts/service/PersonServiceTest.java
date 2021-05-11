package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.FireStationRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class PersonServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @MockBean
    private FireStationRepository fireStationRepositoryMock;

    @Autowired
    private IPersonService personService;

    private Person person;

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
        medicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);

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
                "WHEN saving the list of persons " +
                "THEN it is saved in repository and return code is true")
        public void saveListOfPersonsTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.saveAll(listOfPersons)).thenReturn(listOfPersons);

            //WHEN
            Iterable<Person> savedListOfPersons = personService.saveListOfPersons(listOfPersons);

            //THEN
            assertNotNull(savedListOfPersons);
            assertThat(savedListOfPersons).isNotEmpty();
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "WHEN saving the list of persons " +
                "THEN no data is saved in repository and return code is false")
        public void saveListOfPersonsTest_WithException() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.saveAll(listOfPersons)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.saveListOfPersons(listOfPersons));
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
        @DisplayName("GIVEN persons in repository WHEN asking for all persons " +
                "THEN a list of persons is returned")
        public void getAllPersonsTest_WithPersonDataInRepository() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            person.setPersonId(100L);
            listOfPersons.add(person);
            when(personRepositoryMock.findAll()).thenReturn(listOfPersons);

            List<PersonDTO> expectedListOfPersonsDTO = new ArrayList<>();
            PersonDTO personDTO = new PersonDTO();
            personDTO.setPersonId(person.getPersonId());
            personDTO.setFirstName(person.getFirstName());
            personDTO.setLastName(person.getLastName());
            personDTO.setPhone(person.getPhone());
            personDTO.setEmail(person.getEmail());
            personDTO.setAddress(person.getAddress());
            personDTO.setZip(person.getZip());
            personDTO.setCity(person.getCity());
            expectedListOfPersonsDTO.add(personDTO);

            //THEN
            assertEquals(expectedListOfPersonsDTO, personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an exception WHEN asking for all persons " +
                "THEN an empty list is returned")
        public void getAllPersonsTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertThat(personService.getAllPersons()).isEmpty();
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
            assertNull(personService.getAllEmailsByCity(null));
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
            assertNull(personService.getAllEmailsByCity(""));
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
            personInfoDTO.setAge(TestConstants.ADULT_AGE);
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
            medicalRecord.setBirthDate(TestConstants.CHILD_BIRTHDATE);
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
            medicalRecord2.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            hisParent.setMedicalRecord(medicalRecord2);
            listOfPerson.add(hisParent);

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
            assertNull(personService.getPhoneAlertByFireStation(null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFireStation_StationNumber(null);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByStationNumber tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByStationNumber tests")
    class GetFireStationCoverageByStationNumberTest {
        @Test
        @DisplayName("GIVEN citizens covered by the requested fire station found in repository " +
                "WHEN asking for the person information list " +
                "THEN a list of citizens' information covered by the fire station " +
                " and the number of adults and children is returned")
        public void getFireStationCoverageByStationNumberTest_WithInfoInRepository() {
            //GIVEN 3 persons of which 1 child
            List<Person> listOfPersons = new ArrayList<>();

            Person person1 = new Person();
            person1.setFirstName("PST_first_name_1");
            person1.setLastName("PST_last_name_1");
            person1.setAddress("PST_Address_1");
            person1.setPhone("33 1 23 45 67 89");
            MedicalRecord medicalRecord1 = new MedicalRecord();
            medicalRecord1.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            person1.setMedicalRecord(medicalRecord1);
            listOfPersons.add(person1);

            Person person2 = new Person();
            person2.setFirstName("PST_first_name_2");
            person2.setLastName("PST_last_name_2");
            person2.setAddress("PST_Address_2");
            person2.setPhone("33 1 98 76 54 32");
            MedicalRecord medicalRecord2 = new MedicalRecord();
            medicalRecord2.setBirthDate(TestConstants.CHILD_BIRTHDATE);
            person2.setMedicalRecord(medicalRecord2);
            listOfPersons.add(person2);

            Person person3 = new Person();
            person3.setFirstName("PST_first_name_3");
            person3.setLastName("PST_last_name_3");
            person3.setAddress(person2.getAddress());
            person3.setPhone("33 1 99 99 99 99");
            MedicalRecord medicalRecord3 = new MedicalRecord();
            medicalRecord3.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            person3.setMedicalRecord(medicalRecord3);
            listOfPersons.add(person3);

            when(personRepositoryMock.findAllByFireStation_StationNumber(3)).thenReturn(listOfPersons);

            //WHEN
            FireStationCoverageDTO fireStationCoverageDTO = personService.getFireStationCoverageByStationNumber(3);

            //THEN
            assertEquals(3, fireStationCoverageDTO.getPersonCoveredContactsDTOList().size());
            assertEquals(2, fireStationCoverageDTO.getNumberOfAdults());
            assertEquals(1, fireStationCoverageDTO.getNumberOfChildren());
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(3);
        }

        @Test
        @DisplayName("GIVEN no citizens covered by the requested fire station found in repository " +
                "WHEN asking for the person information list " +
                "THEN the returned list is empty")
        public void getFireStationCoverageByStationNumberTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(999)).thenReturn(new ArrayList<>());

            //WHEN
            FireStationCoverageDTO fireStationCoverageDTO = personService.getFireStationCoverageByStationNumber(999);

            //THEN
            assertNull(fireStationCoverageDTO.getPersonCoveredContactsDTOList());
            assertEquals(0, fireStationCoverageDTO.getNumberOfAdults());
            assertEquals(0, fireStationCoverageDTO.getNumberOfChildren());
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(999);
        }

        @Test
        @DisplayName("GIVEN a null fire station number " +
                "WHEN asking for the person information list " +
                "THEN no list is returned")
        public void getFireStationCoverageByStationNumberTest_WithFireStationNumberNull() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(null)).thenReturn(null);

            //THEN
            assertNull(personService.getFireStationCoverageByStationNumber(null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFireStation_StationNumber(null);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addPerson tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addPerson tests")
    class AddPersonTest {
        @Test
        @DisplayName("GIVEN a new person to add " +
                "WHEN saving this new person " +
                "THEN the returned value is the added person")
        public void addPersonTest_WithSuccess() throws Exception {
            //GIVEN
            PersonDTO personDTOToAdd = new PersonDTO();
            personDTOToAdd.setFirstName("PCT_first_name");
            personDTOToAdd.setLastName("PCT_last_name");
            personDTOToAdd.setAddress("PCT_address");
            personDTOToAdd.setEmail("PCT_email@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");

            Person expectedAddedPerson = new Person();
            expectedAddedPerson.setPersonId(100L);
            expectedAddedPerson.setFirstName(personDTOToAdd.getFirstName());
            expectedAddedPerson.setLastName(personDTOToAdd.getLastName());
            expectedAddedPerson.setAddress(personDTOToAdd.getAddress());
            expectedAddedPerson.setEmail(personDTOToAdd.getEmail());
            expectedAddedPerson.setPhone(personDTOToAdd.getPhone());
            expectedAddedPerson.setCity(personDTOToAdd.getCity());
            expectedAddedPerson.setZip(personDTOToAdd.getZip());
            FireStation fireStation = new FireStation();
            fireStation.setFireStationId(100L);
            fireStation.setStationNumber(73);
            fireStation.setAddress(personDTOToAdd.getAddress());
            expectedAddedPerson.setFireStation(fireStation);

            List<Person> listOfPersons = new ArrayList<>();

            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName()))
                    .thenReturn(listOfPersons);
            when(fireStationRepositoryMock
                    .findByAddress(personDTOToAdd.getAddress())).thenReturn(fireStation);
            when(personRepositoryMock.save(any(Person.class))).thenReturn(expectedAddedPerson);

            //WHEN
            PersonDTO addedPersonDTO = personService.addPerson(personDTOToAdd);

            //THEN
            personDTOToAdd.setPersonId(expectedAddedPerson.getPersonId());
            assertEquals(personDTOToAdd, addedPersonDTO);
            assertNotNull(addedPersonDTO.getPersonId());
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(1)).save(any(Person.class));

        }

        @Test
        @DisplayName("GIVEN a person already present in repository " +
                "WHEN saving this new person " +
                "THEN an AlreadyExistsException is thrown")
        public void addPersonTest_WithExistingPersonInRepository() {
            //GIVEN
            PersonDTO personDTOToAdd = new PersonDTO();
            personDTOToAdd.setFirstName("PCT_first_name_Already_Present");
            personDTOToAdd.setLastName("PCT_last_name_Already_Present");
            personDTOToAdd.setAddress("PCT_address");
            personDTOToAdd.setEmail("PCT_email_Already_Present@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");

            List<Person> listOfPersons = new ArrayList<>();
            Person existingPerson = new Person();
            person.setPersonId(1L);
            person.setFirstName(personDTOToAdd.getFirstName());
            person.setLastName(personDTOToAdd.getLastName());
            listOfPersons.add(existingPerson);

            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName()))
                    .thenReturn(listOfPersons);

            //THEN
            assertThrows(AlreadyExistsException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN an empty person " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addPersonTest_WithMissingPersonInformation() {
            //GIVEN
            PersonDTO personDTOToAdd = new PersonDTO();

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null,null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without firstname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addPersonTest_WithoutFirstName() {
            //GIVEN
            PersonDTO personDTOToAdd = new PersonDTO();
            personDTOToAdd.setLastName("PCT_last_name");
            personDTOToAdd.setAddress("PCT_address");
            personDTOToAdd.setEmail("PCT_email@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null,personDTOToAdd.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without lastname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addPersonTest_WithoutLastName() {
            //GIVEN
            PersonDTO personDTOToAdd = new PersonDTO();
            personDTOToAdd.setFirstName("PCT_first_name");
            personDTOToAdd.setAddress("PCT_address");
            personDTOToAdd.setEmail("PCT_email@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(),null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }
    }

}
