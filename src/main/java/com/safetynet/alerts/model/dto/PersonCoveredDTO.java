package com.safetynet.alerts.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonCoveredDTO {

    private String lastName;

    private String phone;

    private int age;

    private List<String> medications;

    private List<String> allergies;

}
