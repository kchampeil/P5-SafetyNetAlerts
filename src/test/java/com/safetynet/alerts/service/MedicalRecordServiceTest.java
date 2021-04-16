package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("GIVEN a consistent list of persons THEN it is saved in DB and return code is true")
    public void saveListOfPersonsTest_WithConsistentList() {
        //GIVEN

        //WHEN
        List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
        listOfMedicalRecords.add(medicalRecord);
        medicalRecordService.savelistOfMedicalRecords(listOfMedicalRecords);

        //THEN
        verify(medicalRecordRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertTrue(medicalRecordService.savelistOfMedicalRecords(listOfMedicalRecords));

    }

    @Test
    @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
    public void saveListOfPersonsTest_WithException() {
        //GIVEN
        List<MedicalRecord> listOfMedicalRecords = new ArrayList<>();
        listOfMedicalRecords.add(medicalRecord);
        when(medicalRecordRepositoryMock.saveAll(listOfMedicalRecords)).thenThrow(IllegalArgumentException.class);

        //WHEN
        medicalRecordService.savelistOfMedicalRecords(listOfMedicalRecords);

        //THEN
        verify(medicalRecordRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertFalse(medicalRecordService.savelistOfMedicalRecords(listOfMedicalRecords));

    }
}