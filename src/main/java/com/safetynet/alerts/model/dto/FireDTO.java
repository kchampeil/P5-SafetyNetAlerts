package com.safetynet.alerts.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FireDTO {

    List<PersonCoveredDTO> personCoveredDTOList;

    private int stationNumber;
}
