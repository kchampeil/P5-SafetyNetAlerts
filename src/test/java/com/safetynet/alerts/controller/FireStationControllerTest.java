package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
import com.safetynet.alerts.model.dto.PersonCoveredDTO;
import com.safetynet.alerts.service.IFireStationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = FireStationController.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFireStationService fireStationServiceMock;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static List<PersonCoveredDTO> listOfPersonsCovered;

    @BeforeAll
    private static void setUp() {
        listOfPersonsCovered = new ArrayList<>();
        PersonCoveredDTO personCoveredDTO = new PersonCoveredDTO();
        personCoveredDTO.setLastName("FSCT_lastname");
        personCoveredDTO.setPhone("FSCT_phone");
        personCoveredDTO.setAge(TestConstants.ADULT_AGE);
        personCoveredDTO.setMedications(new ArrayList<>());
        personCoveredDTO.setAllergies(new ArrayList<>());
        listOfPersonsCovered.add(personCoveredDTO);
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetAllFireStationsTest {

        @Test
        @DisplayName("GIVEN data in DB WHEN asking for the list of fire stations GET /firestations " +
                "THEN return status is ok and a list of fire stations is returned")
        public void getAllFireStationsTest_WithData() throws Exception {
            // GIVEN
            List<FireStationDTO> listOfFireStationsDTO = new ArrayList<>();
            FireStationDTO fireStationDTO = new FireStationDTO();
            fireStationDTO.setFireStationId(100L);
            fireStationDTO.setStationNumber(2021);
            fireStationDTO.setAddress("FSCT_Address");
            listOfFireStationsDTO.add(fireStationDTO);
            when(fireStationServiceMock.getAllFireStations()).thenReturn(listOfFireStationsDTO);

            //THEN
            mockMvc.perform(get("/firestations"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no data in DB WHEN asking for the list of fire stations GET /firestations " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getAllFireStationsTest_WithoutData() throws Exception {
            // GIVEN
            List<FireStationDTO> emptyListOfFireStationsDTO = new ArrayList<>();
            when(fireStationServiceMock.getAllFireStations()).thenReturn(emptyListOfFireStationsDTO);

            //THEN
            mockMvc.perform(get("/firestations"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetFireStationCoverageByAddressTest {

        private FireDTO fireDTO;

        @BeforeEach
        private void setUpPerTest() {
            fireDTO = new FireDTO();
        }

        @Test
        @DisplayName("GIVEN persons in repository living at the requested address and one fire station covering this address " +
                "WHEN processing a GET /fire request on address " +
                "THEN coverage information is returned")
        public void getFireStationCoverageByAddressTest_WithResults() throws Exception {
            // GIVEN
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
                "THEN return status is 'not found' and an empty coverage info is returned")
        public void getFireStationCoverageByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFireStationCoverageByAddress("AddressTestNotFound"))
                    .thenReturn(fireDTO);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", "AddressTestNotFound"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.personCoveredDTO").doesNotExist());
        }


        @Test
        @DisplayName("GIVEN null address " +
                "WHEN processing a GET /fire request on address " +
                "THEN return status is 'bad request'")
        public void getFireStationCoverageByAddressTest_WithNoAddressAsInput() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFireStationCoverageByAddress(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", (String) null))
                    .andExpect(status().isBadRequest());
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFloodByStationNumbers tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFloodByStationNumbers tests")
    class GetFloodByStationNumbersTest {

        private List<FloodDTO> listOfFloodDTO;

        @BeforeEach
        private void setUpPerTest() {
            listOfFloodDTO = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by one of the fire station of the given list " +
                "WHEN processing a GET /flood/stations request on station numbers " +
                "THEN coverage information is returned")
        public void getFloodByStationNumbersTest_WithResults() throws Exception {
            // GIVEN
            FloodDTO floodDTO = new FloodDTO();
            floodDTO.setStationNumber(73);
            Map<String, List<PersonCoveredDTO>> personCoveredDTOByAddress = new HashMap<>();
            personCoveredDTOByAddress.put("FSCT_Address", listOfPersonsCovered);
            floodDTO.setPersonsCoveredByAddress(personCoveredDTOByAddress);
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
                "THEN return status is 'not found' and an empty coverage info is returned")
        public void getFloodByStationNumbersTest_WithNoResults() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFloodByStationNumbers(Collections.singletonList(999)))
                    .thenReturn(listOfFloodDTO);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", "999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.personsCoveredByAddress").doesNotExist());
        }


        @Test
        @DisplayName("GIVEN null list of station numbers " +
                "WHEN processing a GET /flood/stations request on address " +
                "THEN return status is 'bad request'")
        public void getFloodByStationNumbersTest_WithNoAddressAsInput() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFloodByStationNumbers(null)).thenReturn(null);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", (String) null))
                    .andExpect(status().isBadRequest());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addFireStation tests")
    class AddFireStationTest {

        private FireStationDTO fireStationDTOToAdd;

        @BeforeEach
        private void setUpPerTest() {
            fireStationDTOToAdd = new FireStationDTO();
            fireStationDTOToAdd.setStationNumber(3);
            fireStationDTOToAdd.setAddress("FSCT_Add_Address");
        }

        @Test
        @DisplayName("GIVEN a new mapping address/fire station " +
                "WHEN processing a POST /firestation request for this fire station " +
                "THEN the returned value is the added fire station")
        public void addFireStationTest_WithSuccess() throws Exception {
            // GIVEN
            FireStationDTO addedFireStationDTO = new FireStationDTO();
            addedFireStationDTO.setFireStationId(3L);
            addedFireStationDTO.setStationNumber(fireStationDTOToAdd.getStationNumber());
            addedFireStationDTO.setAddress(fireStationDTOToAdd.getAddress());

            when(fireStationServiceMock.addFireStation(fireStationDTOToAdd))
                    .thenReturn(addedFireStationDTO);

            // THEN

            mockMvc.perform(post("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(fireStationServiceMock, Mockito.times(1)).addFireStation(fireStationDTOToAdd);
        }


        @Test
        @DisplayName("GIVEN a fire station with missing address " +
                "WHEN processing a POST /firestation request for this fire station " +
                "THEN the returned code is 'bad request'")
        public void addFireStationTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.addFireStation(fireStationDTOToAdd))
                    .thenThrow(new MissingInformationException("Address is missing"));

            // THEN
            mockMvc.perform(post("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station for an existing address in repository " +
                "WHEN processing a POST /firestation request for this fire station " +
                "THEN the returned code is 'bad request'")
        public void addFireStationTest_WithExistingFireStation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.addFireStation(fireStationDTOToAdd))
                    .thenThrow(new AlreadyExistsException("Person already exists"));

            // THEN
            mockMvc.perform(post("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                    .andExpect(status().isBadRequest());
            verify(fireStationServiceMock, Mockito.times(1)).addFireStation(fireStationDTOToAdd);
        }

    }

}
