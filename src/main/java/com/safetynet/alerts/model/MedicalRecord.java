package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "medicalrecords")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("medical_record_id")
    private Long medicalRecordId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    //TO-DO à voir si là ou dans Person comme initialement prévu
    @JsonProperty("birthdate")
    @JsonFormat(pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Column("birth_date")
    private LocalDate birthDate;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;

}
