package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.ExceptionConstants;
import com.safetynet.alerts.testconstants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.Person;
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
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    private Person deletedPerson;

    @BeforeEach
    private void setUpPerTest() {
        deletedPerson = new Person();
        deletedPerson.setPersonId(100L);
        deletedPerson.setFirstName(TestConstants.EXISTING_FIRSTNAME);
        deletedPerson.setLastName(TestConstants.EXISTING_LASTNAME);
    }


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
            personDTO.setFirstName(TestConstants.EXISTING_FIRSTNAME);
            personDTO.setLastName(TestConstants.EXISTING_LASTNAME);
            personDTO.setEmail("PICT_Email");
            personDTO.setAddress(TestConstants.EXISTING_ADDRESS);
            listOfPersonsDTO.add(personDTO);
            when(personServiceMock.getAllPersons()).thenReturn(listOfPersonsDTO);

            //THEN
            mockMvc.perform(get("/persons"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(personServiceMock, Mockito.times(1)).getAllPersons();
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
            verify(personServiceMock, Mockito.times(1)).getAllPersons();
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
            when(personServiceMock.getAllEmailsByCity(TestConstants.EXISTING_CITY)).thenReturn(listOfEmails);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", TestConstants.EXISTING_CITY))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("[\"email1@test.com\",\"email2@test.com\",\"email3@test.com\"]"));
            verify(personServiceMock, Mockito.times(1)).getAllEmailsByCity(TestConstants.EXISTING_CITY);
        }

        @Test
        @DisplayName("GIVEN a city name not known in the repository" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is 'not found' but the result is empty")
        public void getAllEmailsByCityTest_WithNoResultsForCity() throws Exception {
            // GIVEN
            when(personServiceMock.getAllEmailsByCity(TestConstants.CITY_NOT_FOUND)).thenReturn(listOfEmails);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", TestConstants.CITY_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
            verify(personServiceMock, Mockito.times(1)).getAllEmailsByCity(TestConstants.CITY_NOT_FOUND);
        }

        @Test
        @DisplayName("GIVEN no city name as input" +
                " WHEN asking for the list of emails of all citizens" +
                " THEN return status is 'bad request'")
        public void getAllEmailsByCityTest_WithNoCityAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getAllEmailsByCity("")).thenReturn(null);

            // THEN
            mockMvc.perform(get("/communityEmail").param("city", ""))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1)).getAllEmailsByCity("");
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
            personInfoDTO.setLastName(TestConstants.EXISTING_LASTNAME);
            personInfoDTO.setEmail("PICT_Email");
            personInfoDTO.setAge(21);
            personInfoDTO.setAddress(TestConstants.EXISTING_ADDRESS);
            personInfoDTO.setMedications(new ArrayList<>());
            personInfoDTO.setAllergies(new ArrayList<>());
            listOfPersonInfoDTO.add(personInfoDTO);

            when(personServiceMock
                    .getPersonInfoByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME))
                    .thenReturn(listOfPersonInfoDTO);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", TestConstants.EXISTING_FIRSTNAME)
                    .param("lastName", TestConstants.EXISTING_LASTNAME))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(personServiceMock, Mockito.times(1))
                    .getPersonInfoByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
        }


        @Test
        @DisplayName("GIVEN firstname and lastname 'not found' in repository " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock
                    .getPersonInfoByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .thenReturn(listOfPersonInfoDTO);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", TestConstants.FIRSTNAME_NOT_FOUND)
                    .param("lastName", TestConstants.LASTNAME_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
            verify(personServiceMock, Mockito.times(1))
                    .getPersonInfoByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
        }


        @Test
        @DisplayName("GIVEN null firstname and lastname " +
                "WHEN processing a GET /personInfo request on firstname+lastname " +
                "THEN return status is 'bad request'")
        public void getPersonInfoByFirstNameAndLastNameTest_WithNoNameAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getPersonInfoByFirstNameAndLastName("", "")).thenReturn(null);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "")
                    .param("lastName", ""))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1))
                    .getPersonInfoByFirstNameAndLastName("", "");
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
            childAlertDTO.setFirstName(TestConstants.EXISTING_FIRSTNAME);
            childAlertDTO.setLastName(TestConstants.EXISTING_LASTNAME);
            childAlertDTO.setAge(TestConstants.CHILD_AGE);
            HouseholdMemberDTO parent = new HouseholdMemberDTO();
            parent.setFirstName(TestConstants.EXISTING_FIRSTNAME + "_Parent");
            parent.setLastName(childAlertDTO.getLastName());
            parent.setEmail("CACT_email_Parent");
            parent.setPhone("CACT_phone_Parent");
            List<HouseholdMemberDTO> householdMembers = new ArrayList<>();
            householdMembers.add(parent);
            childAlertDTO.setListOfOtherHouseholdMembers(householdMembers);
            listOfChildAlertDTO.add(childAlertDTO);

            when(personServiceMock.getChildAlertByAddress(TestConstants.EXISTING_ADDRESS))
                    .thenReturn(listOfChildAlertDTO);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", TestConstants.EXISTING_ADDRESS))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(personServiceMock, Mockito.times(1))
                    .getChildAlertByAddress(TestConstants.EXISTING_ADDRESS);
        }


        @Test
        @DisplayName("GIVEN no child found in repository for the address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getChildAlertByAddressTest_WithNoResults() throws Exception {
            // GIVEN
            when(personServiceMock.getChildAlertByAddress(TestConstants.ADDRESS_NOT_FOUND))
                    .thenReturn(listOfChildAlertDTO);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", TestConstants.ADDRESS_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
            verify(personServiceMock, Mockito.times(1))
                    .getChildAlertByAddress(TestConstants.ADDRESS_NOT_FOUND);
        }


        @Test
        @DisplayName("GIVEN null address " +
                "WHEN processing a GET /childAlert request on address " +
                "THEN return status is 'bad request'")
        public void getChildAlertByAddressTest_WithNoNameAsInput() throws Exception {
            // GIVEN
            when(personServiceMock.getChildAlertByAddress("")).thenReturn(null);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", ""))
                    .andExpect(status().isBadRequest());
            verify(personServiceMock, Mockito.times(1))
                    .getChildAlertByAddress("");
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
            verify(personServiceMock, Mockito.times(1))
                    .getPhoneAlertByFireStation(3);
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
            verify(personServiceMock, Mockito.times(1))
                    .getPhoneAlertByFireStation(2);
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
            verify(personServiceMock, Mockito.times(1))
                    .getPhoneAlertByFireStation(anyInt());
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
            personCoveredContactsDTO1.setFirstName(TestConstants.EXISTING_FIRSTNAME + "_1");
            personCoveredContactsDTO1.setLastName(TestConstants.EXISTING_LASTNAME + "_1");
            personCoveredContactsDTO1.setAddress(TestConstants.EXISTING_ADDRESS + "_1");
            personCoveredContactsDTO1.setPhone("33 1 23 45 67 89");
            listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO1);

            PersonCoveredContactsDTO personCoveredContactsDTO2 = new PersonCoveredContactsDTO();
            personCoveredContactsDTO2.setFirstName(TestConstants.EXISTING_FIRSTNAME + "_2");
            personCoveredContactsDTO2.setLastName(TestConstants.EXISTING_LASTNAME + "_2");
            personCoveredContactsDTO2.setAddress(TestConstants.EXISTING_ADDRESS + "_2");
            personCoveredContactsDTO2.setPhone("33 1 98 76 54 32");
            listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO2);

            PersonCoveredContactsDTO personCoveredContactsDTO3 = new PersonCoveredContactsDTO();
            personCoveredContactsDTO3.setFirstName(TestConstants.EXISTING_FIRSTNAME + "_3");
            personCoveredContactsDTO3.setLastName(TestConstants.EXISTING_LASTNAME + "_3");
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
            verify(personServiceMock, Mockito.times(1))
                    .getFireStationCoverageByStationNumber(3);
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
            verify(personServiceMock, Mockito.times(1))
                    .getFireStationCoverageByStationNumber(2);
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
            verify(personServiceMock, Mockito.times(1))
                    .getFireStationCoverageByStationNumber(anyInt());
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
            personDTOToAdd.setFirstName(TestConstants.NEW_FIRSTNAME);
            personDTOToAdd.setLastName(TestConstants.NEW_LASTNAME);
            personDTOToAdd.setAddress(TestConstants.NEW_ADDRESS);
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
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING));

            // THEN
            mockMvc.perform(post("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToAdd)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING)));
            verify(personServiceMock, Mockito.times(1)).addPerson(personDTOToAdd);
        }


        @Test
        @DisplayName("GIVEN a person already present in repository " +
                "WHEN processing a POST /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void addPersonTest_AlreadyExisting() throws Exception {
            // GIVEN
            when(personServiceMock.addPerson(personDTOToAdd))
                    .thenThrow(new AlreadyExistsException(ExceptionConstants.ALREADY_EXIST_PERSON_FOR_FIRSTNAME_AND_LASTNAME
                            + personDTOToAdd.getFirstName() + " " + personDTOToAdd.getLastName()));

            // THEN
            mockMvc.perform(post("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToAdd)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.ALREADY_EXIST_PERSON_FOR_FIRSTNAME_AND_LASTNAME
                                    + personDTOToAdd.getFirstName() + " " + personDTOToAdd.getLastName())));
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
            personDTOToUpdate.setFirstName(TestConstants.EXISTING_FIRSTNAME);
            personDTOToUpdate.setLastName(TestConstants.EXISTING_LASTNAME);
            personDTOToUpdate.setAddress(TestConstants.EXISTING_ADDRESS);
            personDTOToUpdate.setEmail("PCT_email@safety.com");
            personDTOToUpdate.setPhone("PCT_phone");
            personDTOToUpdate.setCity(TestConstants.EXISTING_CITY);
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
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING));

            // THEN
            mockMvc.perform(put("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING)));
            verify(personServiceMock, Mockito.times(1)).updatePerson(personDTOToUpdate);
        }


        @Test
        @DisplayName("GIVEN a person not present in repository " +
                "WHEN processing a PUT /person request for this person " +
                "THEN the returned code is 'bad request'")
        public void updatePersonTest_NotExisting() throws Exception {
            // GIVEN
            when(personServiceMock.updatePerson(personDTOToUpdate))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                            + personDTOToUpdate.getFirstName() + " " + personDTOToUpdate.getLastName()));

            // THEN
            mockMvc.perform(put("/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                                    + personDTOToUpdate.getFirstName() + " " + personDTOToUpdate.getLastName())));
            verify(personServiceMock, Mockito.times(1)).updatePerson(personDTOToUpdate);
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deletePersonByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deletePersonByFirstNameAndLastName tests")
    class DeletePersonByFirstNameAndLastNameTest {

        @Test
        @DisplayName("GIVEN an existing person for a given firstname+lastname " +
                "WHEN processing a DELETE /person request for this firstname+lastname " +
                "THEN the return status is 'No content'")
        public void deletePersonByFirstNameAndLastNameTest_WithSuccess() throws Exception {
            // GIVEN
            when(personServiceMock
                    .deletePersonByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME))
                    .thenReturn(deletedPerson);

            // THEN
            mockMvc.perform(delete("/person")
                    .param("firstName", TestConstants.EXISTING_FIRSTNAME)
                    .param("lastName", TestConstants.EXISTING_LASTNAME))
                    .andExpect(status().isNoContent());

            verify(personServiceMock, Mockito.times(1))
                    .deletePersonByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
        }


        @Test
        @DisplayName("GIVEN a missing firstname+lastname " +
                "WHEN processing a DELETE /person request for this firstname+lastname " +
                "THEN the returned code is 'bad request'")
        public void deletePersonByFirstNameAndLastNameTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(personServiceMock.deletePersonByFirstNameAndLastName("", ""))
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_DELETING));

            // THEN
            mockMvc.perform(delete("/person")
                    .param("firstName", "")
                    .param("lastName", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_DELETING)));

            verify(personServiceMock, Mockito.times(1))
                    .deletePersonByFirstNameAndLastName("", "");
        }


        @Test
        @DisplayName("GIVEN a person to delete with no existing firstname+lastname in repository " +
                "WHEN processing a DELETE /person request for this firstname+lastname " +
                "THEN the returned code is 'not found'")
        public void deletePersonByFirstNameAndLastNameTest_WithNoExistingPersonInRepository() throws Exception {
            // GIVEN
            when(personServiceMock
                    .deletePersonByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                            + TestConstants.FIRSTNAME_NOT_FOUND + " " + TestConstants.LASTNAME_NOT_FOUND));

            // THEN
            mockMvc.perform(delete("/person")
                    .param("firstName", TestConstants.FIRSTNAME_NOT_FOUND)
                    .param("lastName", TestConstants.LASTNAME_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                                    + TestConstants.FIRSTNAME_NOT_FOUND + " " + TestConstants.LASTNAME_NOT_FOUND)));

            verify(personServiceMock, Mockito.times(1))
                    .deletePersonByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
        }
    }

}
