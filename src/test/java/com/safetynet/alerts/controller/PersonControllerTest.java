package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.PersonService;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllEmailsByCity tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllEmailsByCity tests")
    class GetAllEmailsByCityTest {
        @Test
        @DisplayName("GIVEN a city name known in the repository" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is ok and the result is filled with emails")
        void getAllEmailsByCityTest_WithResultsForCity() throws Exception {
            // GIVEN
            List<String> listOfEmails = new ArrayList<>();
            listOfEmails.add("email1@test.com");
            listOfEmails.add("email2@test.com");
            listOfEmails.add("email3@test.com");
            when(personServiceMock.getAllEmailsByCity("CityTest")).thenReturn(listOfEmails);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", "CityTest"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("[\"email1@test.com\",\"email2@test.com\",\"email3@test.com\"]"));
        }

        @Test
        @DisplayName("GIVEN a city name not known in the repository" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is not found but the result is empty")
        void getAllEmailsByCityTest_WithNoResultsForCity() throws Exception {
            // GIVEN
            List<String> listOfEmails = new ArrayList<>();
            when(personServiceMock.getAllEmailsByCity("CityTestNotKnown")).thenReturn(listOfEmails);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", "CityTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("GIVEN no city name as input" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is bad request and the result is null")
        void getAllEmailsByCityTest_WithNoCityAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getAllEmailsByCity(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", "CityTestNull"))
                    .andExpect(status().isBadRequest());
        }
    }

}
