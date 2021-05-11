package com.safetynet.alerts.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FloodDTO {

    private int stationNumber;

    private Map<String, List<PersonCoveredDTO>> personsCoveredByAddress;

}
