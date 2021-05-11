package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.service.IMedicalRecordService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMedicalRecordService medicalRecordServiceMock;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllMedicalRecordsTest tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Test
    @DisplayName("WHEN asking for the list of medical records (GET) THEN return status is ok")
    public void getAllMedicalRecordsTest() throws Exception {
        mockMvc.perform(get("/medicalrecords"))
                .andExpect(status().isOk());
        //TODO en tests d'intégration .andExpect(jsonPath("$[0].firstName", is("John"))); avec @SpringBootTest
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  addMedicalRecord tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("addMedicalRecord tests")
    class AddMedicalRecordTest {
        @Test
        @DisplayName("GIVEN a medical record not already present in repository " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned value is the added medical record")
        public void addMedicalRecordTest_WithSuccess() throws Exception {
            // GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setFirstName("MRCT_first_name");
            medicalRecordDTOToAdd.setLastName("MRCT_last_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("MRCT_medications_1");
            medications.add("MRCT_medications_2");
            medications.add("MRCT_medications_3");
            medicalRecordDTOToAdd.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("MRCT_allergies_1");
            allergies.add("MRCT_allergies_2");
            medicalRecordDTOToAdd.setAllergies(allergies);

            MedicalRecordDTO addedMedicalRecordDTO = new MedicalRecordDTO();
            addedMedicalRecordDTO.setMedicalRecordId(100L);
            addedMedicalRecordDTO.setFirstName(medicalRecordDTOToAdd.getFirstName());
            addedMedicalRecordDTO.setLastName(medicalRecordDTOToAdd.getLastName());
            addedMedicalRecordDTO.setBirthDate(medicalRecordDTOToAdd.getBirthDate());
            addedMedicalRecordDTO.setMedications(medicalRecordDTOToAdd.getMedications());
            addedMedicalRecordDTO.setAllergies(medicalRecordDTOToAdd.getAllergies());

            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTOToAdd))
                    .thenReturn(addedMedicalRecordDTO);

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTOToAdd)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(medicalRecordServiceMock, Mockito.times(1)).addMedicalRecord(medicalRecordDTOToAdd);
        }


        @Test
        @DisplayName("GIVEN a medical record with missing firstname " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void addMedicalRecordTest_WithMissingInformation() throws Exception {
            // GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setLastName("MRCT_last_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);
            List<String> medications = new ArrayList<>();
            medications.add("MRCT_medications_1");
            medications.add("MRCT_medications_2");
            medications.add("MRCT_medications_3");
            medicalRecordDTOToAdd.setMedications(medications);
            List<String> allergies = new ArrayList<>();
            allergies.add("MRCT_allergies_1");
            allergies.add("MRCT_allergies_2");
            medicalRecordDTOToAdd.setAllergies(allergies);

            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTOToAdd))
                    .thenThrow(new MissingInformationException("Firstname is missing"));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTOToAdd)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("GIVEN a medical record already present in repository " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void addMedicalRecordTest_AlreadyExisting() throws Exception {
            // GIVEN
            MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
            medicalRecordDTOToAdd.setFirstName("MRCT_Existing_first_name");
            medicalRecordDTOToAdd.setLastName("MRCT_Existing_last_name");
            medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);

            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTOToAdd))
                    .thenThrow(new AlreadyExistsException("Medical record already exists"));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTOToAdd)))
                    .andExpect(status().isBadRequest());
            verify(medicalRecordServiceMock, Mockito.times(1)).addMedicalRecord(medicalRecordDTOToAdd);
        }
    }

}
