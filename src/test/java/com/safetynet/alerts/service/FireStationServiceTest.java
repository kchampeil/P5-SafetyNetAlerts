package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
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
}
