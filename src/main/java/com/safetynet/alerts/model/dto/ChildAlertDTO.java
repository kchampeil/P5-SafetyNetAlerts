package com.safetynet.alerts.model.dto;

import com.safetynet.alerts.model.Person;
import lombok.Data;

import java.util.List;

@Data
public class ChildAlertDTO {

    private String firstName;

    private String lastName;

    private int age;

    private List<Person> listOfOtherHouseholdMembers;
}
