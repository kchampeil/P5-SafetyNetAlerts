package com.safetynet.alerts.service;

import com.safetynet.alerts.testconstants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private static Person adult;
    private static Person child;

    @BeforeAll
    private static void setUp() {
        adult = new Person();
        adult.setFirstName("FSST_first_name_1");
        adult.setLastName("FSST_last_name_1");
        adult.setPhone("33 1 23 45 67 89");
        adult.setAddress(TestConstants.EXISTING_ADDRESS);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(adult.getFirstName());
        medicalRecord.setLastName(adult.getLastName());
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
        adult.setMedicalRecord(medicalRecord);

        child = new Person();
        child.setFirstName("FSST_first_name_2");
        child.setLastName("FSST_last_name_2");
        child.setPhone("33 1 98 76 54 32");
        child.setAddress(adult.getAddress());

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setFirstName(child.getFirstName());
        medicalRecord2.setLastName(child.getLastName());
        medicalRecord2.setBirthDate(TestConstants.CHILD_BIRTHDATE);
        child.setMedicalRecord(medicalRecord2);
    }

    @BeforeEach
    private void setUpPerTest() {
        fireStation = new FireStation();
        fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        fireStation.setAddress(adult.getAddress());
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------*/

    @Nested
    @DisplayName("saveListOfFireStations tests")
    class saveListOfFireStationsTest {

        private List<FireStation> listOfFireStations;

        @BeforeEach
        private void setUpPerTest() {
            listOfFireStations = new ArrayList<>();
            listOfFireStations.add(fireStation);
        }

        @Test
        @DisplayName("GIVEN a consistent list of fire stations " +
                "WHEN saving the list of fire stations " +
                "THEN it is saved in DB and the saved list of fire stations is returned")
        public void saveListOfFireStationsTest_WithConsistentList() {
            //GIVEN
            when(fireStationRepositoryMock.saveAll(listOfFireStations)).thenReturn(listOfFireStations);

            //WHEN
            Iterable<FireStation> savedListOfFireStations = fireStationService.saveListOfFireStations(listOfFireStations);

            //THEN
            assertNotNull(savedListOfFireStations);
            assertThat(savedListOfFireStations).isNotEmpty();
            verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("GIVEN an exception when processing WHEN saving the list of fire stations " +
                "THEN no data is saved in DB and null is returned")
        public void saveListOfFireStationsTest_WithException() {
            //GIVEN
            when(fireStationRepositoryMock.saveAll(listOfFireStations)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(fireStationService.saveListOfFireStations(listOfFireStations));
            verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------*/

    @Nested
    @DisplayName("getAllFireStations tests")
    class GetAllFireStationsTest {
        @Test
        @DisplayName("GIVEN fire stations in DB WHEN asking for all fire stations " +
                "THEN a list of fire stations DTO is returned")
        public void getAllFireStationsTest_WithFireStationDataInDb() {
            //GIVEN
            List<FireStation> listOfFireStations = new ArrayList<>();
            fireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            listOfFireStations.add(fireStation);
            when(fireStationRepositoryMock.findAll()).thenReturn(listOfFireStations);

            List<FireStationDTO> expectedListOfFireStationsDTO = new ArrayList<>();
            FireStationDTO fireStationDTO = new FireStationDTO();
            fireStationDTO.setFireStationId(fireStation.getFireStationId());
            fireStationDTO.setStationNumber(fireStation.getStationNumber());
            fireStationDTO.setAddress(fireStation.getAddress());
            expectedListOfFireStationsDTO.add(fireStationDTO);

            //THEN
            assertEquals(expectedListOfFireStationsDTO, fireStationService.getAllFireStations());
            verify(fireStationRepositoryMock, Mockito.times(1)).findAll();
        }


        @Test
        @DisplayName("GIVEN an exception WHEN asking for all fire stations THEN an empty list is returned")
        public void getAllFireStationsTest_WithException() {
            //GIVEN
            when(fireStationRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertThat(fireStationService.getAllFireStations()).isEmpty();
            verify(fireStationRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetFireStationCoverageByAddressTest {

        private FireStation fireStation;

        @BeforeEach
        private void setUpPerTest() {
            fireStation = new FireStation();
            fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        }

        @Test
        @DisplayName("GIVEN citizens living at the requested address + fire station covering the requested address found in repository " +
                "WHEN asking for fire station coverage information " +
                "THEN a list of citizens living at the address and the fire station number covering this address are returned")
        public void getFireStationCoverageByAddressTest_WithInfoInRepository() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(adult);
            listOfPersons.add(child);

            fireStation.setAddress(adult.getAddress());

            when(personRepositoryMock.findAllByAddress(adult.getAddress())).thenReturn(listOfPersons);
            when(fireStationRepositoryMock.findByAddress(adult.getAddress())).thenReturn(fireStation);

            //WHEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress(adult.getAddress());

            //THEN
            assertEquals(2, fireDTO.getPersonCoveredDTOList().size());
            assertEquals(fireStation.getStationNumber(), fireDTO.getStationNumber());
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByAddress(adult.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(adult.getAddress());
        }

        @Test
        @DisplayName("GIVEN no citizens living at the requested address found in repository " +
                "but fire station covering the requested address found in repository " +
                "WHEN asking for fire station coverage information " +
                "THEN in the returned information the list of persons covered is empty and the station number is populated")
        public void getFireStationCoverageByAddressTest_WithNoPersonInRepository() {
            //GIVEN
            fireStation.setAddress(TestConstants.ADDRESS_NOT_FOUND);

            when(personRepositoryMock.findAllByAddress(TestConstants.ADDRESS_NOT_FOUND))
                    .thenReturn(new ArrayList<>());
            when(fireStationRepositoryMock.findByAddress(TestConstants.ADDRESS_NOT_FOUND))
                    .thenReturn(fireStation);

            //WHEN
            FireDTO fireDTO = fireStationService.getFireStationCoverageByAddress(TestConstants.ADDRESS_NOT_FOUND);

            //THEN
            assertThat(fireDTO.getPersonCoveredDTOList()).isEmpty();
            assertEquals(fireStation.getStationNumber(), fireDTO.getStationNumber());
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByAddress(TestConstants.ADDRESS_NOT_FOUND);
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(TestConstants.ADDRESS_NOT_FOUND);
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
            listOfPersonsForStation3.add(adult);
            listOfPersonsForStation3.add(child);

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
            assertTrue(listOfFloodDTO.get(0).getPersonsCoveredByAddress().containsKey(adult.getAddress()));
            assertTrue(listOfFloodDTO.get(1).getPersonsCoveredByAddress().containsKey(person3.getAddress()));
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(3);
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(4);
        }

        @Test
        @DisplayName("GIVEN no citizens living at the area of the requested fire stations found in repository " +
                "WHEN asking for flood information " +
                "THEN the returned list of FloodDTO is empty")
        public void getFloodByStationNumbersTest_WithNoPersonInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(TestConstants.NEW_STATION_NUMBER))
                    .thenReturn(new ArrayList<>());

            //WHEN
            List<FloodDTO> listOfFloodDTO
                    = fireStationService.getFloodByStationNumbers(Collections.singletonList(TestConstants.NEW_STATION_NUMBER));

            //THEN
            assertThat(listOfFloodDTO).isEmpty();
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFireStation_StationNumber(TestConstants.NEW_STATION_NUMBER);
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
    class AddFireStationTest {

        private FireStationDTO fireStationDTOToAdd;

        @BeforeEach
        private void setUpPerTest() {
            fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(TestConstants.NEW_STATION_NUMBER);
            fireStationDTOToAdd.setAddress(TestConstants.NEW_ADDRESS);
        }

        @Test
        @DisplayName("GIVEN a new mapping address/fire station " +
                "WHEN saving this new relationship " +
                "THEN the returned value is the added fire station")
        public void addFireStationTest_WithSuccess() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            FireStation expectedFireStation = new FireStation();
            expectedFireStation.setFireStationId(fireStationDTOToAdd.getStationNumber().longValue());
            expectedFireStation.setStationNumber(fireStationDTOToAdd.getStationNumber());
            expectedFireStation.setAddress(fireStationDTOToAdd.getAddress());

            when(fireStationRepositoryMock.findByAddress(fireStationDTOToAdd.getAddress())).thenReturn(null);
            when(fireStationRepositoryMock.save(any(FireStation.class))).thenReturn(expectedFireStation);

            List<Person> listOfPersons = new ArrayList<>();
            adult.setAddress(expectedFireStation.getAddress());
            adult.setFireStation(null);
            listOfPersons.add(adult);
            when(personRepositoryMock.findAllByAddress(expectedFireStation.getAddress())).thenReturn(listOfPersons);
            when(personRepositoryMock.saveAll(listOfPersons)).thenReturn(listOfPersons);

            //WHEN
            Optional<FireStationDTO> addedFireStationDTO = fireStationService.addFireStation(fireStationDTOToAdd);

            //THEN
            fireStationDTOToAdd.setFireStationId(expectedFireStation.getFireStationId());
            assertEquals(fireStationDTOToAdd, addedFireStationDTO.orElse(null));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(fireStationDTOToAdd.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .save(any(FireStation.class));
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByAddress(expectedFireStation.getAddress());
            verify(personRepositoryMock, Mockito.times(1))
                    .saveAll(listOfPersons);
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station for an existing address in repository " +
                "WHEN saving this new relationship " +
                "THEN an AlreadyExistsException is thrown and no fire station has been added)")
        public void addFireStationTest_WithExistingAddressInRepository() {
            //GIVEN
            FireStation existingFireStation = new FireStation();
            existingFireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            existingFireStation.setAddress(fireStationDTOToAdd.getAddress());

            when(fireStationRepositoryMock.findByAddress(fireStationDTOToAdd.getAddress())).thenReturn(existingFireStation);

            //THEN
            assertThrows(AlreadyExistsException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(fireStationDTOToAdd.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(anyString());
            verify(personRepositoryMock, Mockito.times(0)).saveAll(anyList());
        }


        @Test
        @DisplayName("GIVEN an empty fire station information " +
                "WHEN saving this new relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been added)")
        public void addFireStationTest_WithMissingInformation() {
            //GIVEN
            fireStationDTOToAdd.setStationNumber(null);
            fireStationDTOToAdd.setAddress(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any((FireStation.class)));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(anyString());
            verify(personRepositoryMock, Mockito.times(0)).saveAll(anyList());
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station without any address " +
                "WHEN saving this new relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been added)")
        public void addFireStationTest_WithNoAddressForStation() {
            //GIVEN
            fireStationDTOToAdd.setAddress(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station without any station number " +
                "WHEN saving this new relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been added)")
        public void addFireStationTest_WithNoStationNumber() {
            //GIVEN
            fireStationDTOToAdd.setStationNumber(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.addFireStation(fireStationDTOToAdd));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updateFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updateFireStation tests")
    class UpdateFireStationTest {

        private FireStationDTO fireStationDTOToUpdate;

        @BeforeEach
        private void setUpPerTest() {
            fireStationDTOToUpdate = new FireStationDTO();
            fireStationDTOToUpdate.setStationNumber(TestConstants.NEW_STATION_NUMBER);
            fireStationDTOToUpdate.setAddress(TestConstants.EXISTING_ADDRESS);
        }

        @Test
        @DisplayName("GIVEN a new station number for a given address " +
                "WHEN updating this relationship " +
                "THEN the returned value is the updated fire station")
        public void updateFireStationTest_WithSuccess() throws DoesNotExistException, MissingInformationException {
            //GIVEN
            FireStation expectedFireStation = new FireStation();
            expectedFireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            expectedFireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            expectedFireStation.setAddress(fireStationDTOToUpdate.getAddress());
            when(fireStationRepositoryMock.findByAddress(fireStationDTOToUpdate.getAddress()))
                    .thenReturn(expectedFireStation);

            expectedFireStation.setStationNumber(fireStationDTOToUpdate.getStationNumber());
            when(fireStationRepositoryMock.save(any(FireStation.class))).thenReturn(expectedFireStation);

            //WHEN
            Optional<FireStationDTO> updatedFireStationDTO = fireStationService.updateFireStation(fireStationDTOToUpdate);

            //THEN
            fireStationDTOToUpdate.setFireStationId(expectedFireStation.getFireStationId());
            assertEquals(fireStationDTOToUpdate, updatedFireStationDTO.orElse(null));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(fireStationDTOToUpdate.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(1)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN a new station number for a given address not existing in repository " +
                "WHEN updating this relationship " +
                "THEN an DoesNotExistException is thrown and no fire station has been updated")
        public void updateFireStationTest_WithNoExistingAddressInRepository() {
            //GIVEN
            when(fireStationRepositoryMock.findByAddress(fireStationDTOToUpdate.getAddress())).thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> fireStationService.updateFireStation(fireStationDTOToUpdate));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(fireStationDTOToUpdate.getAddress());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN an empty fire station information " +
                "WHEN updating this relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been updated)")
        public void updateFireStationTest_WithMissingInformation() {
            //GIVEN
            fireStationDTOToUpdate.setStationNumber(null);
            fireStationDTOToUpdate.setAddress(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.updateFireStation(fireStationDTOToUpdate));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any((FireStation.class)));
        }


        @Test
        @DisplayName("GIVEN a fire station to update  without any address " +
                "WHEN updating this new relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been updated)")
        public void updateFireStationTest_WithNoAddressForStation() {
            //GIVEN
            fireStationDTOToUpdate.setAddress(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.updateFireStation(fireStationDTOToUpdate));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }


        @Test
        @DisplayName("GIVEN a fire station to update  without any station number " +
                "WHEN updating this new relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been updated)")
        public void updateFireStationTest_WithNoStationNumber() {
            //GIVEN
            fireStationDTOToUpdate.setStationNumber(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.updateFireStation(fireStationDTOToUpdate));
            verify(fireStationRepositoryMock, Mockito.times(0)).findByAddress(anyString());
            verify(fireStationRepositoryMock, Mockito.times(0)).save(any(FireStation.class));
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deleteFireStationByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deleteFireStationByAddress tests")
    class DeleteFireStationByAddressTest {

        @Test
        @DisplayName("GIVEN an existing fire station for a given address " +
                "WHEN deleting this relationship " +
                "THEN the returned value is the deleted fire station")
        public void deleteFireStationByAddressTest_WithSuccess() throws DoesNotExistException, MissingInformationException {
            //GIVEN
            FireStation existingFireStation = new FireStation();
            existingFireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            existingFireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            existingFireStation.setAddress(TestConstants.EXISTING_ADDRESS);
            when(fireStationRepositoryMock.findByAddress(TestConstants.EXISTING_ADDRESS)).thenReturn(existingFireStation);

            List<Person> listOfPersons = new ArrayList<>();
            adult.setAddress(TestConstants.EXISTING_ADDRESS);
            adult.setFireStation(existingFireStation);
            listOfPersons.add(adult);
            when(personRepositoryMock.findAllByAddress(TestConstants.EXISTING_ADDRESS)).thenReturn(listOfPersons);
            when(personRepositoryMock.saveAll(listOfPersons)).thenReturn(listOfPersons);

            //WHEN
            FireStation deletedFireStation = fireStationService.deleteFireStationByAddress(TestConstants.EXISTING_ADDRESS);

            //THEN
            assertEquals(existingFireStation, deletedFireStation);
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(TestConstants.EXISTING_ADDRESS);
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByAddress(TestConstants.EXISTING_ADDRESS);
            verify(personRepositoryMock, Mockito.times(1))
                    .saveAll(listOfPersons);
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .deleteById(existingFireStation.getFireStationId());

        }


        @Test
        @DisplayName("GIVEN an address not existing in repository " +
                "WHEN deleting this relationship " +
                "THEN an DoesNotExistException is thrown and no fire station has been deleted")
        public void deleteFireStationByAddressTest_WithNoExistingAddressInRepository() {
            //GIVEN
            when(fireStationRepositoryMock.findByAddress(TestConstants.ADDRESS_NOT_FOUND)).thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> fireStationService.deleteFireStationByAddress(TestConstants.ADDRESS_NOT_FOUND));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findByAddress(TestConstants.ADDRESS_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByAddress(TestConstants.ADDRESS_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(0))
                    .saveAll(anyList());
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }


        @Test
        @DisplayName("GIVEN an empty address " +
                "WHEN deleting this relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been deleted)")
        public void deleteFireStationByAddressTest_WithMissingInformation() {
            //GIVEN

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.deleteFireStationByAddress(null));
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .findByAddress(null);
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByAddress(null);
            verify(personRepositoryMock, Mockito.times(0))
                    .saveAll(anyList());
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deleteFireStationByStationNumber tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deleteFireStationByStationNumber tests")
    class DeleteFireStationByStationNumberTest {

        @Test
        @DisplayName("GIVEN an existing fire station for a given station number " +
                "WHEN deleting this relationship " +
                "THEN the returned value is the deleted fire station")
        public void deleteFireStationByStationNumberTest_WithSuccess() throws DoesNotExistException, MissingInformationException {
            //GIVEN
            FireStation existingFireStation = new FireStation();
            existingFireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            existingFireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            existingFireStation.setAddress(adult.getAddress());
            List<FireStation> listOfExistingFireStations = new ArrayList<>();
            listOfExistingFireStations.add(existingFireStation);
            when(fireStationRepositoryMock.findAllByStationNumber(TestConstants.EXISTING_STATION_NUMBER))
                    .thenReturn(listOfExistingFireStations);

            List<Person> listOfPersons = new ArrayList<>();
            adult.setFireStation(existingFireStation);
            listOfPersons.add(adult);
            when(personRepositoryMock.findAllByFireStation_StationNumber(TestConstants.EXISTING_STATION_NUMBER))
                    .thenReturn(listOfPersons);
            when(personRepositoryMock.saveAll(listOfPersons)).thenReturn(listOfPersons);

            List<FireStation> expectedDeletedFireStations = new ArrayList<>();
            expectedDeletedFireStations.add(existingFireStation);

            //WHEN
            List<FireStation> deletedFireStations =
                    fireStationService.deleteFireStationByStationNumber(TestConstants.EXISTING_STATION_NUMBER);

            //THEN
            assertEquals(expectedDeletedFireStations,deletedFireStations);
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findAllByStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFireStation_StationNumber(TestConstants.EXISTING_STATION_NUMBER);
            verify(personRepositoryMock, Mockito.times(1))
                    .saveAll(listOfPersons);
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .deleteById(existingFireStation.getFireStationId());

        }


        @Test
        @DisplayName("GIVEN a station number with no existing fire station in repository " +
                "WHEN deleting this relationship " +
                "THEN an DoesNotExistException is thrown and no fire station has been deleted")
        public void deleteFireStationByStationNumberTest_WithNoExistingFireStationInRepository() {
            //GIVEN
            when(fireStationRepositoryMock.findAllByStationNumber(TestConstants.STATION_NUMBER_NOT_FOUND)).thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> fireStationService.deleteFireStationByStationNumber(TestConstants.STATION_NUMBER_NOT_FOUND));
            verify(fireStationRepositoryMock, Mockito.times(1))
                    .findAllByStationNumber(TestConstants.STATION_NUMBER_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFireStation_StationNumber(TestConstants.STATION_NUMBER_NOT_FOUND);
            verify(personRepositoryMock, Mockito.times(0))
                    .saveAll(anyList());
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }


        @Test
        @DisplayName("GIVEN an missing station number " +
                "WHEN deleting this relationship " +
                "THEN a MissingInformationException is thrown and no fire station has been deleted)")
        public void deleteFireStationByStationNumberTest_WithMissingInformation() {
            //GIVEN

            //THEN
            assertThrows(MissingInformationException.class, () -> fireStationService.deleteFireStationByStationNumber(null));
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .findAllByStationNumber(null);
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFireStation_StationNumber(null);
            verify(personRepositoryMock, Mockito.times(0))
                    .saveAll(anyList());
            verify(fireStationRepositoryMock, Mockito.times(0))
                    .deleteById(anyLong());
        }
    }

}
