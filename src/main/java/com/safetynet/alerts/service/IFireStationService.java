package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FireStationDTO;
import com.safetynet.alerts.model.dto.FloodDTO;

import java.util.List;
import java.util.Optional;

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
     * @throws AlreadyExistsException      if the fire station already exists in repository
     * @throws MissingInformationException if no address or station number has been given
     */
    Optional<FireStationDTO> addFireStation(FireStationDTO fireStationDTOToAdd) throws AlreadyExistsException, MissingInformationException;

    /**
     * update the station number of a given address in the repository
     *
     * @param fireStationDTOToUpdate an address / fire station relationship to update
     * @return the updated fireStation
     * @throws DoesNotExistException       if the fire station to update does not exist in repository
     * @throws MissingInformationException if no address or station number has been given
     */
    Optional<FireStationDTO> updateFireStation(FireStationDTO fireStationDTOToUpdate) throws DoesNotExistException, MissingInformationException;

    /**
     * delete the fire stations for the given address in the repository
     *
     * @param address the address we want to delete the fire station relationships
     * @return the deleted fire station
     * @throws DoesNotExistException       if no fire station has been found for the given address
     * @throws MissingInformationException if no address has been given
     */
    FireStation deleteFireStationByAddress(String address) throws DoesNotExistException, MissingInformationException;

    /**
     * delete the fire stations for the given station number in the repository
     *
     * @param stationNumber the station number we want to delete the fire station relationships
     * @return the list of deleted fire stations
     * @throws DoesNotExistException       if no fire station has been found for the given station number
     * @throws MissingInformationException if no station number has been given
     */
    List<FireStation> deleteFireStationByStationNumber(Integer stationNumber) throws DoesNotExistException, MissingInformationException;
}
