package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
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
class FireStationServiceTest {

    @MockBean
    private FireStationRepository fireStationRepositoryMock;

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

    @Test
    @DisplayName("GIVEN a consistent list of fire stations THEN it is saved in DB and return code is true")
    public void saveListOfFireStationsTest_WithConsistentList() {
        //GIVEN

        //WHEN
        List<FireStation> listOfFireStations = new ArrayList<>();
        listOfFireStations.add(fireStation);
        fireStationService.savelistOfFireStations(listOfFireStations);

        //THEN
        verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertTrue(fireStationService.savelistOfFireStations(listOfFireStations));

    }

    @Test
    @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
    public void saveListOfFireStationsTest_WithException() {
        //GIVEN
        List<FireStation> listOfFireStations = new ArrayList<>();
        listOfFireStations.add(fireStation);
        when(fireStationRepositoryMock.saveAll(listOfFireStations)).thenThrow(IllegalArgumentException.class);

        //WHEN
        fireStationService.savelistOfFireStations(listOfFireStations);

        //THEN
        verify(fireStationRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertFalse(fireStationService.savelistOfFireStations(listOfFireStations));

    }
}
