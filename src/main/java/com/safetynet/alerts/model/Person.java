package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    private String firstName;

    private String lastName;

    private String address;

    private String city;

    private String zip;

    private String phone;

    private String email;

    @OneToOne
    @JoinColumn(name = "medicalRecordId")
    private MedicalRecord medicalRecord;

    /*
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="fireStationId")
    private FireStation fireStation;

     */

}
