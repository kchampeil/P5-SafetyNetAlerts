package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.dto.FireDTO;

import java.util.List;

public interface IFireStationService {

    /**
     * save a list of fire stations in DB
     * @param listOfFireStations list to be saved in DB
     * @return true if data saved, else false
     */
    boolean saveListOfFireStations(List<FireStation> listOfFireStations);


    /**
     * allow getting the list of all fire stations found in DB
     * @return a list of FireStation
     */
    Iterable<FireStation> getAllFireStations();

    /**
     * allow getting the list of persons for a given address with its fire station number, found in DB
     * @return the fire station coverage for the address
     */
    FireDTO getFireStationCoverageByAddress(String address);
}
