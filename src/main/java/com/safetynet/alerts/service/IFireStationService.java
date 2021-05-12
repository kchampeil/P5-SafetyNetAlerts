package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;

import java.util.List;

public interface IFireStationService {

    /**
     * save a list of fire stations in DB
     *
     * @param listOfFireStations list to be saved in DB
     * @return true if data saved, else false
     */
    Iterable<FireStation> saveListOfFireStations(List<FireStation> listOfFireStations);


    /**
     * allow getting the list of all fire stations found in DB
     *
     * @return a list of FireStation
     */
    Iterable<FireStationDTO> getAllFireStations();

    /**
     * allow getting the list of persons for a given address with its fire station number, found in DB
     *
     * @return the fire station coverage for the address
     */
    FireDTO getFireStationCoverageByAddress(String address);

    /**
     * allow getting person information about people covered by fire stations
     * for a given list of station number et grouped by station number and address, found in DB
     *
     * @return the flood for the fire stations
     */
    List<FloodDTO> getFloodByStationNumbers(List<Integer> listOfStationNumbers);

    /**
     * save a new address / fire station relationship in the repository
     *
     * @param fireStationDTOToAdd a new address / fire station relationship to add
     * @return the added fireStation
     * @throws AlreadyExistsException
     * @throws MissingInformationException
     */
    FireStationDTO addFireStation(FireStationDTO fireStationDTOToAdd) throws AlreadyExistsException, MissingInformationException;

    /**
     * update the station number of a given address in the repository
     *
     * @param fireStationDTOToUpdate an address / fire station relationship to update
     * @return the updated fireStation
     * @throws DoesNotExistException
     * @throws MissingInformationException
     */
    FireStationDTO updateFireStation(FireStationDTO fireStationDTOToUpdate) throws DoesNotExistException, MissingInformationException;
}
