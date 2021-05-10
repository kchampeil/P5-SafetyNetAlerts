package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class MedicalRecordServiceTest {

    @MockBean
    private MedicalRecordRepository medicalRecordRepositoryMock;

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;

    @BeforeAll
    private static void setUp() {

    }

    @BeforeEach
    private void setUpPerTest() {
        medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("MRST_first_name");
        medicalRecord.setLastName("MRST_last_name");

    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfMedicalRecords tests
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN a consistent list of medical records
     * THEN it is saved in DB and return code is true
     *
     * GIVEN an exception when processing
     * THEN no data is saved in DB and return code is false
     * -------------------------------------------------------------------------------------------------------------------- */

    @Nested
    @DisplayName("saveListOfMedicalRecords tests")
    class saveListOfMedicalRecordsTest {
        @Test

        @DisplayName("GIVEN a consistent list of medical records THEN it is saved in DB and return code is true")
        public void saveListOfMedicalRecordsTest_WithConsistentList() {
            //GIVEN
            List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
            listOfMedicalRecords.add(medicalRecord);

            //THEN
            assertTrue(medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords));
            verify(medicalRecordRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }

        @Test
        @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
        public void saveListOfMedicalRecordsTest_WithException() {
            //GIVEN
            List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
            listOfMedicalRecords.add(medicalRecord);
            when(medicalRecordRepositoryMock.saveAll(listOfMedicalRecords)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertFalse(medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords));
            verify(medicalRecordRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllMedicalRecords tests
     * -------------------------------------------------------------------------------------------------------------------- */
    @Nested
    @DisplayName("getAllMedicalRecords tests")
    class GetAllMedicalRecordsTest {
        @Test
        @DisplayName("GIVEN medical records in DB WHEN processing a GET /medicalrecords request THEN a list of medical records is returned")
        public void getAllMedicalRecordsTest_WithMedicalRecordDataInDb() {
            //GIVEN
            List<MedicalRecord> expectedListOfMedicalRecords = new ArrayList<>();
            expectedListOfMedicalRecords.add(medicalRecord);
            when(medicalRecordRepositoryMock.findAll()).thenReturn(expectedListOfMedicalRecords);

            //THEN
            assertEquals(expectedListOfMedicalRecords, medicalRecordService.getAllMedicalRecords());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).findAll();

        }

        @Test
        @DisplayName("GIVEN an exception WHEN processing a GET /medicalrecords request THEN null is returned")
        public void getAllMedicalRecordsTest_WithException() {
            //GIVEN
            when(medicalRecordRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(medicalRecordService.getAllMedicalRecords());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).findAll();

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addMedicalRecord tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addMedicalRecord tests")
    class AddMedicalRecordTest {
        @Test
        @DisplayName("GIVEN a new medical record to add " +
                "WHEN saving this new medical record " +
                "THEN the returned value is the added medical record")
        public void addMedicalRecordTest_WithSuccess() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setFirstName("MRST_first_name");
            medicalRecordDTOToAdd.setLastName("MRST_last_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("MRST_medications_1");
            medications.add("MRST_medications_2");
            medications.add("MRST_medications_3");
            medicalRecordDTOToAdd.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("MRST_allergies_1");
            allergies.add("MRST_allergies_2");
            medicalRecordDTOToAdd.setAllergies(allergies);

            MedicalRecord expectedAddedMedicalRecord = new MedicalRecord();
            expectedAddedMedicalRecord.setMedicalRecordId(100L);
            expectedAddedMedicalRecord.setFirstName(medicalRecordDTOToAdd.getFirstName());
            expectedAddedMedicalRecord.setLastName(medicalRecordDTOToAdd.getLastName());
            expectedAddedMedicalRecord.setBirthDate(medicalRecordDTOToAdd.getBirthDate());
            expectedAddedMedicalRecord.setMedications(medicalRecordDTOToAdd.getMedications());
            expectedAddedMedicalRecord.setAllergies(medicalRecordDTOToAdd.getAllergies());

            Person person = new Person();
            person.setFirstName(medicalRecordDTOToAdd.getFirstName());
            person.setLastName(medicalRecordDTOToAdd.getLastName());
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);

            when(medicalRecordRepositoryMock
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()))
                    .thenReturn(new ArrayList<>());
            when(personRepositoryMock
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()))
                    .thenReturn(listOfPersons);
            when(medicalRecordRepositoryMock.save(any(MedicalRecord.class))).thenReturn(expectedAddedMedicalRecord);

            //WHEN
            MedicalRecordDTO addedMedicalRecordDTO = medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd);

            //THEN
            medicalRecordDTOToAdd.setMedicalRecordId(expectedAddedMedicalRecord.getMedicalRecordId());
            assertEquals(medicalRecordDTOToAdd, addedMedicalRecordDTO);
            assertNotNull(addedMedicalRecordDTO.getMedicalRecordId());
            verify(medicalRecordRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());
            verify(personRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).save(any(MedicalRecord.class));

        }


        @Test
        @DisplayName("GIVEN a medical record already present in repository " +
                "WHEN saving this new medical record " +
                "THEN an AlreadyExistsException is thrown")
        public void addMedicalRecordTest_WithExistingPersonInRepository() {
            //GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setFirstName("MRST_first_name_Already_Present");
            medicalRecordDTOToAdd.setLastName("MRST_last_name_Already_Present");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("MRST_medications_1");
            medications.add("MRST_medications_2");
            medications.add("MRST_medications_3");
            medicalRecordDTOToAdd.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("MRST_allergies_1");
            allergies.add("MRST_allergies_2");
            medicalRecordDTOToAdd.setAllergies(allergies);

            List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
            MedicalRecord existingMedicalRecord = new MedicalRecord();
            existingMedicalRecord.setMedicalRecordId(1L);
            existingMedicalRecord.setFirstName(medicalRecordDTOToAdd.getFirstName());
            existingMedicalRecord.setLastName(medicalRecordDTOToAdd.getLastName());
            listOfMedicalRecords.add(existingMedicalRecord);

            when(medicalRecordRepositoryMock
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()))
                    .thenReturn(listOfMedicalRecords);

            //THEN
            assertThrows(AlreadyExistsException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(1))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any(MedicalRecord.class));
        }

        @Test
        @DisplayName("GIVEN an empty medical record " +
                "WHEN saving this new medical record " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithMissingInformation() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            MedicalRecordDTO medicalRecordDTOTOAdd = new MedicalRecordDTO();

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOTOAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(),anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without firstname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithoutFirstName() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setLastName("MRST_last_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(null, medicalRecordDTOToAdd.getLastName());
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(),anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }

        @Test
        @DisplayName("GIVEN a new person without lastname " +
                "WHEN saving this new person " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithoutLastName() throws AlreadyExistsException, MissingInformationException {
            //GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setFirstName("MRST_first_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(),null);
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(),anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }
    }

}
