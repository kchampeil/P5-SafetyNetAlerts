package com.safetynet.alerts.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = FireStationController.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationServiceMock;


    @Test
    @DisplayName("WHEN asking for the list of fire stations (GET) THEN return status is ok")
    void getAllFireStationsTest() throws Exception {

        //IN-PROGRESS
        //TODO when(fireStationServiceMock.getAllFireStations()).thenReturn(any(List< FireStation >));
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk());
        //TODO en tests d'intégration .andExpect(jsonPath("$[0].firstName", is("John"))); avec @SpringBootTest
    }

}
