package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.testconstants.TestConstants;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @MockBean
    private MedicalRecordRepository medicalRecordRepositoryMock;

    @Autowired
    private IPersonService personService;

    private Person person;
    private List<Person> listOfPersons;

    @BeforeEach
    private void setUpPerTest() {
        listOfPersons = new ArrayList<>();

        person = new Person();
        person.setPersonId(1L);
        person.setFirstName(TestConstants.EXISTING_FIRSTNAME);
        person.setLastName(TestConstants.EXISTING_LASTNAME);
        person.setEmail("PST_email");
        person.setCity("PST_city");
        person.setZip("PST_zip");
        person.setAddress(TestConstants.EXISTING_ADDRESS);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setMedicalRecordId(1L);
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
            listOfPersons.add(person);
            when(personRepositoryMock.findAllByCity(TestConstants.EXISTING_CITY)).thenReturn(listOfPersons);

            //THEN
            assertEquals(1,
                    personService.getAllEmailsByCity(TestConstants.EXISTING_CITY).size());
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity(TestConstants.EXISTING_CITY);
        }

        @Test
        @DisplayName("GIVEN no citizens' emails in repository for the requested city " +
                "WHEN processing a GET /communityEmail request " +
                "THEN an empty list of citizens' emails is returned")
        public void getAllEmailsByCityTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByCity(TestConstants.CITY_NOT_FOUND)).thenReturn(new ArrayList<>());

            //THEN
            assertThat(personService.getAllEmailsByCity(TestConstants.CITY_NOT_FOUND)).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity(TestConstants.CITY_NOT_FOUND);
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
            listOfPersons.add(person);

            List<PersonInfoDTO> expectedListOfPersonInfo = new ArrayList<>();
            PersonInfoDTO personInfoDTO = new PersonInfoDTO();
            personInfoDTO.setLastName(person.getLastName());
            personInfoDTO.setAddress(person.getAddress());
            personInfoDTO.setAge(TestConstants.ADULT_AGE);
            personInfoDTO.setEmail(person.getEmail());
            personInfoDTO.setMedications(person.getMedicalRecord().getMedications());
            personInfoDTO.setAllergies(person.getMedicalRecord().getAllergies());
            expectedListOfPersonInfo.add(personInfoDTO);

            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME))
                    .thenReturn(listOfPersons);

            //THEN
            assertEquals(expectedListOfPersonInfo,
                    personService.getPersonInfoByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME));
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameNotAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
        }

        @Test
        @DisplayName("GIVEN firstname and lastname not found in repository " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is empty")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoDataInRepository() {

            //GIVEN
            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .thenReturn(null);

            //THEN
            assertThat(personService
                    .getPersonInfoByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .isEmpty();
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameNotAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
        }

        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNullRequestParameters() {

            assertNull(personService.getPersonInfoByFirstNameAndLastName(null, null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameNotAndLastName(null, null);

        }

        @Test
        @DisplayName("GIVEN empty firstname and lastname " +
                "WHEN getting person information on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithEmptyRequestParameters() {

            assertNull(personService.getPersonInfoByFirstNameAndLastName("", ""));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName("", "");
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameNotAndLastName("", "");

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
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameNotAndLastName(anyString(), anyString());

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
            Person aChild = new Person();
            aChild.setFirstName(TestConstants.EXISTING_FIRSTNAME);
            aChild.setLastName(TestConstants.EXISTING_LASTNAME);
            aChild.setEmail("PST_email");
            aChild.setAddress(TestConstants.EXISTING_ADDRESS);

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setFirstName(aChild.getFirstName());
            medicalRecord.setLastName(aChild.getLastName());
            medicalRecord.setBirthDate(TestConstants.CHILD_BIRTHDATE);
            aChild.setMedicalRecord(medicalRecord);

            listOfPersons.add(aChild);

            Person hisParent = new Person();
            hisParent.setFirstName(aChild.getFirstName() + "_parent");
            hisParent.setLastName(aChild.getLastName());
            hisParent.setEmail(aChild.getEmail() + "_parent");
            hisParent.setPhone(aChild.getPhone() + "_parent");
            hisParent.setAddress(aChild.getAddress());

            MedicalRecord medicalRecord2 = new MedicalRecord();
            medicalRecord2.setFirstName(hisParent.getFirstName());
            medicalRecord2.setLastName(hisParent.getLastName());
            medicalRecord2.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            hisParent.setMedicalRecord(medicalRecord2);
            listOfPersons.add(hisParent);

            when(personRepositoryMock.findAllByAddress(aChild.getAddress())).thenReturn(listOfPersons);

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
            when(personRepositoryMock.findAllByAddress(TestConstants.ADDRESS_NOT_FOUND)).thenReturn(null);

            //THEN
            assertThat(personService.getChildAlertByAddress(TestConstants.ADDRESS_NOT_FOUND)).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress(TestConstants.ADDRESS_NOT_FOUND);
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
        private PersonDTO personDTOToAdd;

        @BeforeEach
        private void setUpPerTest() {
            personDTOToAdd = new PersonDTO();
            personDTOToAdd.setFirstName(TestConstants.NEW_FIRSTNAME);
            personDTOToAdd.setLastName(TestConstants.NEW_LASTNAME);
            personDTOToAdd.setAddress(TestConstants.EXISTING_ADDRESS);
            personDTOToAdd.setEmail("PCT_email@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");
        }

        @Test
        @DisplayName("GIVEN a new person to add " +
                "WHEN saving this new person " +
                "THEN the returned value is the added person")
        public void addPersonTest_WithSuccess() throws Exception {
            //GIVEN
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
            fireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            fireStation.setAddress(personDTOToAdd.getAddress());
            expectedAddedPerson.setFireStation(fireStation);

            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName()))
                    .thenReturn(listOfPersons);
            when(fireStationRepositoryMock
                    .findByAddress(personDTOToAdd.getAddress())).thenReturn(fireStation);
            when(personRepositoryMock.save(any(Person.class))).thenReturn(expectedAddedPerson);

            //WHEN
            Optional<PersonDTO> addedPersonDTO = personService.addPerson(personDTOToAdd);

            //THEN
            personDTOToAdd.setPersonId(expectedAddedPerson.getPersonId());
            assertEquals(personDTOToAdd, addedPersonDTO.orElse(null));
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
            person.setFirstName(personDTOToAdd.getFirstName());
            person.setLastName(personDTOToAdd.getLastName());
            listOfPersons.add(person);

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

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(new PersonDTO()));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without firstname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addPersonTest_WithoutFirstName() {
            //GIVEN
            personDTOToAdd.setFirstName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(null, personDTOToAdd.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without lastname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addPersonTest_WithoutLastName() {
            //GIVEN
            personDTOToAdd.setLastName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.addPerson(personDTOToAdd));
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToAdd.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updatePerson tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updatePerson tests")
    class UpdatePersonTest {
        private PersonDTO personDTOToUpdate;

        @BeforeEach
        private void setUpPerTest() {
            personDTOToUpdate = new PersonDTO();
            personDTOToUpdate.setFirstName(TestConstants.EXISTING_FIRSTNAME);
            personDTOToUpdate.setLastName(TestConstants.EXISTING_LASTNAME);
            personDTOToUpdate.setAddress(TestConstants.NEW_ADDRESS);
            personDTOToUpdate.setEmail("new_email@safety.com");
            personDTOToUpdate.setPhone("PCT_phone");
            personDTOToUpdate.setCity("PCT_city");
            personDTOToUpdate.setZip("PCT_zip");
        }

        @Test
        @DisplayName("GIVEN a new person to update " +
                "WHEN updating this person " +
                "THEN the returned value is the updated person")
        public void updatePersonTest_WithSuccess() throws Exception {
            //GIVEN
            person.setFirstName(personDTOToUpdate.getFirstName());
            person.setLastName(personDTOToUpdate.getLastName());
            person.setAddress(TestConstants.EXISTING_ADDRESS);
            person.setEmail("old_email@safety.net");
            FireStation fireStation = new FireStation();
            fireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            fireStation.setAddress(personDTOToUpdate.getAddress());
            person.setFireStation(fireStation);

            Person expectedUpdatedPerson = new Person();
            expectedUpdatedPerson.setPersonId(person.getPersonId());
            expectedUpdatedPerson.setFirstName(personDTOToUpdate.getFirstName());
            expectedUpdatedPerson.setLastName(personDTOToUpdate.getLastName());
            expectedUpdatedPerson.setAddress(personDTOToUpdate.getAddress());
            expectedUpdatedPerson.setEmail(personDTOToUpdate.getEmail());
            expectedUpdatedPerson.setPhone(personDTOToUpdate.getPhone());
            expectedUpdatedPerson.setCity(personDTOToUpdate.getCity());
            expectedUpdatedPerson.setZip(personDTOToUpdate.getZip());
            FireStation expectedFireStation = new FireStation();
            expectedFireStation.setFireStationId(TestConstants.NEW_STATION_NUMBER.longValue());
            expectedFireStation.setStationNumber(TestConstants.NEW_STATION_NUMBER);
            expectedFireStation.setAddress(personDTOToUpdate.getAddress());
            expectedUpdatedPerson.setFireStation(expectedFireStation);

            when(personRepositoryMock
                    .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName()))
                    .thenReturn(person);

            when(fireStationRepositoryMock.findByAddress(personDTOToUpdate.getAddress()))
                    .thenReturn(expectedFireStation);

            when(personRepositoryMock.save(any(Person.class))).thenReturn(expectedUpdatedPerson);

            //WHEN
            Optional<PersonDTO> updatedPersonDTO = personService.updatePerson(personDTOToUpdate);

            //THEN
            personDTOToUpdate.setPersonId(expectedUpdatedPerson.getPersonId());
            assertEquals(personDTOToUpdate, updatedPersonDTO.orElse(null));
            verify(personRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress(personDTOToUpdate.getAddress());
            verify(personRepositoryMock, Mockito.times(1)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN a person to update not present in repository " +
                "WHEN updating this new person " +
                "THEN an DoesNotExistException is thrown and no person has been updated")
        public void updatePersonTest_WithNoExistingPersonInRepository() {
            //GIVEN
            when(personRepositoryMock
                    .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName()))
                    .thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> personService.updatePerson(personDTOToUpdate));
            verify(personRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToUpdate.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN an empty person " +
                "WHEN updating this new person " +
                "THEN an MissingInformationException is thrown")
        public void updatePersonTest_WithMissingPersonInformation() {

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.updatePerson(new PersonDTO()));
            verify(personRepositoryMock, Mockito.times(0)).findByFirstNameAndLastName(null, null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a person to update without firstname " +
                "WHEN updating this new person " +
                "THEN an MissingInformationException is thrown")
        public void updatePersonTest_WithoutFirstName() {
            //GIVEN
            personDTOToUpdate.setFirstName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.updatePerson(personDTOToUpdate));
            verify(personRepositoryMock, Mockito.times(0))
                    .findByFirstNameAndLastName(null, personDTOToUpdate.getLastName());
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToUpdate.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }

        @Test
        @DisplayName("GIVEN a person to update without lastname " +
                "WHEN updating this new person " +
                "THEN an MissingInformationException is thrown")
        public void updatePersonTest_WithoutLastName() {
            //GIVEN
            personDTOToUpdate.setLastName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> personService.updatePerson(personDTOToUpdate));
            verify(personRepositoryMock, Mockito.times(0)).findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(personDTOToUpdate.getAddress());
            verify(personRepositoryMock, Mockito.times(0)).save(any((Person.class)));
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deletePersonByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deletePersonByFirstNameAndLastName tests")
    class DeletePersonByFirstNameAndLastNameTest {

        @Test
        @DisplayName("GIVEN an existing person for a given firstname+lastname " +
                "WHEN deleting this person " +
                "THEN the returned value is the deleted person")
        public void deletePersonByFirstNameAndLastNameTest_WithSuccess() throws DoesNotExistException, MissingInformationException {
            //GIVEN
            when(personRepositoryMock
                    .findByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME))
                    .thenReturn(person);

            //WHEN
            Person deletedPerson = personService
                    .deletePersonByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);

            //THEN
            assertEquals(person, deletedPerson);
            verify(personRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
            verify(personRepositoryMock, Mockito.times(1))
                    .save(person);
            verify(medicalRecordRepositoryMock, Mockito.times(1))
                    .deleteById(anyLong());
            verify(personRepositoryMock, Mockito.times(1))
                    .deleteById(person.getPersonId());
        }


        @Test
        @DisplayName("GIVEN a person not existing in repository " +
                "WHEN deleting this person record " +
                "THEN a DoesNotExistException is thrown and no person has been deleted")
        public void deletePersonByFirstNameAndLastNameTest_WithNoExistingPersonInRepository() {
            //GIVEN
            when(personRepositoryMock
                    .findByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> personService
                    .deletePersonByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND));
            verify(personRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
            verify(personRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }


        @Test
        @DisplayName("GIVEN an empty firstname+lastname " +
                "WHEN deleting this person " +
                "THEN a MissingInformationException is thrown and no person has been deleted)")
        public void deletePersonByFirstNameAndLastNameTest_WithMissingInformation() {
            //GIVEN

            //THEN
            assertThrows(MissingInformationException.class,
                    () -> personService.deletePersonByFirstNameAndLastName(null, null));
            verify(personRepositoryMock, Mockito.times(0))
                    .findByFirstNameAndLastName(null, null);
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
            verify(personRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }
    }

}
