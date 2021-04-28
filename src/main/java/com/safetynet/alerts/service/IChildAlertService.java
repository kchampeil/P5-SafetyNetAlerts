package com.safetynet.alerts.service;

import com.safetynet.alerts.model.dto.ChildAlertDTO;

import java.util.List;

public interface IChildAlertService {

    /**
     * allow getting the list of child alert found in repository for given address
     * @param address
     * @return a list of child alert
     */
    List<ChildAlertDTO> getChildAlertByAddress(String address);
}
