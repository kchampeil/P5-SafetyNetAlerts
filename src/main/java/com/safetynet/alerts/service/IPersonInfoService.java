package com.safetynet.alerts.service;

import com.safetynet.alerts.model.dto.PersonInfoDTO;

import java.util.List;

public interface IPersonInfoService {

    /**
     * allow getting the list of person information found in repository
     * for given firstname and lastname
     * @param firstName
     * @param lastName
     * @return a list of person information
     */
    List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName);
}
