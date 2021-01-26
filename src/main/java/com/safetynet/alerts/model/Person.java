package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "persons")
public class Person {

    @EmbeddedId
    private PersonId personId;

    private String address;

    private String city;

    private int zip;

    private String phone;

    private String email;

    private LocalDate birthDate;

    @Transient
    private Integer age;

}
