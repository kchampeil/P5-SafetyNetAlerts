package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.ExceptionConstants;
import com.safetynet.alerts.testconstants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private static FireStation deletedFireStation;

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

        deletedFireStation = new FireStation();
        deletedFireStation.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
        deletedFireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        deletedFireStation.setAddress(TestConstants.EXISTING_ADDRESS);
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllFireStations tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllFireStations tests")
    class GetAllFireStationsTest {

        @Test
        @DisplayName("GIVEN data in DB WHEN asking for the list of fire stations GET /firestations " +
                "THEN return status is ok and a list of fire stations is returned")
        public void getAllFireStationsTest_WithData() throws Exception {
            // GIVEN
            List<FireStationDTO> listOfFireStationsDTO = new ArrayList<>();
            FireStationDTO fireStationDTO = new FireStationDTO();
            fireStationDTO.setFireStationId(TestConstants.EXISTING_STATION_NUMBER.longValue());
            fireStationDTO.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            fireStationDTO.setAddress(TestConstants.EXISTING_ADDRESS);
            listOfFireStationsDTO.add(fireStationDTO);
            when(fireStationServiceMock.getAllFireStations()).thenReturn(listOfFireStationsDTO);

            //THEN
            mockMvc.perform(get("/firestations"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(fireStationServiceMock, Mockito.times(1)).getAllFireStations();
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
            fireDTO.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);

            when(fireStationServiceMock.getFireStationCoverageByAddress(TestConstants.EXISTING_ADDRESS))
                    .thenReturn(fireDTO);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", TestConstants.EXISTING_ADDRESS))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(fireStationServiceMock, Mockito.times(1))
                    .getFireStationCoverageByAddress(TestConstants.EXISTING_ADDRESS);
        }


        @Test
        @DisplayName("GIVEN no person found for requested address in repository " +
                "WHEN processing a GET /fire request on address " +
                "THEN return status is 'not found' and an empty coverage info is returned")
        public void getFireStationCoverageByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFireStationCoverageByAddress(TestConstants.ADDRESS_NOT_FOUND))
                    .thenReturn(fireDTO);

            // THEN
            mockMvc.perform(get("/fire")
                    .param("address", TestConstants.ADDRESS_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.personCoveredDTO").doesNotExist());
            verify(fireStationServiceMock, Mockito.times(1))
                    .getFireStationCoverageByAddress(TestConstants.ADDRESS_NOT_FOUND);
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
            floodDTO.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
            Map<String, List<PersonCoveredDTO>> personCoveredDTOByAddress = new HashMap<>();
            personCoveredDTOByAddress.put(TestConstants.EXISTING_ADDRESS, listOfPersonsCovered);
            floodDTO.setPersonsCoveredByAddress(personCoveredDTOByAddress);
            listOfFloodDTO.add(floodDTO);

            when(fireStationServiceMock.getFloodByStationNumbers(Collections.singletonList(TestConstants.EXISTING_STATION_NUMBER)))
                    .thenReturn(listOfFloodDTO);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", TestConstants.EXISTING_STATION_NUMBER.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(fireStationServiceMock, Mockito.times(1))
                    .getFloodByStationNumbers(Collections.singletonList(TestConstants.EXISTING_STATION_NUMBER));
        }


        @Test
        @DisplayName("GIVEN no person found for requested fire stations in repository " +
                "WHEN processing a GET /flood/stations request on station numbers " +
                "THEN return status is 'not found' and an empty coverage info is returned")
        public void getFloodByStationNumbersTest_WithNoResults() throws Exception {
            // GIVEN
            when(fireStationServiceMock.getFloodByStationNumbers(Collections.singletonList(TestConstants.STATION_NUMBER_NOT_FOUND)))
                    .thenReturn(listOfFloodDTO);

            // THEN
            mockMvc.perform(get("/flood/stations")
                    .param("stations", TestConstants.STATION_NUMBER_NOT_FOUND.toString()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.personsCoveredByAddress").doesNotExist());
            verify(fireStationServiceMock, Mockito.times(1))
                    .getFloodByStationNumbers(Collections.singletonList(TestConstants.STATION_NUMBER_NOT_FOUND));
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
            fireStationDTOToAdd.setStationNumber(TestConstants.NEW_STATION_NUMBER);
            fireStationDTOToAdd.setAddress(TestConstants.NEW_ADDRESS);
        }

        @Test
        @DisplayName("GIVEN a new mapping address/fire station " +
                "WHEN processing a POST /firestation request for this fire station " +
                "THEN the returned value is the added fire station")
        public void addFireStationTest_WithSuccess() throws Exception {
            // GIVEN
            FireStationDTO addedFireStationDTO = new FireStationDTO();
            addedFireStationDTO.setFireStationId(fireStationDTOToAdd.getStationNumber().longValue());
            addedFireStationDTO.setStationNumber(fireStationDTOToAdd.getStationNumber());
            addedFireStationDTO.setAddress(fireStationDTOToAdd.getAddress());

            when(fireStationServiceMock.addFireStation(fireStationDTOToAdd))
                    .thenReturn(Optional.of(addedFireStationDTO));

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
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS));

            // THEN
            mockMvc.perform(post("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS)));
            verify(fireStationServiceMock, Mockito.times(1)).addFireStation(fireStationDTOToAdd);
        }


        @Test
        @DisplayName("GIVEN a new mapping address/fire station for an existing address in repository " +
                "WHEN processing a POST /firestation request for this fire station " +
                "THEN the returned code is 'bad request'")
        public void addFireStationTest_WithExistingFireStation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.addFireStation(fireStationDTOToAdd))
                    .thenThrow(new AlreadyExistsException(ExceptionConstants.ALREADY_EXIST_FIRE_STATION_FOR_ADDRESS));

            // THEN
            mockMvc.perform(post("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.ALREADY_EXIST_FIRE_STATION_FOR_ADDRESS)));
            verify(fireStationServiceMock, Mockito.times(1)).addFireStation(fireStationDTOToAdd);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updateFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updateFireStation tests")
    class UpdateFireStationTest {

        private FireStationDTO fireStationDTOToUpdate;

        @BeforeEach
        private void setUpPerTest() {
            fireStationDTOToUpdate = new FireStationDTO();
            fireStationDTOToUpdate.setStationNumber(TestConstants.NEW_STATION_NUMBER);
            fireStationDTOToUpdate.setAddress(TestConstants.EXISTING_ADDRESS);
        }

        @Test
        @DisplayName("GIVEN a new station number for a given address " +
                "WHEN processing a PUT /firestation request for this fire station " +
                "THEN the returned value is the updated fire station")
        public void updateFireStationTest_WithSuccess() throws Exception {
            // GIVEN
            FireStationDTO updatedFireStationDTO = new FireStationDTO();
            updatedFireStationDTO.setFireStationId(fireStationDTOToUpdate.getStationNumber().longValue());
            updatedFireStationDTO.setStationNumber(fireStationDTOToUpdate.getStationNumber());
            updatedFireStationDTO.setAddress(fireStationDTOToUpdate.getAddress());

            when(fireStationServiceMock.updateFireStation(fireStationDTOToUpdate))
                    .thenReturn(Optional.of(updatedFireStationDTO));

            // THEN

            mockMvc.perform(put("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(fireStationServiceMock, Mockito.times(1)).updateFireStation(fireStationDTOToUpdate);
        }


        @Test
        @DisplayName("GIVEN a fire station to update with missing address " +
                "WHEN processing a put /firestation request for this fire station " +
                "THEN the returned code is 'bad request'")
        public void updateFireStationTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.updateFireStation(fireStationDTOToUpdate))
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS));

            // THEN
            mockMvc.perform(put("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS)));
            verify(fireStationServiceMock, Mockito.times(1)).updateFireStation(fireStationDTOToUpdate);
        }


        @Test
        @DisplayName("GIVEN a fire station to update with no existing address in repository " +
                "WHEN processing a PUT /firestation request for this fire station " +
                "THEN the returned code is 'bad request'")
        public void updateFireStationTest_WithNoExistingAddressInRepository() throws Exception {
            // GIVEN
            when(fireStationServiceMock.updateFireStation(fireStationDTOToUpdate))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_ADDRESS));

            // THEN
            mockMvc.perform(put("/firestation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fireStationDTOToUpdate)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_ADDRESS)));
            verify(fireStationServiceMock, Mockito.times(1)).updateFireStation(fireStationDTOToUpdate);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deleteFireStationByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deleteFireStationByAddress tests")
    class DeleteFireStationByAddressTest {

        @Test
        @DisplayName("GIVEN an existing fire station for a given address " +
                "WHEN processing a DELETE /firestation/address request for this address " +
                "THEN the return status is 'No content'")
        public void deleteFireStationByAddressTest_WithSuccess() throws Exception {
            // GIVEN
            when(fireStationServiceMock.deleteFireStationByAddress(TestConstants.EXISTING_ADDRESS))
                    .thenReturn(deletedFireStation);

            // THEN
            mockMvc.perform(delete("/firestation/address")
                    .param("address", TestConstants.EXISTING_ADDRESS))
                    .andExpect(status().isNoContent());

            verify(fireStationServiceMock, Mockito.times(1))
                    .deleteFireStationByAddress(TestConstants.EXISTING_ADDRESS);
        }


        @Test
        @DisplayName("GIVEN a missing address " +
                "WHEN processing a DELETE /firestation/address request for this address " +
                "THEN the returned code is 'bad request'")
        public void deleteFireStationByAddressTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.deleteFireStationByAddress(""))
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS));

            // THEN
            mockMvc.perform(delete("/firestation/address")
                    .param("address", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_ADDRESS)));

            verify(fireStationServiceMock, Mockito.times(1))
                    .deleteFireStationByAddress("");
        }


        @Test
        @DisplayName("GIVEN an address to delete with no existing address in repository " +
                "WHEN processing a DELETE /firestation/address request for this address " +
                "THEN the returned code is 'not found'")
        public void deleteFireStationByAddressTest_WithNoExistingAddressInRepository() throws Exception {
            // GIVEN
            when(fireStationServiceMock.deleteFireStationByAddress(TestConstants.ADDRESS_NOT_FOUND))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_ADDRESS));

            // THEN
            mockMvc.perform(delete("/firestation/address")
                    .param("address", TestConstants.ADDRESS_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_ADDRESS)));

            verify(fireStationServiceMock, Mockito.times(1))
                    .deleteFireStationByAddress(TestConstants.ADDRESS_NOT_FOUND);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deleteFireStationByStationNumber tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deleteFireStationByStationNumber tests")
    class DeleteFireStationByStationNumberTest {

        @Test
        @DisplayName("GIVEN an existing fire station for a given station number " +
                "WHEN processing a DELETE /firestation/station request for this station number " +
                "THEN the return status is 'No content'")
        public void deleteFireStationByStationNumberTest_WithSuccess() throws Exception {
            // GIVEN
            List<FireStation> deletedFireStations = new ArrayList<>();
            deletedFireStations.add(deletedFireStation);
            when(fireStationServiceMock.deleteFireStationByStationNumber(TestConstants.EXISTING_STATION_NUMBER))
                    .thenReturn(deletedFireStations);

            // THEN
            mockMvc.perform(delete("/firestation/station")
                    .param("stationNumber", TestConstants.EXISTING_STATION_NUMBER.toString()))
                    .andExpect(status().isNoContent());

            verify(fireStationServiceMock, Mockito.times(1))
                    .deleteFireStationByStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        }


        @Test
        @DisplayName("GIVEN a missing station number " +
                "WHEN processing a DELETE /firestation/station request for this station number " +
                "THEN the returned code is 'bad request'")
        public void deleteFireStationByStationNumberTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(fireStationServiceMock.deleteFireStationByStationNumber(null))
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_FIRE_STATION_STATION_NUMBER));

            // THEN
            mockMvc.perform(delete("/firestation/station")
                    .param("stationNumber", (String) null))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("GIVEN a station number to delete with no existing related fire station in repository " +
                "WHEN processing a DELETE /firestation/station request for this station number " +
                "THEN the returned code is 'not found'")
        public void deleteFireStationByStationNumberTest_WithNoExistingFireStationInRepository() throws Exception {
            // GIVEN
            when(fireStationServiceMock.deleteFireStationByStationNumber(TestConstants.STATION_NUMBER_NOT_FOUND))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_STATION_NUMBER));

            // THEN
            mockMvc.perform(delete("/firestation/station")
                    .param("stationNumber", TestConstants.STATION_NUMBER_NOT_FOUND.toString()))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_FIRE_STATION_FOUND_FOR_STATION_NUMBER)));

            verify(fireStationServiceMock, Mockito.times(1))
                    .deleteFireStationByStationNumber(TestConstants.STATION_NUMBER_NOT_FOUND);
        }

    }

}
