package com.safetynet.alerts.service;

import com.safetynet.alerts.model.dto.PersonInfoDTO;

import java.util.List;

public interface IPersonInfoService {
    List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName);
}
