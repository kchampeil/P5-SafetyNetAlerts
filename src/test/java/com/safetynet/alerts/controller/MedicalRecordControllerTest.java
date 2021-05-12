package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.service.IMedicalRecordService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private MedicalRecordDTO medicalRecordDTO;

    @BeforeEach
    private void setUpPerTest() {
        medicalRecordDTO = new MedicalRecordDTO();
        medicalRecordDTO.setFirstName("MRCT_first_name");
        medicalRecordDTO.setLastName("MRCT_last_name");
        medicalRecordDTO.setBirthDate(TestConstants.ADULT_BIRTHDATE);
        List<String> medications = new ArrayList<>();
        medications.add("MRCT_medications_1");
        medications.add("MRCT_medications_2");
        medications.add("MRCT_medications_3");
        medicalRecordDTO.setMedications(medications);
        List<String> allergies = new ArrayList<>();
        allergies.add("MRCT_allergies_1");
        allergies.add("MRCT_allergies_2");
        medicalRecordDTO.setAllergies(allergies);
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllMedicalRecords tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllMedicalRecords tests")
    class GetAllMedicalRecordsTest {

        private List<MedicalRecordDTO> listOfMedicalRecordsDTO;

        @BeforeEach
        private void setUpPerTest() {
            listOfMedicalRecordsDTO = new ArrayList<>();
        }

        @Test
        @DisplayName("GIVEN data in DB WHEN asking for the list of medical records GET /medicalrecords " +
                "THEN return status is ok and a list of medical records is returned")
        public void getAllMedicalRecordsTest_WithData() throws Exception {
            //GIVEN
            listOfMedicalRecordsDTO.add(medicalRecordDTO);
            when(medicalRecordServiceMock.getAllMedicalRecords()).thenReturn(listOfMedicalRecordsDTO);

            //THEN
            mockMvc.perform(get("/medicalrecords"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
        }


        @Test
        @DisplayName("GIVEN no data in DB WHEN asking for the list of medical records GET /medicalrecords " +
                "THEN return status is 'not found' and an empty list is returned")
        public void getAllMedicalRecordsTest_WithoutData() throws Exception {
            //GIVEN
            when(medicalRecordServiceMock.getAllMedicalRecords()).thenReturn(listOfMedicalRecordsDTO);

            //THEN
            mockMvc.perform(get("/medicalrecords"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }
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
            MedicalRecordDTO addedMedicalRecordDTO = new MedicalRecordDTO();
            addedMedicalRecordDTO.setMedicalRecordId(100L);
            addedMedicalRecordDTO.setFirstName(medicalRecordDTO.getFirstName());
            addedMedicalRecordDTO.setLastName(medicalRecordDTO.getLastName());
            addedMedicalRecordDTO.setBirthDate(medicalRecordDTO.getBirthDate());
            addedMedicalRecordDTO.setMedications(medicalRecordDTO.getMedications());
            addedMedicalRecordDTO.setAllergies(medicalRecordDTO.getAllergies());

            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTO))
                    .thenReturn(addedMedicalRecordDTO);

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(medicalRecordServiceMock, Mockito.times(1)).addMedicalRecord(medicalRecordDTO);
        }


        @Test
        @DisplayName("GIVEN a medical record with missing firstname " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void addMedicalRecordTest_WithMissingInformation() throws Exception {
            // GIVEN
            medicalRecordDTO.setFirstName(null);

            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTO))
                    .thenThrow(new MissingInformationException("Firstname is missing"));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("GIVEN a medical record already present in repository " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void addMedicalRecordTest_AlreadyExisting() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTO))
                    .thenThrow(new AlreadyExistsException("Medical record already exists"));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest());
            verify(medicalRecordServiceMock, Mockito.times(1)).addMedicalRecord(medicalRecordDTO);
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  updateMedicalRecord tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("updateMedicalRecord tests")
    class UpdateMedicalRecordTest {

        @Test
        @DisplayName("GIVEN a medical record present in repository " +
                "WHEN processing a PUT /medicalRecord request for this medical record " +
                "THEN the returned value is the updated medical record")
        public void updateMedicalRecordTest_WithSuccess() throws Exception {
            // GIVEN
            MedicalRecordDTO updatedMedicalRecordDTO = new MedicalRecordDTO();
            updatedMedicalRecordDTO.setMedicalRecordId(100L);
            updatedMedicalRecordDTO.setFirstName(medicalRecordDTO.getFirstName());
            updatedMedicalRecordDTO.setLastName(medicalRecordDTO.getLastName());
            updatedMedicalRecordDTO.setBirthDate(medicalRecordDTO.getBirthDate());
            updatedMedicalRecordDTO.setMedications(medicalRecordDTO.getMedications());
            updatedMedicalRecordDTO.setAllergies(medicalRecordDTO.getAllergies());

            when(medicalRecordServiceMock.updateMedicalRecord(medicalRecordDTO))
                    .thenReturn(updatedMedicalRecordDTO);

            // THEN
            mockMvc.perform(put("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty());
            verify(medicalRecordServiceMock, Mockito.times(1)).updateMedicalRecord(medicalRecordDTO);
        }


        @Test
        @DisplayName("GIVEN a medical record with missing firstname " +
                "WHEN processing a PUT /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void updateMedicalRecordTest_WithMissingInformation() throws Exception {
            // GIVEN
            medicalRecordDTO.setFirstName(null);

            when(medicalRecordServiceMock.updateMedicalRecord(medicalRecordDTO))
                    .thenThrow(new MissingInformationException("Firstname is missing"));

            // THEN
            mockMvc.perform(put("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest());
            verify(medicalRecordServiceMock, Mockito.times(1)).updateMedicalRecord(medicalRecordDTO);
        }


        @Test
        @DisplayName("GIVEN a medical record not present in repository " +
                "WHEN processing a PUT /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void updateMedicalRecordTest_NotExisting() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock.updateMedicalRecord(medicalRecordDTO))
                    .thenThrow(new DoesNotExistException("Medical record does not exist"));

            // THEN
            mockMvc.perform(put("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest());
            verify(medicalRecordServiceMock, Mockito.times(1)).updateMedicalRecord(medicalRecordDTO);
        }
    }

}
