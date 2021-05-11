package com.safetynet.alerts.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class PersonInfoDTO {

    private String lastName;

    private String address;

    private int age;

    private String email;

    private List<String> medications;

    private List<String> allergies;

}
