package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.HouseholdMemberDTO;
import com.safetynet.alerts.model.dto.PersonCoveredContactsDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.IPersonService;
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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPersonService personServiceMock;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllMedicalRecords tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllPersons tests")
    class GetAllPersonsTest {

        private List<PersonDTO> listOfPersonsDTO;

        @BeforeEach
        private void setUpPerTest() {
            listOfPersonsDTO = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN data in DB WHEN asking for the list of persons GET /persons " +
                "THEN return status is ok and a list of persons is returned")
        public void getAllPersonsTest_WithData() throws Exception {
            //GIVEN
            PersonDTO personDTO = new PersonDTO();
            personDTO.setPersonId(100L);
            personDTO.setFirstName("PICT_FirstName");
            personDTO.setLastName("PICT_LastName");
            personDTO.setEmail("PICT_Email");
            personDTO.setAddress("PICT_Address");
            listOfPersonsDTO.add(personDTO);
            when(personServiceMock.getAllPersons()).thenReturn(listOfPersonsDTO);

            //THEN
            mockMvc.perform(get("/persons"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("GIVEN no data in DB WHEN asking for the list of fire stations GET /persons " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getAllPersonsTest_WithoutData() throws Exception {
            //GIVEN
            when(personServiceMock.getAllPersons()).thenReturn(listOfPersonsDTO);

            //THEN
            mockMvc.perform(get("/persons"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }


    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllEmailsByCity tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllEmailsByCity tests")
    class GetAllEmailsByCityTest {

        private List<String> listOfEmails;

        @BeforeEach
        private void setUpPerTest() {
            listOfEmails = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN a city name known in the repository" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is ok and the result is filled with emails")
        public void getAllEmailsByCityTest_WithResultsForCity() throws Exception {
            // GIVEN
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
                " THEN return status is 'not found' but the result is empty")
        public void getAllEmailsByCityTest_WithNoResultsForCity() throws Exception {
            // GIVEN
            when(personServiceMock.getAllEmailsByCity("CityTestNotKnown")).thenReturn(listOfEmails);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", "CityTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("GIVEN no city name as input" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is 'bad request'")
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
        private List<PersonInfoDTO> listOfPersonInfoDTO;

        @BeforeEach
        private void setUpPerTest() {
            listOfPersonInfoDTO = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN persons in repository for the requested firstname+lastname " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN a list of person information is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithResults() throws Exception {
            // GIVEN
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
        @DisplayName("GIVEN firstname and lastname 'not found' in repository " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock.getPersonInfoByFirstNameAndLastName("FirstNameTestNotKnown", "LastNameTestNotKnown"))
                    .thenReturn(listOfPersonInfoDTO);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "FirstNameTestNotKnown")
                    .param("lastName", "LastNameTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }


        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN return status is 'bad request'")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoNameAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getPersonInfoByFirstNameAndLastName(anyString(), anyString())).thenReturn(null);

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
        private List<ChildAlertDTO> listOfChildAlertDTO;

        @BeforeEach
        private void setUpPerTest() {
            listOfChildAlertDTO = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN children in repository for the requested address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN a list of child alert is returned")
        public void getChildAlertByAddressTest_WithResults() throws Exception {
            // GIVEN
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
                "THEN return status is 'not found' and an empty list is returned")
        public void getChildAlertByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock.getChildAlertByAddress("AddressTestNotKnown"))
                    .thenReturn(listOfChildAlertDTO);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", "AddressTestNotKnown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }


        @Test
        @DisplayName("GIVEN null address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN return status is 'bad request'")
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
        private List<String> listOfPhoneNumbers;

        @BeforeEach
        private void setUpPerTest() {
            listOfPhoneNumbers = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by the requested fire station " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN a list of phone number is returned")
        public void getPhoneAlertByFireStationTest_WithResults() throws Exception {
            // GIVEN
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
                "THEN return status is 'not found' and an empty list is returned")
        public void getPhoneAlertByFireStationTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock.getPhoneAlertByFireStation(2))
                    .thenReturn(listOfPhoneNumbers);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", "2"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }


        @Test
        @DisplayName("GIVEN null fire station number " +
                "WHEN processing a GET /phoneAlert request on fire station number " +
                "THEN return status is 'bad request'")
        public void getPhoneAlertByFireStationTest_WithNoStationNumberAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getPhoneAlertByFireStation(anyInt())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/phoneAlert")
                    .param("firestation", String.valueOf(999)))
                    .andExpect(status().isBadRequest());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getFireStationCoverageByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getFireStationCoverageByAddress tests")
    class GetFireStationCoverageByAddressTest {
        private FireStationCoverageDTO fireStationCoverageDTO;

        @BeforeEach
        private void setUpPerTest() {
            fireStationCoverageDTO = new FireStationCoverageDTO();
        }

        @Test
        @DisplayName("GIVEN persons in repository living at one address covered by the requested fire station " +
                "WHEN processing a GET /firestation request on fire station number " +
                "THEN a list of person info + nb of adults + nb of children is returned")
        public void getFireStationCoverageByAddressTest_WithResults() throws Exception {
            // GIVEN
            List<PersonCoveredContactsDTO> listOfPersonCoveredContactsDTO = new ArrayList<>();

            PersonCoveredContactsDTO personCoveredContactsDTO1 = new PersonCoveredContactsDTO();
            personCoveredContactsDTO1.setFirstName("PCT_first_name_1");
            personCoveredContactsDTO1.setLastName("PCT_last_name_1");
            personCoveredContactsDTO1.setAddress("PCT_Address_1");
            personCoveredContactsDTO1.setPhone("33 1 23 45 67 89");
            listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO1);

            PersonCoveredContactsDTO personCoveredContactsDTO2 = new PersonCoveredContactsDTO();
            personCoveredContactsDTO2.setFirstName("PCT_first_name_2");
            personCoveredContactsDTO2.setLastName("PCT_last_name_2");
            personCoveredContactsDTO2.setAddress("PCT_Address_2");
            personCoveredContactsDTO2.setPhone("33 1 98 76 54 32");
            listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO2);

            PersonCoveredContactsDTO personCoveredContactsDTO3 = new PersonCoveredContactsDTO();
            personCoveredContactsDTO3.setFirstName("PCT_first_name_3");
            personCoveredContactsDTO3.setLastName("PCT_last_name_3");
            personCoveredContactsDTO3.setAddress(personCoveredContactsDTO2.getAddress());
            personCoveredContactsDTO3.setPhone("33 1 99 99 99 99");
            listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO3);

            fireStationCoverageDTO.setPersonCoveredContactsDTOList(listOfPersonCoveredContactsDTO);
            fireStationCoverageDTO.setNumberOfChildren(1);
            fireStationCoverageDTO.setNumberOfAdults(2);

            when(personServiceMock.getFireStationCoverageByStationNumber(3))
                    .thenReturn(fireStationCoverageDTO);

            // THEN
            mockMvc.perform(get("/firestation")
                    .param("stationNumber", "3"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no person found for requested fire station number in repository " +
                "WHEN processing a GET /firestation request on fire station number " +
                "THEN return status is 'not found', the returned list is null and number of adults & children equals 0")
        public void getFireStationCoverageByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock.getFireStationCoverageByStationNumber(2))
                    .thenReturn(fireStationCoverageDTO);

            // THEN
            mockMvc.perform(get("/firestation")
                    .param("stationNumber", "2"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.numberOfChildren", is(0)))
                    .andExpect(jsonPath("$.numberOfAdults", is(0)))
                    .andExpect(jsonPath("$.personCoveredContactsDTOList", is(nullValue())));
        }


        @Test
        @DisplayName("GIVEN null fire station number " +
                "WHEN processing a GET /firestation request on fire station number " +
                "THEN return status is 'bad request'")
        public void getFireStationCoverageByAddressTest_WithNoStationNumberAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getFireStationCoverageByStationNumber(anyInt())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/firestation")
                    .param("stationNumber", String.valueOf(999)))
                    .andExpect(status().isBadRequest());
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addPerson tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addPerson tests")
    class AddPersonTest {
        private PersonDTO personDTOToAdd;

        @BeforeEach
        private void setUpPerTest() {
            personDTOToAdd = new PersonDTO();
            personDTOToAdd.setFirstName("PCT_first_name");
            personDTOToAdd.setLastName("PCT_last_name");
            personDTOToAdd.setAddress("PCT_address");
            personDTOToAdd.setEmail("PCT_email@safety.com");
            personDTOToAdd.setPhone("PCT_phone");
            personDTOToAdd.setCity("PCT_city");
            personDTOToAdd.setZip("PCT_zip");
        }

        @Test
        @DisplayName("GIVEN a person not already present in repository " +
                "WHEN processing a POST /person request for this person " +
                "THEN the returned value is the added person")
        public void addPersonTest_WithSuccess() throws Exception {
            // GIVEN
            PersonDTO addedPersonDTO = new PersonDTO();
            addedPersonDTO.setPersonId(100L);
            addedPersonDTO.setFirstName(personDTOToAdd.getFirstName());
            addedPersonDTO.setLastName(personDTOToAdd.getLastName());
            addedPersonDTO.setAddress(personDTOToAdd.getAddress());
            addedPersonDTO.setEmail(personDTOToAdd.getEmail());
            addedPersonDTO.setPhone(personDTOToAdd.getPhone());
            addedPersonDTO.setCity(personDTOToAdd.getCity());
            addedPersonDTO.setZip(personDTOToAdd.getZip());

            when(personServiceMock.addPerson(personDTOToAdd))
                    .thenReturn(addedPersonDTO);

            // THEN
            mockMvc.perform(post("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToAdd)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(personServiceMock, Mockito.times(1)).addPerson(personDTOToAdd);
        }


        @Test
        @DisplayName("GIVEN a person with missing firstname " +
                "WHEN processing a POST /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void addPersonTest_WithMissingInformation() throws Exception {
            // GIVEN
            personDTOToAdd.setFirstName(null);
            when(personServiceMock.addPerson(personDTOToAdd))
                    .thenThrow(new MissingInformationException("Firstname is missing"));

            // THEN
            mockMvc.perform(post("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToAdd)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("GIVEN a person already present in repository " +
                "WHEN processing a POST /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void addPersonTest_AlreadyExisting() throws Exception {
            // GIVEN
            when(personServiceMock.addPerson(personDTOToAdd))
                    .thenThrow(new AlreadyExistsException("Person already exists"));

            // THEN
            mockMvc.perform(post("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToAdd)))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1)).addPerson(personDTOToAdd);
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updatePerson tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updatePerson tests")
    class UpdatePersonTest {
        private PersonDTO personDTOToUpdate;

        @BeforeEach
        private void setUpPerTest() {
            personDTOToUpdate = new PersonDTO();
            personDTOToUpdate.setFirstName("PCT_first_name");
            personDTOToUpdate.setLastName("PCT_last_name");
            personDTOToUpdate.setAddress("PCT_address");
            personDTOToUpdate.setEmail("PCT_email@safety.com");
            personDTOToUpdate.setPhone("PCT_phone");
            personDTOToUpdate.setCity("PCT_city");
            personDTOToUpdate.setZip("PCT_zip");
        }

        @Test
        @DisplayName("GIVEN a person present in repository " +
                "WHEN processing a PUT /person request for this person " +
                "THEN the returned value is the updated person")
        public void updatePersonTest_WithSuccess() throws Exception {
            // GIVEN
            PersonDTO updatedPersonDTO = new PersonDTO();
            updatedPersonDTO.setPersonId(100L);
            updatedPersonDTO.setFirstName(personDTOToUpdate.getFirstName());
            updatedPersonDTO.setLastName(personDTOToUpdate.getLastName());
            updatedPersonDTO.setAddress(personDTOToUpdate.getAddress());
            updatedPersonDTO.setZip(personDTOToUpdate.getZip());
            updatedPersonDTO.setCity(personDTOToUpdate.getCity());
            updatedPersonDTO.setEmail(personDTOToUpdate.getEmail());
            updatedPersonDTO.setPhone(personDTOToUpdate.getPhone());

            when(personServiceMock.updatePerson(personDTOToUpdate))
                    .thenReturn(updatedPersonDTO);

            // THEN
            mockMvc.perform(put("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(personServiceMock, Mockito.times(1)).updatePerson(personDTOToUpdate);
        }


        @Test
        @DisplayName("GIVEN a person with missing firstname " +
                "WHEN processing a PUT /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void updatePersonTest_WithMissingInformation() throws Exception {
            // GIVEN
            personDTOToUpdate.setFirstName(null);

            when(personServiceMock.updatePerson(personDTOToUpdate))
                    .thenThrow(new MissingInformationException("Firstname is missing"));

            // THEN
            mockMvc.perform(put("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1)).updatePerson(personDTOToUpdate);
        }


        @Test
        @DisplayName("GIVEN a person not present in repository " +
                "WHEN processing a PUT /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void updatePersonTest_NotExisting() throws Exception {
            // GIVEN
            when(personServiceMock.updatePerson(personDTOToUpdate))
                    .thenThrow(new DoesNotExistException("Person does not exist"));

            // THEN
            mockMvc.perform(put("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1)).updatePerson(personDTOToUpdate);
        }
    }

}
