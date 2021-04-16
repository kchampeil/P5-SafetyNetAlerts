package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "firestations")
public class FireStation {

    @Id
    @JsonProperty("station")
    private Integer stationNumber;

    //@ElementCollection
    //private List<String> address;
    //TODO revoir le modèle dans doc une ligne par address et pas une liste d'adresse associée à la station
    private String address;
}
