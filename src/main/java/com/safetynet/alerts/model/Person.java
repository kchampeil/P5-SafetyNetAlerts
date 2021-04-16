package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "persons")
public class Person {

    @Id
    private int PersonId;

    private String firstName;

    private String lastName;

    private String address;

    private String city;

    private String zip;

    private String phone;

    private String email;

    private LocalDate birthDate;

    @Transient
    private Integer age;

}
