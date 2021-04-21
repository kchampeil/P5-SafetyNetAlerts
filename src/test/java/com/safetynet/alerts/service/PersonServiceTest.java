package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PersonServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private PersonService personService;

    private Person person;

    @BeforeAll
    private static void setUp() {

    }

    @BeforeEach
    private void setUpPerTest() {
        person = new Person();
        person.setFirstName("PST_first_name");
        person.setLastName("PST_last_name");
        person.setZip("PST_zip");
    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfPersons tests
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN a consistent list of persons
     * THEN it is saved in DB and return code is true
     *
     * GIVEN an exception when processing
     * THEN no data is saved in DB and return code is false
     * -------------------------------------------------------------------------------------------------------------------- */
    @Nested
    @DisplayName("saveListOfPersons tests")
    class saveListOfPersonsTest {
        @Test
        @DisplayName("GIVEN a consistent list of persons THEN it is saved in DB and return code is true")
        public void saveListOfPersonsTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);

            //THEN
            assertTrue(personService.saveListOfPersons(listOfPersons));
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }

        @Test
        @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
        public void saveListOfPersonsTest_WithException() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.saveAll(listOfPersons)).thenThrow(IllegalArgumentException.class);

            //THEN
            assertFalse(personService.saveListOfPersons(listOfPersons));
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());

        }
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllPersons tests
     * ----------------------------------------------------------------------------------------------------------------------
     * GIVEN persons in DB
     * WHEN processing a GET /persons request
     * THEN a list of persons is returned
     *
     * GIVEN an exception
     * WHEN processing a GET /persons request
     * THEN null is returned
     * -------------------------------------------------------------------------------------------------------------------- */
    @Nested
    @DisplayName("getAllPersons tests")
    class GetAllPersonsTest {
        @Test
        @DisplayName("GIVEN persons in DB WHEN processing a GET /persons request THEN a list of persons is returned")
        public void getAllPersonsTest_WithPersonDataInDb() {
            //GIVEN
            List<Person> expectedListOfPersons = new ArrayList<>();
            expectedListOfPersons.add(person);
            when(personRepositoryMock.findAll()).thenReturn(expectedListOfPersons);

            //THEN
            assertEquals(expectedListOfPersons, personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();

        }

        @Test
        @DisplayName("GIVEN an exception WHEN processing a GET /persons request THEN null is returned")
        public void getAllPersonsTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();

        }

    }

}
