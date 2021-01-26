package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "medicalrecords")
public class MedicalRecord {

    @EmbeddedId
    private PersonId personId;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;

}
