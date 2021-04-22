package com.safetynet.alerts.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.service.PersonService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personServiceMock;

    @Test
    @DisplayName("WHEN asking for the list of persons (GET) THEN return status is ok")
    void getAllPersonsTest() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk());
                //TODO en tests d'intégration .andExpect(jsonPath("$[0].firstName", is("John"))); avec @SpringBootTest
    }

    @Test
    @DisplayName("GIVEN a city name WHEN asking for the lis of emails of all citizens THEN return status is ok")
    void getAllEmailsByCityTest() throws Exception {
        mockMvc.perform(get("/communityEmail"))
                .andExpect(status().isOk());
    }
}
