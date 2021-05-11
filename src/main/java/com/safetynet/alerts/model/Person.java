package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


@Data
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="address", nullable = false)
    private String address;

    private String city;

    private String zip;

    private String phone;

    private String email;

    @OneToOne
    @JoinColumn(name = "medicalRecordId")
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name="fireStationId")
    private FireStation fireStation;

    @Transient
    private int age;

}
