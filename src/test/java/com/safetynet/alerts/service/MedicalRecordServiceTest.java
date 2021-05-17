package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
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
class MedicalRecordServiceTest {

    @MockBean
    private MedicalRecordRepository medicalRecordRepositoryMock;

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private IMedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;

    @BeforeEach
    private void setUpPerTest() {
        medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("MRST_first_name");
        medicalRecord.setLastName("MRST_last_name");

    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfMedicalRecords tests
     * ---------------------------------------------------------------------------------------------------------------------- */

    @Nested
    @DisplayName("saveListOfMedicalRecords tests")
    class saveListOfMedicalRecordsTest {

        private List<MedicalRecord> listOfMedicalRecords;

        @BeforeEach
        private void setUpPerTest() {
            listOfMedicalRecords = new ArrayList<>();
            listOfMedicalRecords.add(medicalRecord);
        }

        @Test
        @DisplayName("GIVEN a consistent list of medical records " +
                "WHEN saving the list of medical records " +
                "THEN it is saved in DB and the saved list of medical records is returned")
        public void saveListOfMedicalRecordsTest_WithConsistentList() {
            //GIVEN
            when(medicalRecordRepositoryMock.saveAll(listOfMedicalRecords)).thenReturn(listOfMedicalRecords);

            //WHEN
            Iterable<MedicalRecord> savedListOfMedicalRecords = medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords);

            //THEN
            assertNotNull(savedListOfMedicalRecords);
            assertThat(savedListOfMedicalRecords).isNotEmpty();
            verify(medicalRecordRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }

        @Test
        @DisplayName("GIVEN an exception when processing WHEN saving the list of medical records " +
                "THEN no data is saved in DB and return code is false")
        public void saveListOfMedicalRecordsTest_WithException() {
            //GIVEN
            when(medicalRecordRepositoryMock.saveAll(listOfMedicalRecords)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords));
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
        @DisplayName("GIVEN medical records in DB WHEN asking for all medical records " +
                "THEN a list of medical records is returned")
        public void getAllMedicalRecordsTest_WithMedicalRecordDataInDb() {
            //GIVEN
            List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
            medicalRecord.setMedicalRecordId(100L);
            listOfMedicalRecords.add(medicalRecord);
            when(medicalRecordRepositoryMock.findAll()).thenReturn(listOfMedicalRecords);

            List<MedicalRecordDTO> expectedListOfMedicalRecordsDTO = new ArrayList<>();
            MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
            medicalRecordDTO.setMedicalRecordId(medicalRecord.getMedicalRecordId());
            medicalRecordDTO.setFirstName(medicalRecord.getFirstName());
            medicalRecordDTO.setLastName(medicalRecord.getLastName());
            medicalRecordDTO.setBirthDate(medicalRecord.getBirthDate());
            medicalRecordDTO.setMedications(medicalRecord.getMedications());
            medicalRecordDTO.setAllergies(medicalRecord.getAllergies());
            expectedListOfMedicalRecordsDTO.add(medicalRecordDTO);

            //THEN
            assertEquals(expectedListOfMedicalRecordsDTO, medicalRecordService.getAllMedicalRecords());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).findAll();

        }

        @Test
        @DisplayName("GIVEN an exception WHEN asking for all medical records THEN an empty list is returned")
        public void getAllMedicalRecordsTest_WithException() {
            //GIVEN
            when(medicalRecordRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertThat(medicalRecordService.getAllMedicalRecords()).isEmpty();
            verify(medicalRecordRepositoryMock, Mockito.times(1)).findAll();

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addMedicalRecord tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addMedicalRecord tests")
    class AddMedicalRecordTest {

        private
        MedicalRecordDTO medicalRecordDTOToAdd;

        @BeforeEach
        private void setUpPerTest() {
            medicalRecordDTOToAdd = new MedicalRecordDTO();
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
        }

        @Test
        @DisplayName("GIVEN a new medical record to add " +
                "WHEN saving this new medical record " +
                "THEN the returned value is the added medical record")
        public void addMedicalRecordTest_WithSuccess() throws AlreadyExistsException, MissingInformationException, DoesNotExistException {
            //GIVEN
            MedicalRecord expectedAddedMedicalRecord = new MedicalRecord();
            expectedAddedMedicalRecord.setMedicalRecordId(100L);
            expectedAddedMedicalRecord.setFirstName(medicalRecordDTOToAdd.getFirstName());
            expectedAddedMedicalRecord.setLastName(medicalRecordDTOToAdd.getLastName());
            expectedAddedMedicalRecord.setBirthDate(medicalRecordDTOToAdd.getBirthDate());
            expectedAddedMedicalRecord.setMedications(medicalRecordDTOToAdd.getMedications());
            expectedAddedMedicalRecord.setAllergies(medicalRecordDTOToAdd.getAllergies());

            Person person = new Person();
            person.setPersonId(100L);
            person.setFirstName(medicalRecordDTOToAdd.getFirstName());
            person.setLastName(medicalRecordDTOToAdd.getLastName());

            when(medicalRecordRepositoryMock
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()))
                    .thenReturn(new ArrayList<>());
            when(personRepositoryMock
                    .findByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()))
                    .thenReturn(person);
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
                    .findByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).save(any(MedicalRecord.class));
            verify(personRepositoryMock, Mockito.times(1)).save(any(Person.class));
        }


        @Test
        @DisplayName("GIVEN a medical record already present in repository " +
                "WHEN saving this new medical record " +
                "THEN an AlreadyExistsException is thrown")
        public void addMedicalRecordTest_WithExistingMedicalRecordInRepository() {
            //GIVEN
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
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN an empty medical record " +
                "WHEN saving this new medical record " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithMissingInformation() {
            //GIVEN
            MedicalRecordDTO emptyMedicalRecordDTOTOAdd = new MedicalRecordDTO();

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(emptyMedicalRecordDTOTOAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(null, null);
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(), anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN a new medical record without firstname " +
                "WHEN saving this new medical record " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithoutFirstName() {
            //GIVEN
            medicalRecordDTOToAdd.setFirstName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(null, medicalRecordDTOToAdd.getLastName());
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(), anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }

        @Test
        @DisplayName("GIVEN a new medical record without lastname " +
                "WHEN saving this new medical record " +
                "THEN an MissingInformationException is thrown")
        public void addMedicalRecordTest_WithoutLastName() {
            //GIVEN
            medicalRecordDTOToAdd.setLastName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findAllByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), null);
            verify(personRepositoryMock, Mockito.times(0)).findAllByFirstNameAndLastName(anyString(), anyString());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
            verify(personRepositoryMock, Mockito.times(0)).save(any(Person.class));
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updateMedicalRecord tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updateMedicalRecord tests")
    class UpdateMedicalRecordTest {

        private
        MedicalRecordDTO medicalRecordDTOToUpdate;

        @BeforeEach
        private void setUpPerTest() {
            medicalRecordDTOToUpdate = new MedicalRecordDTO();
            medicalRecordDTOToUpdate.setFirstName("MRST_first_name");
            medicalRecordDTOToUpdate.setLastName("MRST_last_name");
            medicalRecordDTOToUpdate.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("MRST_medications_1");
            medications.add("MRST_medications_2");
            medications.add("MRST_medications_3");
            medicalRecordDTOToUpdate.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("MRST_allergies_1");
            allergies.add("MRST_allergies_2");
            medicalRecordDTOToUpdate.setAllergies(allergies);
        }

        @Test
        @DisplayName("GIVEN a medical record to update " +
                "WHEN updating this medical record " +
                "THEN the returned value is the updated medical record")
        public void updateMedicalRecordTest_WithSuccess() throws DoesNotExistException, MissingInformationException {
            //GIVEN
            MedicalRecord expectedUpdatedMedicalRecord = new MedicalRecord();
            expectedUpdatedMedicalRecord.setMedicalRecordId(100L);
            expectedUpdatedMedicalRecord.setFirstName(medicalRecordDTOToUpdate.getFirstName());
            expectedUpdatedMedicalRecord.setLastName(medicalRecordDTOToUpdate.getLastName());
            expectedUpdatedMedicalRecord.setBirthDate(medicalRecordDTOToUpdate.getBirthDate());
            when(medicalRecordRepositoryMock
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(),medicalRecordDTOToUpdate.getLastName()))
                    .thenReturn(expectedUpdatedMedicalRecord);

            expectedUpdatedMedicalRecord.setMedications(medicalRecordDTOToUpdate.getMedications());
            expectedUpdatedMedicalRecord.setAllergies(medicalRecordDTOToUpdate.getAllergies());
            when(medicalRecordRepositoryMock.save(any(MedicalRecord.class))).thenReturn(expectedUpdatedMedicalRecord);

            //WHEN
            MedicalRecordDTO updatedMedicalRecordDTO = medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate);

            //THEN
            medicalRecordDTOToUpdate.setMedicalRecordId(expectedUpdatedMedicalRecord.getMedicalRecordId());
            assertEquals(medicalRecordDTOToUpdate, updatedMedicalRecordDTO);
            assertNotNull(updatedMedicalRecordDTO.getMedicalRecordId());
            verify(medicalRecordRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(), medicalRecordDTOToUpdate.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).save(any(MedicalRecord.class));

        }


        @Test
        @DisplayName("GIVEN a medical record to update not present in repository " +
                "WHEN updating this medical record " +
                "THEN an DoesNotExistException is thrown and no medical record has been updated")
        public void updateMedicalRecordTest_WithNoExistingMedicalRecordInRepository() {
            //GIVEN
            when(medicalRecordRepositoryMock
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(),medicalRecordDTOToUpdate.getLastName()))
                    .thenReturn(null);

            //THEN
            assertThrows(DoesNotExistException.class, () -> medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate));
            verify(medicalRecordRepositoryMock, Mockito.times(1))
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(), medicalRecordDTOToUpdate.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any(MedicalRecord.class));
        }

        @Test
        @DisplayName("GIVEN an empty medical record " +
                "WHEN updating this medical record " +
                "THEN an MissingInformationException is thrown")
        public void updateMedicalRecordTest_WithMissingInformation() {
            //GIVEN
            MedicalRecordDTO emptyMedicalRecordDTOToUpdate = new MedicalRecordDTO();

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.updateMedicalRecord(emptyMedicalRecordDTOToUpdate));
            verify(medicalRecordRepositoryMock, Mockito.times(0)).findByFirstNameAndLastName(null, null);
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }

        @Test
        @DisplayName("GIVEN a medical record to update without firstname " +
                "WHEN updating this medical record " +
                "THEN an MissingInformationException is thrown")
        public void updateMedicalRecordTest_WithoutFirstName() {
            //GIVEN
            medicalRecordDTOToUpdate.setFirstName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findByFirstNameAndLastName(null, medicalRecordDTOToUpdate.getLastName());
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }

        @Test
        @DisplayName("GIVEN a medical record to update without lastname " +
                "WHEN updating this new medical record " +
                "THEN an MissingInformationException is thrown")
        public void updateMedicalRecordTest_WithoutLastName() {
            //GIVEN
            medicalRecordDTOToUpdate.setLastName(null);

            //THEN
            assertThrows(MissingInformationException.class, () -> medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate));
            verify(medicalRecordRepositoryMock, Mockito.times(0))
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(), null);
            verify(medicalRecordRepositoryMock, Mockito.times(0)).save(any((MedicalRecord.class)));
        }
    }

}
