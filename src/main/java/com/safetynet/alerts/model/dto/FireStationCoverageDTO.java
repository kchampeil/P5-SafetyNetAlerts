package com.safetynet.alerts.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FireStationCoverageDTO {

    List<PersonCoveredContactsDTO> personCoveredContactsDTOList;

    private int numberOfAdults;

    private int numberOfChildren;
}
