package com.safetynet.alerts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
class JsonParserServiceTest {

    @MockBean
    private PersonService personServiceMock;

    @MockBean
    private FireStationService fireStationServiceMock;

    @MockBean
    private MedicalRecordService medicalRecordServiceMock;

    @Autowired
    private IFileParserService jsonParserService;


    @Test
    @DisplayName("GIVEN a correct and complete json file WHEN parsing the file " +
            "THEN lists of persons, fire stations and medical records are saved")
    public void readDataFromFileTest_WithCorrectFile() {
        //GIVEN
        ReflectionTestUtils.setField(jsonParserService, "dataInputFilePath", "test_complete_data.json");

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(medicalRecordServiceMock, Mockito.times(1)).saveListOfMedicalRecords(anyList());
        verify(fireStationServiceMock, Mockito.times(1)).saveListOfFireStations(anyList());
        verify(personServiceMock, Mockito.times(1)).saveListOfPersons(anyList());
    }


    @Test
    @DisplayName("GIVEN an empty json file WHEN parsing the file THEN no data are saved")
    public void readDataFromFileTest_WithEmptyFile() {
        //GIVEN
        ReflectionTestUtils.setField(jsonParserService, "dataInputFilePath", "test_empty_data.json");

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(medicalRecordServiceMock, Mockito.times(0)).saveListOfMedicalRecords(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).saveListOfFireStations(anyList());
        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
    }


    @Test
    @DisplayName("GIVEN no available json file WHEN parsing the file THEN no data are saved")
    public void readDataFromFileTest_WithNoAvailableInputFile() {
        //GIVEN
        ReflectionTestUtils.setField(jsonParserService, "dataInputFilePath", "unknown_data_file.json");

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(medicalRecordServiceMock, Mockito.times(0)).saveListOfMedicalRecords(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).saveListOfFireStations(anyList());
        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
    }


    @Test
    @DisplayName("GIVEN a json file with a different structure WHEN parsing the file THEN no data are saved")
    public void readDataFromFileTest_WithUnknownStructure() {
        //GIVEN
        ReflectionTestUtils.setField(jsonParserService, "dataInputFilePath", "test_holidays.json");

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(medicalRecordServiceMock, Mockito.times(0)).saveListOfMedicalRecords(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).saveListOfFireStations(anyList());
        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
    }

}