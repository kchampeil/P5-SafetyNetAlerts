package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "firestations")
public class FireStation {

    @Id
    @JsonProperty("station")
    private Integer stationNumber;
    //TODO revoir le modèle station au lieu de stationNumber

    //@ElementCollection
    //private List<String> address;
    //TODO revoir le modèle
    private String address;
}
