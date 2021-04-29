package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class PhoneAlertServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private PhoneAlertService phoneAlertService;


    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getPhoneAlertByFireStation tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getPhoneAlertByFireStation tests")
    class GetPhoneAlertByFireStationTest {
        @Test
        @DisplayName("GIVEN citizens covered by the requested fire station found in repository " +
                "WHEN asking for the phone number list " +
                "THEN a list of citizens' phone numbers covered by the fire station is returned")
        public void getPhoneAlertByFireStationTest_WithInfoInRepository() {
            //GIVEN 3 persons but only 2 distinct phone numbers
            List<Person> listOfPersons = new ArrayList<>();

            Person person1 = new Person();
            person1.setFirstName("PST_first_name_1");
            person1.setLastName("PST_last_name_1");
            person1.setPhone("33 1 23 45 67 89");
            listOfPersons.add(person1);

            Person person2 = new Person();
            person2.setFirstName("PST_first_name_2");
            person2.setLastName("PST_last_name_2");
            person2.setPhone("33 1 98 76 54 32");
            listOfPersons.add(person2);

            Person person3 = new Person();
            person3.setFirstName("PST_first_name_3");
            person3.setLastName("PST_last_name_3");
            person3.setPhone("33 1 23 45 67 89");
            listOfPersons.add(person3);

            when(personRepositoryMock.findAllByFireStation_StationNumber(3)).thenReturn(listOfPersons);

            //THEN
            assertEquals(2,
                    phoneAlertService.getPhoneAlertByFireStation(3).size());
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(3);
        }

        @Test
        @DisplayName("GIVEN no citizens covered by the requested fire station found in repository " +
                "WHEN asking for the phone number list " +
                "THEN an empty list of citizens' phone numbers is returned")
        public void getPhoneAlertByFireStationTest_WithNoInfoInRepository() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(999)).thenReturn(new ArrayList<>());

            //THEN
            assertThat(phoneAlertService.getPhoneAlertByFireStation(999)).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByFireStation_StationNumber(999);
        }

        @Test
        @DisplayName("GIVEN a null fire station number " +
                "WHEN asking for the phone number list " +
                "THEN no list is returned")
        public void getPhoneAlertByFireStationTest_WithFireStationNumberNull() {
            //GIVEN
            when(personRepositoryMock.findAllByFireStation_StationNumber(null)).thenReturn(null);

            //THEN
            assertThat(phoneAlertService.getPhoneAlertByFireStation(null)).isNull();
            verify(personRepositoryMock, Mockito.times(0)).findAllByFireStation_StationNumber(null);
        }

    }

}
