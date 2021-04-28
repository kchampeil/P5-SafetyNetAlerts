package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class MedicalRecordServiceTest {

    @MockBean
    private MedicalRecordRepository medicalRecordRepositoryMock;

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
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN medical records in DB
     * WHEN processing a GET /medicalrecords request
     * THEN a list of medical records is returned
     *
     * GIVEN an exception
     * WHEN processing a GET /medicalrecords request
     * THEN null is returned
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
            assertNull( medicalRecordService.getAllMedicalRecords());
            verify(medicalRecordRepositoryMock, Mockito.times(1)).findAll();

        }
    }
}