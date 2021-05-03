package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.FireDTO;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private FireStationService fireStationService;

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

            //THEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress("FSST_AddressTest");
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

            //THEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress("FSST_AddressTestNotFound");
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
}
