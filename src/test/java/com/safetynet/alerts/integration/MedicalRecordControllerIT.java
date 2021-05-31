package com.safetynet.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.integration.ITConstants.ITConstants;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.testconstants.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MedicalRecordRepository medicalRecordRepository;

    @Autowired
    PersonRepository personRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("WHEN asking for the list of medical records GET /medicalrecords " +
            "THEN return status is OK and the list of all medical records is returned")
    public void getAllMedicalRecordsTest_WithSuccess() throws Exception {
        mockMvc.perform(get("/medicalrecords"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(ITConstants.NB_OF_MEDICAL_RECORD_RECORDS_ALL)));
    }


    @Test
    @DisplayName("WHEN processing a POST/medicalRecord request for a new medical record for an existing person" +
            "THEN return status is CREATED, the returned value is the new medical record " +
            "and the medical record has been added in DB")
    public void addMedicalRecordTest_WithSuccess() throws Exception {
        //init the database with one person
        Person person = new Person();
        person.setFirstName(TestConstants.NEW_FIRSTNAME);
        person.setLastName(TestConstants.NEW_LASTNAME);
        person.setAddress(TestConstants.EXISTING_ADDRESS);

        person = personRepository.save(person);

        //test
        MedicalRecordDTO medicalRecordDTOToAdd = new MedicalRecordDTO();
        medicalRecordDTOToAdd.setFirstName(person.getFirstName());
        medicalRecordDTOToAdd.setLastName(person.getLastName());
        medicalRecordDTOToAdd.setBirthDate(TestConstants.ADULT_BIRTHDATE);
        List<String> medications = new ArrayList<>();
        medications.add("added_medications_1");
        medications.add("added_medications_2");
        medications.add("added_medications_3");
        medicalRecordDTOToAdd.setMedications(medications);
        List<String> allergies = new ArrayList<>();
        allergies.add("added_allergies_1");
        allergies.add("added_allergies_2");
        medicalRecordDTOToAdd.setAllergies(allergies);

        mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecordDTOToAdd)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.birthdate",
                        is(medicalRecordDTOToAdd.getBirthDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.medications", is(medications)))
                .andExpect(jsonPath("$.allergies", is(allergies)));

        Optional<MedicalRecord> addedMedicalRecord = Optional.ofNullable(medicalRecordRepository
                .findByFirstNameAndLastName(medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName()));
        assertThat(addedMedicalRecord).isNotEmpty();
        assertEquals(medicalRecordDTOToAdd.getBirthDate(), addedMedicalRecord.get().getBirthDate());

        //clean the database by deleting the added medical record
        person.setMedicalRecord(null);
        personRepository.save(person);
        medicalRecordRepository.deleteById(addedMedicalRecord.get().getMedicalRecordId());
        personRepository.deleteById(person.getPersonId());
    }


    @Test
    @DisplayName("WHEN processing a PUT /medicalRecord request for an existing medical record " +
            "THEN return status is OK, the returned value is the updated medical record " +
            "and the medical record has been updated in DB")
    @Transactional
    public void updateMedicalRecordTest_WithSuccess() throws Exception {
        //init the database with one medical record to update
        Person person = new Person();
        person.setFirstName(ITConstants.FIRSTNAME_TO_UPDATE);
        person.setLastName(ITConstants.LASTNAME_TO_UPDATE);
        person.setAddress(TestConstants.EXISTING_ADDRESS);
        person = personRepository.save(person);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(person.getFirstName());
        medicalRecord.setLastName(person.getLastName());
        medicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);
        medicalRecord = medicalRecordRepository.save(medicalRecord);
        person.setMedicalRecord(medicalRecord);
        personRepository.save(person);

        //test
        MedicalRecordDTO medicalRecordDTOToUpdate = new MedicalRecordDTO();
        medicalRecordDTOToUpdate.setFirstName(medicalRecord.getFirstName());
        medicalRecordDTOToUpdate.setLastName(medicalRecord.getLastName());
        medicalRecordDTOToUpdate.setBirthDate(medicalRecord.getBirthDate());
        List<String> medications = new ArrayList<>();
        medications.add("updated_medications_1");
        medications.add("updated_medications_2");
        medicalRecordDTOToUpdate.setMedications(medications);
        List<String> allergies = new ArrayList<>();
        allergies.add("updated_allergies_1");
        medicalRecordDTOToUpdate.setAllergies(allergies);

        mockMvc.perform(put("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecordDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.medications", is(medications)))
                .andExpect(jsonPath("$.allergies", is(allergies)));

        Optional<MedicalRecord> updatedMedicalRecord = medicalRecordRepository.findById(medicalRecord.getMedicalRecordId());
        assertThat(updatedMedicalRecord).isNotEmpty();
        assertEquals(medicalRecordDTOToUpdate.getMedications(), updatedMedicalRecord.get().getMedications());
        assertEquals(medicalRecordDTOToUpdate.getAllergies(), updatedMedicalRecord.get().getAllergies());

        //clean the database by deleting the initialized medical record and person
        person.setMedicalRecord(null);
        personRepository.save(person);
        medicalRecordRepository.deleteById(medicalRecord.getMedicalRecordId());
        personRepository.deleteById(person.getPersonId());
    }


    @Test
    @DisplayName("WHEN processing a DELETE /medicalRecord request for an existing medical record " +
            "THEN the return status is 'No content' and medical record is no longer present in DB " +
            "and no longer associated to the person")
    public void deleteMedicalRecordByFirstNameAndLastNameTest_WithSuccess() throws Exception {
        //init the database with one medical record to delete
        Person person = new Person();
        person.setFirstName(ITConstants.FIRSTNAME_TO_DELETE);
        person.setLastName(ITConstants.LASTNAME_TO_DELETE);
        person.setAddress(TestConstants.EXISTING_ADDRESS);
        person = personRepository.save(person);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(person.getFirstName());
        medicalRecord.setLastName(person.getLastName());
        medicalRecord.setBirthDate(TestConstants.CHILD_BIRTHDATE);
        medicalRecordRepository.save(medicalRecord);
        person.setMedicalRecord(medicalRecord);
        personRepository.save(person);

        //test
        mockMvc.perform(delete("/medicalRecord")
                .param("firstName", ITConstants.FIRSTNAME_TO_DELETE)
                .param("lastName", ITConstants.LASTNAME_TO_DELETE))
                .andExpect(status().isNoContent());

        Optional<MedicalRecord> foundMedicalRecordAfterDeletion = medicalRecordRepository.findById(medicalRecord.getMedicalRecordId());
        assertThat(foundMedicalRecordAfterDeletion).isEmpty();
        Optional<Person> personAfterDeletion = personRepository.findById(person.getPersonId());
        assertThat(personAfterDeletion).isNotEmpty();
        assertNull(personAfterDeletion.get().getMedicalRecord());

        //clean the database by deleting the initialized person
        personRepository.deleteById(person.getPersonId());
    }
}
