package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.constants.ExceptionConstants;
import com.safetynet.alerts.constants.TestConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private MedicalRecord deletedMedicalRecord;

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

        deletedMedicalRecord = new MedicalRecord();
        deletedMedicalRecord.setMedicalRecordId(100L);
        deletedMedicalRecord.setFirstName(TestConstants.EXISTING_FIRSTNAME);
        deletedMedicalRecord.setLastName(TestConstants.EXISTING_LASTNAME);
        deletedMedicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);
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
            verify(medicalRecordServiceMock, Mockito.times(1)).getAllMedicalRecords();
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
            verify(medicalRecordServiceMock, Mockito.times(1)).getAllMedicalRecords();
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
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING)));
        }


        @Test
        @DisplayName("GIVEN a medical record already present in repository " +
                "WHEN processing a POST /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void addMedicalRecordTest_AlreadyExisting() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock.addMedicalRecord(medicalRecordDTO))
                    .thenThrow(new AlreadyExistsException(ExceptionConstants.ALREADY_EXIST_MEDICAL_RECORD_FOUND_FOR_PERSON
                            + medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName()));

            // THEN
            mockMvc.perform(post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.ALREADY_EXIST_MEDICAL_RECORD_FOUND_FOR_PERSON
                                    + medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName())));
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
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING));

            // THEN
            mockMvc.perform(put("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING)));
            verify(medicalRecordServiceMock, Mockito.times(1)).updateMedicalRecord(medicalRecordDTO);
        }


        @Test
        @DisplayName("GIVEN a medical record not present in repository " +
                "WHEN processing a PUT /medicalRecord request for this medical record " +
                "THEN the returned code is 'bad request'")
        public void updateMedicalRecordTest_NotExisting() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock.updateMedicalRecord(medicalRecordDTO))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON
                            + medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName()));

            // THEN
            mockMvc.perform(put("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON
                                    + medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName())));
            verify(medicalRecordServiceMock, Mockito.times(1)).updateMedicalRecord(medicalRecordDTO);
        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  deleteMedicalRecordByFirstNameAndLastName tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("deleteMedicalRecordByFirstNameAndLastName tests")
    class DeleteMedicalRecordByFirstNameAndLastNameTest {

        @Test
        @DisplayName("GIVEN an existing medical record for a given firstname+lastname " +
                "WHEN processing a DELETE /medicalRecord request for this firstname+lastname " +
                "THEN the return status is 'No content'")
        public void deleteMedicalRecordByFirstNameAndLastNameTest_WithSuccess() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock
                    .deleteMedicalRecordByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME))
                    .thenReturn(deletedMedicalRecord);

            // THEN
            mockMvc.perform(delete("/medicalRecord")
                    .param("firstName", TestConstants.EXISTING_FIRSTNAME)
                    .param("lastName", TestConstants.EXISTING_LASTNAME))
                    .andExpect(status().isNoContent());

            verify(medicalRecordServiceMock, Mockito.times(1))
                    .deleteMedicalRecordByFirstNameAndLastName(TestConstants.EXISTING_FIRSTNAME, TestConstants.EXISTING_LASTNAME);
        }


        @Test
        @DisplayName("GIVEN a missing firstname+lastname " +
                "WHEN processing a DELETE /medicalRecord request for this firstname+lastname " +
                "THEN the returned code is 'bad request'")
        public void deleteMedicalRecordByFirstNameAndLastNameTest_WithMissingInformation() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock.deleteMedicalRecordByFirstNameAndLastName("", ""))
                    .thenThrow(new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_DELETING));

            // THEN
            mockMvc.perform(delete("/medicalRecord")
                    .param("firstName", "")
                    .param("lastName", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_DELETING)));

            verify(medicalRecordServiceMock, Mockito.times(1))
                    .deleteMedicalRecordByFirstNameAndLastName("", "");
        }


        @Test
        @DisplayName("GIVEN a medical record to delete with no existing firstname+lastname in repository " +
                "WHEN processing a DELETE /medicalRecord request for this firstname+lastname " +
                "THEN the returned code is 'not found'")
        public void deleteMedicalRecordByFirstNameAndLastNameTest_WithNoExistingMedicalRecordInRepository() throws Exception {
            // GIVEN
            when(medicalRecordServiceMock
                    .deleteMedicalRecordByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND))
                    .thenThrow(new DoesNotExistException(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON
                            + TestConstants.FIRSTNAME_NOT_FOUND + " " + TestConstants.LASTNAME_NOT_FOUND));

            // THEN
            mockMvc.perform(delete("/medicalRecord")
                    .param("firstName", TestConstants.FIRSTNAME_NOT_FOUND)
                    .param("lastName", TestConstants.LASTNAME_NOT_FOUND))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                            .contains(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON
                                    + TestConstants.FIRSTNAME_NOT_FOUND + " " + TestConstants.LASTNAME_NOT_FOUND)));

            verify(medicalRecordServiceMock, Mockito.times(1))
                    .deleteMedicalRecordByFirstNameAndLastName(TestConstants.FIRSTNAME_NOT_FOUND, TestConstants.LASTNAME_NOT_FOUND);
        }
    }

}
