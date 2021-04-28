package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class ChildAlertServiceTest {

    @MockBean
    private PersonRepository personRepositoryMock;

    @Autowired
    private ChildAlertService childAlertService;

    private static Person aChild;
    private static Person hisParent;


    @BeforeAll
    private static void setUp() {

        aChild = new Person();
        aChild.setFirstName("CAST_first_name");
        aChild.setLastName("CAST_last_name");
        aChild.setEmail("CAST_email");
        aChild.setAddress("CAST_address");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(aChild.getFirstName());
        medicalRecord.setLastName(aChild.getLastName());
        medicalRecord.setBirthDate(LocalDate.of(2019, 9, 9));
        aChild.setMedicalRecord(medicalRecord);

        hisParent = new Person();
        hisParent.setFirstName("CAST_first_name_parent");
        hisParent.setLastName("CAST_last_name");
        hisParent.setEmail("CAST_email_parent");
        hisParent.setAddress(aChild.getAddress());

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setFirstName(hisParent.getFirstName());
        medicalRecord2.setLastName(hisParent.getLastName());
        medicalRecord2.setBirthDate(LocalDate.of(1980, 9, 9));
        hisParent.setMedicalRecord(medicalRecord2);
    }

    @BeforeEach
    private void setUpPerTest() {

    }

    /* ----------------------------------------------------------------------------------------------------------------------
     *                  getChildAlertByAddress tests
     * ----------------------------------------------------------------------------------------------------------------------*/
    @Nested
    @DisplayName("getChildAlertByAddress tests")
    class GetChildAlertByAddressTest {

        @Test
        @DisplayName("GIVEN children in repository for the requested address " +
                "WHEN getting child alert on address " +
                "THEN a list of child alert information is returned")
        public void getChildAlertByAddressTest_WithConsistentList() {
            //GIVEN
            List<Person> listOfPerson = new ArrayList<>();
            listOfPerson.add(aChild);
            listOfPerson.add(hisParent);

            // TTR List<ChildAlertDTO> expectedListOfChildAlert = new ArrayList<>();
            //TTR expectedListOfChildAlert.add(personInfoDTO);

            when(personRepositoryMock.findAllByAddress(aChild.getAddress())).thenReturn(listOfPerson);

            //WHEN
            List<ChildAlertDTO> returnedListOfChildAlert = childAlertService.getChildAlertByAddress(aChild.getAddress());

            //THEN
            assertEquals(1, returnedListOfChildAlert.size());
            assertEquals(aChild.getFirstName(), returnedListOfChildAlert.get(0).getFirstName());
            assertEquals(aChild.getLastName(), returnedListOfChildAlert.get(0).getLastName());
            assertEquals(hisParent, returnedListOfChildAlert.get(0).getListOfOtherHouseholdMembers().get(0));
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress(aChild.getAddress());

        }

        @Test
        @DisplayName("GIVEN no children in repository for the requested address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is empty")
        public void getChildAlertByAddressTest_WithNoDataInRepository() {

            //GIVEN
            when(personRepositoryMock.findAllByAddress("TestAddress")).thenReturn(null);

            //THEN
            assertThat(childAlertService.getChildAlertByAddress("TestAddress")).isEmpty();
            verify(personRepositoryMock, Mockito.times(1)).findAllByAddress("TestAddress");
        }

        @Test
        @DisplayName("GIVEN null address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getChildAlertByAddressTest_WithNullRequestParameters() {

            assertNull(childAlertService.getChildAlertByAddress(null));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress(null);

        }

        @Test
        @DisplayName("GIVEN empty address " +
                "WHEN getting child alert on address " +
                "THEN the returned list is null and no request has been sent to repository")
        public void getChildAlertByAddressTest_WithEmptyRequestParameters() {

            assertNull(childAlertService.getChildAlertByAddress(""));
            verify(personRepositoryMock, Mockito.times(0)).findAllByAddress("");

        }

        @Test
        @DisplayName("GIVEN an exception when processing " +
                "THEN no data is returned")
        public void getChildAlertByAddressTest_WithException() {
            //GIVEN
            when(personRepositoryMock.findAllByAddress(anyString()))
                    .thenThrow(IllegalArgumentException.class);

            //THEN
            assertNull(childAlertService.getChildAlertByAddress(""));
            verify(personRepositoryMock, Mockito.times(0))
                    .findAllByAddress(anyString());

        }

    }
}
