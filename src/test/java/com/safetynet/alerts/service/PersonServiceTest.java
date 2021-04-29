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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
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
        person.setEmail("PST_email");
        person.setCity("PST_city");
        person.setZip("PST_zip");
    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  saveListOfPersons tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("saveListOfPersons tests")
    class saveListOfPersonsTest {
        @Test
        @DisplayName("GIVEN a consistent list of persons " +
                "THEN it is saved in repository and return code is true")
        public void saveListOfPersonsTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);

            //THEN
            assertTrue(personService.saveListOfPersons(listOfPersons));
            verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "THEN no data is saved in repository and return code is false")
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
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllPersons tests")
    class GetAllPersonsTest {
        @Test
        @DisplayName("GIVEN persons in repository WHEN processing a GET /persons request " +
                "THEN a list of persons is returned")
        public void getAllPersonsTest_WithPersonDataInRepository() {
            //GIVEN
            List<Person> expectedListOfPersons = new ArrayList<>();
            expectedListOfPersons.add(person);
            when(personRepositoryMock.findAll()).thenReturn(expectedListOfPersons);

            //THEN
            assertEquals(expectedListOfPersons, personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an exception WHEN processing a GET /persons request " +
                "THEN null is returned")
        public void getAllPersonsTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAll()).thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(personService.getAllPersons());
            verify(personRepositoryMock, Mockito.times(1)).findAll();
        }

    }


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getAllEmailsByCity tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getAllEmailsByCity tests")
    class GetAllEmailsByCityTest {
        @Test
        @DisplayName("GIVEN citizens' emails in repository for the requested city" +
                "WHEN processing a GET /communityEmail request " +
                "THEN a list of citizens' emails is returned")
        public void getAllEmailsByCityTest_WithInfoInRepository() {
            //GIVEN
            List<Person> listOfPersons = new ArrayList<>();
            listOfPersons.add(person);
            when(personRepositoryMock.findAllByCity("PST_city")).thenReturn(listOfPersons);

            //THEN
            assertEquals(1,
                    personService.getAllEmailsByCity("PST_city").size());
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity("PST_city");
        }

        @Test
        @DisplayName("GIVEN no citizens' emails in repository for the requested city " +
                "WHEN processing a GET /communityEmail request " +
                "THEN an empty list of citizens' emails is returned")
        public void getAllEmailsByCityTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByCity("PST_city_not_in_repository")).thenReturn(new ArrayList<>());

            //THEN
            assertThat(personService.getAllEmailsByCity("PST_city_not_in_repository")).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByCity("PST_city_not_in_repository");
        }

        @Test
        @DisplayName("GIVEN a null city name " +
                "WHEN processing a GET /communityEmail request " +
                "THEN no list is returned")
        public void getAllEmailsByCityTest_WithCityNameNull() {
            //GIVEN
            when(personRepositoryMock.findAllByCity(null)).thenReturn(null);

            //THEN
            assertThat(personService.getAllEmailsByCity(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByCity(null);
        }

        @Test
        @DisplayName("GIVEN an empty city name " +
                "WHEN processing a GET /communityEmail request " +
                "THEN no list is returned")
        public void getAllEmailsByCityTest_WithCityNameEmpty() {
            //GIVEN
            when(personRepositoryMock.findAllByCity("")).thenReturn(null);

            //THEN
            assertThat(personService.getAllEmailsByCity("")).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByCity("");
        }
    }

}
