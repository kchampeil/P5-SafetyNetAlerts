package com.safetynet.alerts.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
import com.safetynet.alerts.model.dto.PersonCoveredDTO;
import com.safetynet.alerts.service.FireStationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebMvcTest(controllers = FireStationController.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationServiceMock;


    @Test
    @DisplayName("WHEN asking for the list of fire stations (GET) THEN return status is ok")
    public void getAllFireStationsTest() throws Exception {

        //TODO when(fireStationServiceMock.getAllFireStations()).thenReturn(any(List< FireStation >));
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk());
        //TODO en tests d'intégration .andExpect(jsonPath("$[0].firstName", is("John"))); avec @SpringBootTest
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetFireStationCoverageByAddressTest {
        @Test
        @DisplayName("GIVEN persons in repository living at the requested address and one fire station covering this address " +
                "WHEN processing a GET /fire request on address " +
                "THEN coverage information is returned")
        public void getFireStationCoverageByAddressTest_WithResults() throws Exception {
            // GIVEN
            List<PersonCoveredDTO> listOfPersonsCovered = new ArrayList<>();
            PersonCoveredDTO personCoveredDTO = new PersonCoveredDTO();
            personCoveredDTO.setLastName("FSCT_lastname");
            personCoveredDTO.setPhone("FSCT_phone");
            personCoveredDTO.setAge(TestConstants.ADULT_AGE);
            personCoveredDTO.setMedications(new ArrayList<>());
            personCoveredDTO.setAllergies(new ArrayList<>());
            listOfPersonsCovered.add(personCoveredDTO);

            FireDTO fireDTO = new FireDTO();
            fireDTO.setPersonCoveredDTOList(listOfPersonsCovered);
            fireDTO.setStationNumber(73);

            when(fireStationServiceMock.getFireStationCoverageByAddress("AddressTest"))
                    .thenReturn(fireDTO);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", "AddressTest"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no person found for requested address in repository " +
                "WHEN processing a GET /fire request on address " +
                "THEN the returned coverage information is null")
        public void getFireStationCoverageByAddressTest_WithNoResults() throws Exception{
            // GIVEN
            FireDTO fireDTO = new FireDTO();
            when(fireStationServiceMock.getFireStationCoverageByAddress("AddressTestNotFound"))
                    .thenReturn(fireDTO);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", "AddressTestNotFound"))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("GIVEN null address " +
                "WHEN processing a GET /fire request on address " +
                "THEN the returned coverage information is null and no request has been sent to repository")
        public void getFireStationCoverageByAddressTest_WithNoAddressAsInput() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFireStationCoverageByAddress(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", "AddressTestNull"))
                    .andExpect(status().isBadRequest());
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFloodByStationNumbers tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFloodByStationNumbers tests")
    class GetFloodByStationNumbersTest {
        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by one of the fire station of the given list " +
                "WHEN processing a GET /flood/stations request on station numbers " +
                "THEN coverage information is returned")
        public void getFloodByStationNumbersTest_WithResults() throws Exception {
            // GIVEN
            List<PersonCoveredDTO> listOfPersonsCovered = new ArrayList<>();
            PersonCoveredDTO personCoveredDTO = new PersonCoveredDTO();
            personCoveredDTO.setLastName("FSCT_lastname");
            personCoveredDTO.setPhone("FSCT_phone");
            personCoveredDTO.setAge(TestConstants.ADULT_AGE);
            personCoveredDTO.setMedications(new ArrayList<>());
            personCoveredDTO.setAllergies(new ArrayList<>());
            listOfPersonsCovered.add(personCoveredDTO);

            FloodDTO floodDTO = new FloodDTO();
            floodDTO.setStationNumber(73);
            Map<String, List<PersonCoveredDTO>> personCoveredDTOByAddress = new HashMap<>();
            personCoveredDTOByAddress.put("FSCT_Address",listOfPersonsCovered);
            floodDTO.setPersonsCoveredByAddress(personCoveredDTOByAddress);
            List<FloodDTO> listOfFloodDTO = new ArrayList<>();
            listOfFloodDTO.add(floodDTO);

            when(fireStationServiceMock.getFloodByStationNumbers(Collections.singletonList(73)))
                    .thenReturn(listOfFloodDTO);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", "73"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no person found for requested fire stations in repository " +
                "WHEN processing a GET /flood/stations request on station numbers " +
                "THEN the returned coverage information is null")
        public void getFloodByStationNumbersTest_WithNoResults() throws Exception{
            // GIVEN
            FloodDTO floodDTO = new FloodDTO();
            List<FloodDTO> listOfFloodDTO = new ArrayList<>();
            when(fireStationServiceMock.getFloodByStationNumbers(Collections.singletonList(999)))
                    .thenReturn(listOfFloodDTO);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", "999"))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("GIVEN null list of station numbers " +
                "WHEN processing a GET /flood/stations request on address " +
                "THEN the returned coverage information is null and no request has been sent to repository")
        public void getFloodByStationNumbersTest_WithNoAddressAsInput() throws Exception {
            // GIVEN
            List<Integer> listOfStationNumbers = new ArrayList<>();
            when(fireStationServiceMock.getFloodByStationNumbers(listOfStationNumbers)).thenReturn(null);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", String.valueOf(listOfStationNumbers)))
                    .andExpect(status().isBadRequest());
        }
    }

}
