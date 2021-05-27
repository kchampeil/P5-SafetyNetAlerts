package com.safetynet.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.integration.ITConstants.ITConstants;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    FireStationRepository fireStationRepository;

    @Autowired
    MedicalRecordRepository medicalRecordRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Person person;

    @BeforeEach
    private void setUpPerTest() {
        person = new Person();
        person.setAddress(TestConstants.EXISTING_ADDRESS);
        person.setEmail("email@safety.com");
        person.setPhone("phone");
        person.setCity("city");
        person.setZip("zip");
    }

    @Test
    @Order(1)
    @DisplayName("WHEN asking for the list of persons GET /persons " +
            "THEN return status is OK and the list of all persons is returned")
    public void getAllPersonsTest_WithSuccess() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(ITConstants.NB_OF_PERSON_RECORDS_ALL)));
    }


    @Test
    @DisplayName("WHEN processing a POST/person request for a new person" +
            "THEN return status is CREATED and the returned value is the person")
    public void addPersonTest_WithSuccess() throws Exception {
        //test
        PersonDTO personDTOToAdd = new PersonDTO();
        personDTOToAdd.setFirstName(TestConstants.NEW_FIRSTNAME);
        personDTOToAdd.setLastName(TestConstants.NEW_LASTNAME);
        personDTOToAdd.setAddress(TestConstants.EXISTING_ADDRESS);
        personDTOToAdd.setEmail("emailOfAddedPerson@safety.com");
        personDTOToAdd.setPhone("phone of added person");
        personDTOToAdd.setCity("city of added person");
        personDTOToAdd.setZip("zip of added person");

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTOToAdd)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.address", is(personDTOToAdd.getAddress())));

        Optional<Person> addedPerson = Optional.ofNullable(personRepository
                .findByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName()));
        assertThat(addedPerson).isNotEmpty();
        assertEquals(personDTOToAdd.getAddress(), addedPerson.get().getAddress());
        assertNotNull(addedPerson.get().getFireStation());

        //clean the database by deleting the added person
        personRepository.deleteById(addedPerson.get().getPersonId());
    }


    @Test
    @DisplayName("WHEN processing a PUT /person request for an existing person " +
            "THEN return status is OK and the returned value is the updated person" +
            "with his new covering fire station")
    public void updatePersonTest_WithSuccess() throws Exception {
        //init the database with one person to update
        person.setFirstName(ITConstants.FIRSTNAME_TO_UPDATE);
        person.setLastName(ITConstants.LASTNAME_TO_UPDATE);
        person.setFireStation(fireStationRepository.findByAddress(person.getAddress()));
        person = personRepository.save(person);

        //test
        PersonDTO personDTOToUpdate = new PersonDTO();
        personDTOToUpdate.setFirstName(person.getFirstName());
        personDTOToUpdate.setLastName(person.getLastName());
        personDTOToUpdate.setAddress(ITConstants.ANOTHER_EXISTING_ADDRESS);
        personDTOToUpdate.setEmail(person.getEmail());
        personDTOToUpdate.setPhone(person.getPhone());
        personDTOToUpdate.setCity(person.getCity());
        personDTOToUpdate.setZip(person.getZip());

        mockMvc.perform(put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.address", is(personDTOToUpdate.getAddress())));

        Optional<Person> updatedPerson = Optional.ofNullable(personRepository
                .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName()));
        assertThat(updatedPerson).isNotEmpty();
        assertEquals(personDTOToUpdate.getAddress(), updatedPerson.get().getAddress());
        assertEquals(fireStationRepository.findByAddress(personDTOToUpdate.getAddress()),
                updatedPerson.get().getFireStation());
    }


    @Test
    @DisplayName("WHEN processing a DELETE /person request for an existing person " +
            "THEN the return status is 'No content' and person is no longer present in DB " +
            "and the associated medical record is also deleted")
    public void deletePersonByAddressTest_WithSuccess() throws Exception {
        //init the database with one person to delete and his medical record
        person.setFirstName(ITConstants.FIRSTNAME_TO_DELETE);
        person.setLastName(ITConstants.LASTNAME_TO_DELETE);
        person.setFireStation(fireStationRepository.findByAddress(person.getAddress()));
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(person.getFirstName());
        medicalRecord.setLastName(person.getLastName());
        medicalRecord.setBirthDate(TestConstants.ADULT_BIRTHDATE);
        medicalRecord = medicalRecordRepository.save(medicalRecord);
        person.setMedicalRecord(medicalRecord);
        personRepository.save(person);

        //test
        mockMvc.perform(delete("/person")
                .param("firstName", ITConstants.FIRSTNAME_TO_DELETE)
                .param("lastName", ITConstants.LASTNAME_TO_DELETE))
                .andExpect(status().isNoContent());

        Optional<Person> foundPersonAfterDeletion = Optional.ofNullable(personRepository
                .findByFirstNameAndLastName(ITConstants.FIRSTNAME_TO_DELETE, ITConstants.LASTNAME_TO_DELETE));
        assertThat(foundPersonAfterDeletion).isEmpty();
        medicalRecord = medicalRecordRepository.findByFirstNameAndLastName(ITConstants.FIRSTNAME_TO_DELETE, ITConstants.LASTNAME_TO_DELETE);
        assertNull(medicalRecord);
    }
}
