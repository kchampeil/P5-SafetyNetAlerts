package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class FireStationServiceTest {

    @MockBean
    private FireStationRepository fireStationRepositoryMock;

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private IFireStationService fireStationService;

    private FireStation fireStation;

    @BeforeAll
    private static void setUp() {

    }

    @BeforeEach
    private void setUpPerTest() {
        fireStation = new FireStation();
        fireStation.setStationNumber(2021);
        fireStation.setAddress("FSST_address");
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN a consistent list of fire stations
     * THEN it is saved in DB and return code is true
     *
     * GIVEN an exception when processing
     * THEN no data is saved in DB and return code is false
     * -------------------------------------------------------------------------------------------------------------------- */

    @Nested
    @DisplayName("saveListOfFireStations tests")
    class saveListOfFireStationsTest {
        @Test
        @DisplayName("GIVEN a consistent list of fire stations THEN it is saved in DB and return code is true")
        public void saveListOfFireStationsTest_WithConsistentList() {
            //GIVEN
            List<FireStation> listOfFireStations = new ArrayList<>();
            listOfFireStations.add(fireStation);

            //THEN
            assertTrue(fireStationService.saveListOfFireStations(listOfFireStations));
            verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }

        @Test
        @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
        public void saveListOfFireStationsTest_WithException() {
            //GIVEN
            List<FireStation> listOfFireStations = new ArrayList<>();
            listOfFireStations.add(fireStation);
            when(fireStationRepositoryMock.saveAll(listOfFireStations)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertFalse(fireStationService.saveListOfFireStations(listOfFireStations));
            verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN fire stations in DB
     * WHEN processing a GET /firestations request
     * THEN a list of fire stations is returned
     *
     * GIVEN an exception
     * WHEN processing a GET /firestations request
     * THEN null is returned
     * -------------------------------------------------------------------------------------------------------------------- */

    @Nested
    @DisplayName("getAllFireStations tests")
    class GetAllFireStationsTest {
        @Test
        @DisplayName("GIVEN fire stations in DB WHEN processing a GET /firestations request THEN a list of fire stations is returned")
        public void getAllFireStationsTest_WithFireStationDataInDb() {
            //GIVEN
            List<FireStation> expectedListOfFireStations = new ArrayList<>();
            expectedListOfFireStations.add(fireStation);
            when(fireStationRepositoryMock.findAll()).thenReturn(expectedListOfFireStations);

            //THEN
            assertEquals(expectedListOfFireStations, fireStationService.getAllFireStations());
            verify(fireStationRepositoryMock, Mockito.times(1)).findAll();

        }

        @Test
        @DisplayName("GIVEN an exception WHEN processing a GET /firestations request THEN null is returned")
        public void getAllFireStationsTest_WithException() {
            //GIVEN
            when(fireStationRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(fireStationService.getAllFireStations());
            verify(fireStationRepositoryMock, Mockito.times(1)).findAll();

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetFireStationCoverageByAddressTest {
        @Test
        @DisplayName("GIVEN citizens living at the requested address + fire station covering the requested address found in repository " +
                "WHEN asking for fire station coverage information " +
                "THEN a list of citizens living at the address and the fire station number covering this address are returned")
        public void getFireStationCoverageByAddressTest_WithInfoInRepository() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();

            Person person1 = new Person();
            person1.setFirstName("FSST_first_name_1");
            person1.setLastName("FSST_last_name_1");
            person1.setPhone("33 1 23 45 67 89");
            person1.setAddress("FSST_AddressTest");

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setFirstName(person1.getFirstName());
            medicalRecord.setLastName(person1.getLastName());
            medicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("FSST_medications_1");
            medications.add("FSST_medications_2");
            medications.add("FSST_medications_3");
            medicalRecord.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("FSST_allergies_1");
            allergies.add("FSST_allergies_2");
            medicalRecord.setAllergies(allergies);
            person1.setMedicalRecord(medicalRecord);
            listOfPersons.add(person1);

            Person person2 = new Person();
            person2.setFirstName("FSST_first_name_2");
            person2.setLastName("FSST_last_name_2");
            person2.setPhone("33 1 98 76 54 32");
            person2.setAddress(person1.getAddress());

            MedicalRecord medicalRecord2 = new MedicalRecord();
            medicalRecord2.setFirstName(person2.getFirstName());
            medicalRecord2.setLastName(person2.getLastName());
            medicalRecord2.setBirthDate(TestConstants.CHILD_BIRTHDATE);
            person2.setMedicalRecord(medicalRecord2);
            listOfPersons.add(person2);

            FireStation fireStation = new FireStation();
            fireStation.setStationNumber(73);
            fireStation.setAddress(person1.getAddress());

            when(personRepositoryMock.findAllByAddress("FSST_AddressTest")).thenReturn(listOfPersons);
            when(fireStationRepositoryMock.findByAddress("FSST_AddressTest")).thenReturn(fireStation);

            //WHEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress("FSST_AddressTest");

            //THEN
            assertEquals(2, fireDTO.getPersonCoveredDTOList().size());
            assertEquals(fireStation.getStationNumber(), fireDTO.getStationNumber());
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress("FSST_AddressTest");
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress("FSST_AddressTest");
        }

        @Test
        @DisplayName("GIVEN no citizens living at the requested address found in repository " +
                "but fire station covering the requested address found in repository " +
                "WHEN asking for fire station coverage information " +
                "THEN in the returned information the list of persons covered is null and the station number is populated")
        public void getFireStationCoverageByAddressTest_WithNoPersonInRepository() {
            //GIVEN
            FireStation fireStation = new FireStation();
            fireStation.setStationNumber(73);
            fireStation.setAddress("FSST_AddressTestNotFound");

            when(personRepositoryMock.findAllByAddress("FSST_AddressTestNotFound")).thenReturn(new ArrayList<>());
            when(fireStationRepositoryMock.findByAddress("FSST_AddressTestNotFound")).thenReturn(fireStation);

            //WHEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress("FSST_AddressTestNotFound");

            //THEN
            assertThat(fireDTO.getPersonCoveredDTOList()).isNull();
            assertEquals(fireStation.getStationNumber(), fireDTO.getStationNumber());
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress("FSST_AddressTestNotFound");
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress("FSST_AddressTestNotFound");
        }

        @Test
        @DisplayName("GIVEN a null address " +
                "WHEN asking for fire station coverage information " +
                "THEN no information is returned")
        public void getFireStationCoverageByAddressTest_WithFireStationNumberNull() {
            //GIVEN
            when(personRepositoryMock.findAllByAddress(anyString())).thenReturn(null);
            when(fireStationRepositoryMock.findByAddress(anyString())).thenReturn(null);

            //THEN
            assertThat(fireStationService.getFireStationCoverageByAddress(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(null);
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(null);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFloodByStationNumbers tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFloodByStationNumbers tests")
    class GetFloodByStationNumbersTest {
        @Test
        @DisplayName("GIVEN citizens living at the addresses covered by one of the fire stations found in repository " +
                "WHEN asking for flood information " +
                "THEN a list of person information grouped fire station number and by address are returned")
        public void getFloodByStationNumbersTest_WithInfoInRepository() {
            //GIVEN
            List<Person> listOfPersonsForStation3 = new ArrayList<>();

            Person person1 = new Person();
            person1.setFirstName("FSST_first_name_1");
            person1.setLastName("FSST_last_name_1");
            person1.setPhone("33 1 23 45 67 89");
            person1.setAddress("FSST_AddressTest1");

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setFirstName(person1.getFirstName());
            medicalRecord.setLastName(person1.getLastName());
            medicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("FSST_medications_1");
            medications.add("FSST_medications_2");
            medications.add("FSST_medications_3");
            medicalRecord.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("FSST_allergies_1");
            allergies.add("FSST_allergies_2");
            medicalRecord.setAllergies(allergies);
            person1.setMedicalRecord(medicalRecord);
            listOfPersonsForStation3.add(person1);

            Person person2 = new Person();
            person2.setFirstName("FSST_first_name_2");
            person2.setLastName("FSST_last_name_2");
            person2.setPhone("33 1 98 76 54 32");
            person2.setAddress(person1.getAddress());

            MedicalRecord medicalRecord2 = new MedicalRecord();
            medicalRecord2.setFirstName(person2.getFirstName());
            medicalRecord2.setLastName(person2.getLastName());
            medicalRecord2.setBirthDate(TestConstants.CHILD_BIRTHDATE);
            person2.setMedicalRecord(medicalRecord2);
            listOfPersonsForStation3.add(person2);

            List<Person> listOfPersonsForStation4 = new ArrayList<>();
            Person person3 = new Person();
            person3.setFirstName("FSST_first_name_3");
            person3.setLastName("FSST_last_name_3");
            person3.setPhone("33 1 98 76 99 99");
            person3.setAddress("FSST_AddressTest3");

            MedicalRecord medicalRecord3 = new MedicalRecord();
            medicalRecord3.setFirstName(person3.getFirstName());
            medicalRecord3.setLastName(person3.getLastName());
            medicalRecord3.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            person3.setMedicalRecord(medicalRecord3);
            listOfPersonsForStation4.add(person3);

            List<Integer> stationNumbers = new ArrayList<>();
            stationNumbers.add(3);
            stationNumbers.add(4);

            when(personRepositoryMock.findAllByFireStation_StationNumber(3)).thenReturn(listOfPersonsForStation3);
            when(personRepositoryMock.findAllByFireStation_StationNumber(4)).thenReturn(listOfPersonsForStation4);

            //WHEN
            List<FloodDTO> listOfFloodDTO = fireStationService.getFloodByStationNumbers(stationNumbers);

            //THEN
            assertEquals(2, listOfFloodDTO.size());
            assertTrue(listOfFloodDTO.get(0).getPersonsCoveredByAddress().containsKey("FSST_AddressTest1"));
            assertTrue(listOfFloodDTO.get(1).getPersonsCoveredByAddress().containsKey("FSST_AddressTest3"));
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(3);
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(4);
        }

        @Test
        @DisplayName("GIVEN no citizens living at the area of the requested fire stations found in repository " +
                "WHEN asking for flood information " +
                "THEN the returned list of FloodDTO is empty")
        public void getFloodByStationNumbersTest_WithNoPersonInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByAddress("FSST_AddressTestNotFound")).thenReturn(new ArrayList<>());

            //WHEN
            List<FloodDTO> listOfFloodDTO = fireStationService.getFloodByStationNumbers(Collections.singletonList(999));

            //THEN
            assertThat(listOfFloodDTO).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(999);
        }

        @Test
        @DisplayName("GIVEN an empty list of station numbers " +
                "WHEN asking for fire station coverage information " +
                "THEN no information is returned")
        public void getFloodByStationNumbersTest_WithListOfStationNumbersNull() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(anyInt())).thenReturn(null);

            //THEN
            assertThat(fireStationService.getFloodByStationNumbers(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(null);
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addFireStation tests")
    class addFireStationTest {
        @Test
        @DisplayName("GIVEN a new mapping address/fire station " +
                "WHEN saving this new relationship " +
                "THEN the returned value is the added fire station")
        public void addFireStationTest_WithSuccess() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);
            fireStationDTOToAdd.setAddress("FSST_New_Address");

            FireStation expectedFireStation = new FireStation();
            expectedFireStation.setFireStationId(100L);
            expectedFireStation.setStationNumber(fireStationDTOToAdd.getStationNumber());
            expectedFireStation.setAddress(fireStationDTOToAdd.getAddress());

            when(fireStationRepositoryMock.findByAddress("FSST_New_Address")).thenReturn(null);
            when(fireStationRepositoryMock.save(any(FireStation.class))).thenReturn(expectedFireStation);

            //WHEN
            FireStationDTO addedFireStationDTO = fireStationService.addFireStation(fireStationDTOToAdd);

            //THEN
            fireStationDTOToAdd.setFireStationId(expectedFireStation.getFireStationId());
            assertEquals(fireStationDTOToAdd, addedFireStationDTO);
            assertNotNull(addedFireStationDTO.getFireStationId());
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress(fireStationDTOToAdd.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(1)).save(any(FireStation.class));

        }


        @Test
        @DisplayName("GIVEN a new fire station without address " +
                "WHEN saving this new fire station " +
                "THEN an MissingInformationException is thrown")
        public void addFireStationTest_WithoutAddress() {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station for an existing address in repository " +
                "WHEN saving this new relationship " +
                "THEN the returned value is null (ie no fire station has been added)")
        public void addFireStationTest_WithExistingAddressInRepository() {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);
            fireStationDTOToAdd.setAddress("FSST_Address_Already_Present");

            FireStation existingFireStation = new FireStation();
            existingFireStation.setStationNumber(4);
            existingFireStation.setAddress(existingFireStation.getAddress());

            when(fireStationRepositoryMock.findByAddress(fireStationDTOToAdd.getAddress())).thenReturn(existingFireStation);

            //THEN
            assertThrows(AlreadyExistsException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(1)).findByAddress(fireStationDTOToAdd.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN an empty fire station information " +
                "WHEN saving this new relationship " +
                "THEN the returned value is null (ie no fire station has been added)")
        public void addFireStationTest_WithMissingFireStationInformation() {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any((FireStation.class)));
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station without any address " +
                "WHEN saving this new relationship " +
                "THEN the returned value is null (ie no fire station has been added)")
        public void addFireStationTest_WithNoAddressForStation() {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station without any station number " +
                "WHEN saving this new relationship " +
                "THEN the returned value is null (ie no fire station has been added)")
        public void addFireStationTest_WithNoStationNumber() {
            //GIVEN
            FireStationDTO fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setAddress("FSST_New_Address");

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }
    }

}
