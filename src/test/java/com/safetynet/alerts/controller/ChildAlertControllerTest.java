package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.HouseholdMemberDTO;
import com.safetynet.alerts.service.ChildAlertService;
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

@WebMvcTest(controllers = ChildAlertController.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildAlertService childAlertServiceMock;

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
        void getChildAlertByAddressTest_WithResults() throws Exception {
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

            when(childAlertServiceMock.getChildAlertByAddress("AddressTest"))
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
            when(childAlertServiceMock.getChildAlertByAddress("AddressTestNotKnown"))
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
            when(childAlertServiceMock.getChildAlertByAddress(anyString())).thenReturn(null);

            // THEN
            mockMvc.perform(get("/childAlert").param("address", "AddressTestNull"))
                    .andExpect(status().isBadRequest());
        }
    }
}