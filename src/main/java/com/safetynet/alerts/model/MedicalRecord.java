package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@IdClass(PersonId.class)
@Table(name = "medicalrecords")
public class MedicalRecord {

    //TOASK comment faire avec EmbeddedId pour charger le fichier JSON ?
    // @EmbeddedId
    // private PersonId personId;

    @Id
    private String firstName;

    @Id
    private String lastName;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;

}
