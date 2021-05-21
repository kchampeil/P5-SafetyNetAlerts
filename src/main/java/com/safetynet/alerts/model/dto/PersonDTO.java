package com.safetynet.alerts.model.dto;

import lombok.Data;


@Data
public class PersonDTO {

    private Long personId;

    private String firstName;

    private String lastName;

    private String address;

    private String city;

    private String zip;

    private String phone;

    private String email;
}
