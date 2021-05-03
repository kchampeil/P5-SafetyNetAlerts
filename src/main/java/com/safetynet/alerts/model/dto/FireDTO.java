package com.safetynet.alerts.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FireDTO {

    List<PersonCoveredDTO> personCoveredDTOList;

    private int stationNumber;
}
