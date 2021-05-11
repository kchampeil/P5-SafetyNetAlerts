package com.safetynet.alerts.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FireStationDTO {

    private Long fireStationId;

    @JsonProperty("station")
    private Integer stationNumber;

    private String address;

}
