package com.safetynet.alerts.model.dto;

import lombok.Data;


@Data
public class PersonDTO {

    private Long personId;

    //TOASK @NotNull
    //TOASK @NotEmpty(message = "firstname cannot be null")
    private String firstName;

    //TOASK @NotEmpty(message = "lastname cannot be null")
    private String lastName;

    //TOASK @NotEmpty(message = "address cannot be null")
    private String address;

    //TOASK @NotEmpty(message = "city cannot be null")
    private String city;

    //TOASK @NotEmpty(message = "zip cannot be null")
    private String zip;

    //TOASK @NotEmpty(message = "phone cannot be null")
    private String phone;

    //TOASK @NotEmpty(message = "email cannot be null")
    //TOASK @Email(message = "email must be valid")
    private String email;
}
