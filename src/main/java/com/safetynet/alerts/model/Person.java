package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("person_id")
    private Long personId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String address;

    private String city;

    private String zip;

    private String phone;

    private String email;

    @Column("birth_date")
    private LocalDate birthDate;

    @Transient
    private Integer age;

}
