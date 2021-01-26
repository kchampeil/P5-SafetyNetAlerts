package com.safetynet.alerts.model;

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
    private String stationNumber;

    @ElementCollection
    private List<String> address;
}
