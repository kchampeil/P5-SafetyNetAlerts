package com.safetynet.alerts.model;

import lombok.Data;

import java.io.Serializable;


//TOASK comment faire avec EmbeddedId pour charger le fichier JSON ?
// @Embeddable

@Data
public class PersonId implements Serializable {

    private String firstName;

    private String lastName;

}
