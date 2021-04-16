package com.safetynet.alerts.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@SpringBootTest
class JsonParserServiceTest {

    @MockBean
    private PersonService personServiceMock;

    @MockBean
    private FireStationService fireStationServiceMock;

    @MockBean
    private MedicalRecordService medicalRecordServiceMock;

    @Autowired
    private JsonParserService jsonParserService;


    @Test
    @DisplayName("GIVEN a correct and complete json file WHEN parsing the file THEN lists of persons, fire stations and medical records are saved")
    public void readDataFromFileTest_WithCorrectFile() {
        //GIVEN data.inputFilePath= complete_data.json

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN

        verify(personServiceMock, Mockito.times(1)).saveListOfPersons(anyList());
        verify(fireStationServiceMock, Mockito.times(1)).savelistOfFireStations(anyList());
        verify(medicalRecordServiceMock, Mockito.times(1)).savelistOfMedicalRecords(anyList());
    }

    //TODO test à écrire : comment passer en paramètre le chemin vers le fichier vide
/*    @Test
    @DisplayName("GIVEN an empty json file WHEN parsing the file THEN no data are saved")
    public void readDataFromFileTest_WithEmptyFile() {
        //GIVEN data.inputFilePath= empty_data.json

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN

        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).savelistOfFireStations(anyList());
        verify(medicalRecordServiceMock, Mockito.times(0)).savelistOfMedicalRecords(anyList());
    }

    //TODO test à écrire : comment passer en paramètre le chemin vers un fichier inexistant
    @Test
    @DisplayName("GIVEN no available json file WHEN parsing the file THEN an error is generated and no data are saved")
    public void readDataFromFileTest_WithNoAvailableInputFile() {
        //GIVEN data.inputFilePath= unknown_data_file.json

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).savelistOfFireStations(anyList());
        verify(medicalRecordServiceMock, Mockito.times(0)).savelistOfMedicalRecords(anyList());
    }

    //TODO test à écrire : comment passer en paramètre le chemin vers le fichier au format différent
    @Test
    @DisplayName("GIVEN a json file with a different structure WHEN parsing the file THEN an error is generated and no data are saved")
    public void readDataFromFileTest_WithUnknownStructure() {
        //GIVEN data.inputFilePath= holidays.json

        //WHEN
        jsonParserService.readDataFromFile();

        //THEN
        verify(personServiceMock, Mockito.times(0)).saveListOfPersons(anyList());
        verify(fireStationServiceMock, Mockito.times(0)).savelistOfFireStations(anyList());
        verify(medicalRecordServiceMock, Mockito.times(0)).savelistOfMedicalRecords(anyList());
    }


 */
}