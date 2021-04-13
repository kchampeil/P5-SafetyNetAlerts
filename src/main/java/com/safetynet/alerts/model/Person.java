package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Data
@Entity
@IdClass(PersonId.class)
@Table(name = "persons")
public class Person {

    //TOASK comment faire avec EmbeddedId pour charger le fichier JSON ?
    // @EmbeddedId
    // private PersonId personId;

    @Id
    private String firstName;

    @Id
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
