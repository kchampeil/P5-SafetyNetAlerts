package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    @DisplayName("GIVEN a consistent list of persons THEN it is saved in DB and return code is true")
    public void saveListOfPersonsTest_WithConsistentList() {
        //GIVEN

        //WHEN
        List<Person> listOfPersons = new ArrayList<>();
        listOfPersons.add(person);
        personService.saveListOfPersons(listOfPersons);

        //THEN
        verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertTrue(personService.saveListOfPersons(listOfPersons));

    }

    @Test
    @DisplayName("GIVEN an exception when processing THEN no data is saved in DB and return code is false")
    public void saveListOfPersonsTest_WithException() {
        //GIVEN
        List<Person> listOfPersons = new ArrayList<>();
        listOfPersons.add(person);
        when(personRepositoryMock.saveAll(listOfPersons)).thenThrow(IllegalArgumentException.class);

        //WHEN
        personService.saveListOfPersons(listOfPersons);

        //THEN
        verify(personRepositoryMock, Mockito.times(1)).saveAll(anyList());
        assertFalse(personService.saveListOfPersons(listOfPersons));

    }

}
