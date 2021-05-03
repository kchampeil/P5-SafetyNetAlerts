package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.HouseholdMemberDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.PersonService;
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
import static org.mockito.ArgumentMatchers.anyString;
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
    public void getAllPersonsTest() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk());
        //TODO en tests d'intégration .andExpect(jsonPath("$[0].firstName", is("John"))); avec @SpringBootTest ?
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
        public void getAllEmailsByCityTest_WithResultsForCity() throws Exception {
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
        public void getAllEmailsByCityTest_WithNoResultsForCity() throws Exception {
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
        public void getAllEmailsByCityTest_WithNoCityAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getAllEmailsByCity(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", "CityTestNull"))
                    .andExpect(status().isBadRequest());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPersonInfoByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPersonInfoByFirstNameAndLastName tests")
    class GetPersonInfoByFirstNameAndLastNameTest {
        @Test
        @DisplayName("GIVEN persons in repository for the requested firstname+lastname " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN a list of person information is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithResults() throws Exception {
            // GIVEN
            List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();
            PersonInfoDTO personInfoDTO = new PersonInfoDTO();
            personInfoDTO.setLastName("PICT_LastName");
            personInfoDTO.setEmail("PICT_Email");
            personInfoDTO.setAge(21);
            personInfoDTO.setAddress("PICT_Address");
            personInfoDTO.setMedications(new ArrayList<>());
            personInfoDTO.setAllergies(new ArrayList<>());
            listOfPersonInfoDTO.add(personInfoDTO);

            when(personServiceMock.getPersonInfoByFirstNameAndLastName("FirstNameTest", "LastNameTest"))
                    .thenReturn(listOfPersonInfoDTO);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "FirstNameTest")
                    .param("lastName", "LastNameTest"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN firstname and lastname not found in repository " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN the returned list is null")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoResults() throws Exception{
            // GIVEN
            List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();
            when(personServiceMock.getPersonInfoByFirstNameAndLastName("FirstNameTestNotKnown", "LastNameTestNotKnown"))
                    .thenReturn(listOfPersonInfoDTO);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "FirstNameTestNotKnown")
                    .param("lastName", "LastNameTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }


        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoNameAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getPersonInfoByFirstNameAndLastName(anyString(),anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "FirstNameTestNull")
                    .param("lastName", "LastNameTestNull"))
                    .andExpect(status().isBadRequest());
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getChildAlertByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getChildAlertByAddress tests")
    class GetChildAlertByAddressTest {
        @Test
        @DisplayName("GIVEN children in repository for the requested address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN a list of child alert is returned")
        public void getChildAlertByAddressTest_WithResults() throws Exception {
            // GIVEN
            List<ChildAlertDTO> listOfChildAlertDTO = new ArrayList<>();
            ChildAlertDTO childAlertDTO = new ChildAlertDTO();
            childAlertDTO.setFirstName("CACT_FirstName");
            childAlertDTO.setLastName("CACT_LastName");
            childAlertDTO.setAge(21);
            HouseholdMemberDTO parent = new HouseholdMemberDTO();
            parent.setFirstName("CACT_FirstName_Parent");
            parent.setLastName(childAlertDTO.getLastName());
            parent.setEmail("CACT_email_Parent");
            parent.setPhone("CACT_phone_Parent");
            List<HouseholdMemberDTO> householdMembers = new ArrayList<>();
            householdMembers.add(parent);
            childAlertDTO.setListOfOtherHouseholdMembers(householdMembers);
            listOfChildAlertDTO.add(childAlertDTO);

            when(personServiceMock.getChildAlertByAddress("AddressTest"))
                    .thenReturn(listOfChildAlertDTO);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", "AddressTest"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no child found in repository for the address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN the returned list is null")
        public void getChildAlertByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            List<ChildAlertDTO> listOfChildAlertDTO = new ArrayList<>();
            when(personServiceMock.getChildAlertByAddress("AddressTestNotKnown"))
                    .thenReturn(listOfChildAlertDTO);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", "AddressTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }


        @Test
        @DisplayName("GIVEN null address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getChildAlertByAddressTest_WithNoNameAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getChildAlertByAddress(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", "AddressTestNull"))
                    .andExpect(status().isBadRequest());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPhoneAlertByFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPhoneAlertByFireStation tests")
    class GetPhoneAlertByFireStationTest {
        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by the requested fire station " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN a list of phone number is returned")
        public void getPhoneAlertByFireStationTest_WithResults() throws Exception {
            // GIVEN
            List<String > listOfPhoneNumbers = new ArrayList<>();
            listOfPhoneNumbers.add("33-1 23 45 67 89");
            listOfPhoneNumbers.add("33-1 98 76 54 32");
            listOfPhoneNumbers.add("33 1 11 11 11 11");

            when(personServiceMock.getPhoneAlertByFireStation(3))
                    .thenReturn(listOfPhoneNumbers);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", "3"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no person found for requested fire station number in repository " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN the returned list is null")
        public void getPhoneAlertByFireStationTest_WithNoResults() throws Exception{
            // GIVEN
            List<String> listOfPhoneNumbers = new ArrayList<>();
            when(personServiceMock.getPhoneAlertByFireStation(2))
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
            when(personServiceMock.getPhoneAlertByFireStation(anyInt())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", String.valueOf(999)))
                    .andExpect(status().isBadRequest());
        }
    }

}
