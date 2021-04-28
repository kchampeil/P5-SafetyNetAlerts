package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.PersonInfoService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonInfoController.class)
class PersonInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonInfoService personInfoServiceMock;

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
        void getPersonInfoByFirstNameAndLastNameTest_WithResults() throws Exception {
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

            when(personInfoServiceMock.getPersonInfoByFirstNameAndLastName("FirstNameTest", "LastNameTest"))
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
            when(personInfoServiceMock.getPersonInfoByFirstNameAndLastName("FirstNameTestNotKnown", "LastNameTestNotKnown"))
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
            when(personInfoServiceMock.getPersonInfoByFirstNameAndLastName(anyString(),anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/personInfo")
                    .param("firstName", "FirstNameTestNull")
                    .param("lastName", "LastNameTestNull"))
                    .andExpect(status().isBadRequest());
        }

    }
}