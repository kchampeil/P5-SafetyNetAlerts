package com.safetynet.alerts.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FireStationCoverageDTO {

    List<PersonCoveredContactsDTO> personCoveredContactsDTOList;

    private int numberOfAdults;

    private int numberOfChildren;
}
