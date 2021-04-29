package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.PhoneAlertService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PhoneAlertController.class)
class PhoneAlertControllerTest {

    @Test
    void getPhoneAlertByFireStation() {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhoneAlertService phoneAlertServiceMock;

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPhoneAlertByFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPhoneAlertByFireStation tests")
    class GetPhoneAlertByFireStation {
        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by the requested fire station " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN a list of phone number is returned")
        void getPhoneAlertByFireStationTest_WithResults() throws Exception {
            // GIVEN
            List<String > listOfPhoneNumbers = new ArrayList<>();
            listOfPhoneNumbers.add("33-1 23 45 67 89");
            listOfPhoneNumbers.add("33-1 98 76 54 32");
            listOfPhoneNumbers.add("33 1 11 11 11 11");

            when(phoneAlertServiceMock.getPhoneAlertByFireStation(3))
                    .thenReturn(listOfPhoneNumbers);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", "3"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no person found for requested fire station number not found in repository " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN the returned list is null")
        public void getPhoneAlertByFireStationTest_WithNoResults() throws Exception{
            // GIVEN
            List<String> listOfPhoneNumbers = new ArrayList<>();
            when(phoneAlertServiceMock.getPhoneAlertByFireStation(2))
                    .thenReturn(listOfPhoneNumbers);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", "2"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }


        @Test
        @DisplayName("GIVEN null fire station number " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPhoneAlertByFireStationTest_WithNoStationNumberAsInput() throws Exception {
            // GIVEN
            when(phoneAlertServiceMock.getPhoneAlertByFireStation(anyInt())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", String.valueOf(999)))
                    .andExpect(status().isBadRequest());
        }
    }

}
