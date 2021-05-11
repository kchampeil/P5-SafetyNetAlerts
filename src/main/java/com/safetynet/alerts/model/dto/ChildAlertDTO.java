package com.safetynet.alerts.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChildAlertDTO {

    private String firstName;

    private String lastName;

    private int age;

    private List<HouseholdMemberDTO> listOfOtherHouseholdMembers;
}
