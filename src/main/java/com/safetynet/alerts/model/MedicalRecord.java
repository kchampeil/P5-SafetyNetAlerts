package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDate;
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

    //TO-DO à voir si là ou dans Person comme initialement prévu
    @JsonProperty("birthdate")
    @JsonFormat(pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;

}
