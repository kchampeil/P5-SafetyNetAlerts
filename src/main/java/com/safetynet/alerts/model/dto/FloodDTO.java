package com.safetynet.alerts.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FloodDTO {

    private int stationNumber;

    private Map<String, List<PersonCoveredDTO>> personsCoveredByAddress;

}
