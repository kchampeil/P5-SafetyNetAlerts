package com.safetynet.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.integration.ITConstants.ITConstants;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.testconstants.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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

@TestPropertySource(locations = "/application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FireStationRepository fireStationRepository;

    @Autowired
    PersonRepository personRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("WHEN asking for the list of fire stations GET /firestations " +
            "THEN return status is OK and the list of all fire stations is returned")
    public void getAllFireStationsTest_WithSuccess() throws Exception {
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(ITConstants.NB_OF_FIRE_STATION_RECORDS_ALL)));
    }


    @Test
    @DisplayName("WHEN processing a POST/firestation request for a new mapping address/fire station" +
            "THEN return status is CREATED, the returned value is the added fire station" +
            "and the fire station is added in DB")
    public void addFireStationTest_WithSuccess() throws Exception {
        FireStationDTO fireStationDTOToAdd = new FireStationDTO();
        fireStationDTOToAdd.setStationNumber(TestConstants.NEW_STATION_NUMBER);
        fireStationDTOToAdd.setAddress(TestConstants.NEW_ADDRESS);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationDTOToAdd)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.address", is(fireStationDTOToAdd.getAddress())))
                .andExpect(jsonPath("$.station", is(fireStationDTOToAdd.getStationNumber())));

        Optional<FireStation> addedFireStation =
                Optional.ofNullable(fireStationRepository.findByAddress(fireStationDTOToAdd.getAddress()));
        assertThat(addedFireStation).isNotEmpty();
        assertEquals(fireStationDTOToAdd.getStationNumber(), addedFireStation.get().getStationNumber());

        //clean the database by deleting the added fire station
        fireStationRepository.deleteById(addedFireStation.get().getFireStationId());
    }


    @Test
    @DisplayName("WHEN processing a PUT /firestation request for a new station number for an existing address " +
            "THEN return status is OK, the returned value is the updated fire station " +
            "and the fire station is updated in DB")
    public void updateFireStationTest_WithSuccess() throws Exception {
        //init the database with one fire station to update
        FireStation fireStation = new FireStation();
        fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        fireStation.setAddress(ITConstants.ADDRESS_OF_FIRE_STATION_TO_UPDATE);
        fireStation = fireStationRepository.save(fireStation);

        //test
        FireStationDTO fireStationDTOToUpdate = new FireStationDTO();
        fireStationDTOToUpdate.setStationNumber(ITConstants.NEW_STATION_NUMBER_FOR_FIRE_STATION_TO_UPDATE);
        fireStationDTOToUpdate.setAddress(ITConstants.ADDRESS_OF_FIRE_STATION_TO_UPDATE);

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.address", is(fireStationDTOToUpdate.getAddress())))
                .andExpect(jsonPath("$.station", is(fireStationDTOToUpdate.getStationNumber())));

        Optional<FireStation> updatedFireStation = fireStationRepository.findById(fireStation.getFireStationId());
        assertThat(updatedFireStation).isNotEmpty();
        assertEquals(fireStationDTOToUpdate.getStationNumber(), updatedFireStation.get().getStationNumber());

        //clean the database by deleting the initialized fire station
        fireStationRepository.deleteById(fireStation.getFireStationId());
    }


    @Test
    @DisplayName("WHEN processing a DELETE /firestation/address request for an existing address where one person is living" +
            "THEN the return status is 'No content', the fire station is no longer present in DB " +
            "and the person is not covered by a fire station anymore")
    public void deleteFireStationByAddressTest_WithSuccess() throws Exception {
        //init the database with one fire station to delete and one person living at the covered address
        FireStation fireStation = new FireStation();
        fireStation.setStationNumber(TestConstants.EXISTING_STATION_NUMBER);
        fireStation.setAddress(ITConstants.ADDRESS_TO_DELETE);
        fireStation = fireStationRepository.save(fireStation);

        Person person = new Person();
        person.setFirstName(TestConstants.NEW_FIRSTNAME);
        person.setLastName(TestConstants.NEW_LASTNAME);
        person.setAddress(fireStation.getAddress());
        person.setFireStation(fireStation);
        person = personRepository.save(person);

        //Test
        mockMvc.perform(delete("/firestation/address")
                .param("address", ITConstants.ADDRESS_TO_DELETE))
                .andExpect(status().isNoContent());

        Optional<FireStation> foundFireStationAfterDeletion = fireStationRepository.findById(fireStation.getFireStationId());
        assertThat(foundFireStationAfterDeletion).isEmpty();

        Optional<Person> personAfterDeletion = personRepository.findById(person.getPersonId());
        assertThat(personAfterDeletion).isNotEmpty();
        assertNull(personAfterDeletion.get().getFireStation());

        //clean the database by deleting the initialized person
        personRepository.deleteById(person.getPersonId());
    }


    @Test
    @DisplayName("WHEN processing a DELETE /firestation/station request for an existing station number covering one person" +
            "THEN the return status is 'No content', the fire station is no longer present in DB " +
            "and the person is not covered by a fire station anymore")
    public void deleteFireStationByStationNumberTest_WithSuccess() throws Exception {
        //init the database with 2 fire stations to delete and one person living at the second covered address
        FireStation fireStation = new FireStation();
        fireStation.setStationNumber(ITConstants.STATION_NUMBER_OF_STATION_TO_DELETE);
        fireStation.setAddress(ITConstants.ADDRESS_TO_DELETE + " 1");
        fireStationRepository.save(fireStation);
        fireStation.setStationNumber(ITConstants.STATION_NUMBER_OF_STATION_TO_DELETE);
        fireStation.setAddress(ITConstants.ADDRESS_TO_DELETE + " 2");
        fireStation = fireStationRepository.save(fireStation);

        Person person = new Person();
        person.setFirstName(TestConstants.NEW_FIRSTNAME);
        person.setLastName(TestConstants.NEW_LASTNAME);
        person.setAddress(fireStation.getAddress());
        person.setFireStation(fireStation);
        person = personRepository.save(person);

        //Test
        mockMvc.perform(delete("/firestation/station")
                .param("stationNumber", ITConstants.STATION_NUMBER_OF_STATION_TO_DELETE.toString()))
                .andExpect(status().isNoContent());

        List<FireStation> foundFireStationsAfterDeletion =
                fireStationRepository.findAllByStationNumber(ITConstants.STATION_NUMBER_OF_STATION_TO_DELETE);
        assertEquals(0, foundFireStationsAfterDeletion.size());

        Optional<Person> personAfterDeletion = personRepository.findById(person.getPersonId());
        assertThat(personAfterDeletion).isNotEmpty();
        assertNull(personAfterDeletion.get().getFireStation());

        //clean the database by deleting the initialized person
        personRepository.deleteById(person.getPersonId());
    }
}
