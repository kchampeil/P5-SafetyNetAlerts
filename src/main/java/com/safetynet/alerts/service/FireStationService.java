package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FireStationService implements IFireStationService {

    @Autowired
    FireStationRepository fireStationRepository;

    /**
     * save a list of fire stations in DB
     * @param listOfFireStations list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfFireStations(List<FireStation> listOfFireStations) {
            try {
                fireStationRepository.saveAll(listOfFireStations);
                return true;
            } catch (IllegalArgumentException e) {
                log.error("error when saving the list of fire stations in DB : " + e.getMessage() + "\n");
                return false;
            }
    }


    /**
     * allow getting the list of all fire stations found in DB
     * @return a list of FireStation
     */
    @Override
    public Iterable<FireStation> getAllFireStations() {
        try {
            return fireStationRepository.findAll();
        } catch (Exception exception) {
            log.error("error when getting the list of fire stations " + exception.getMessage() + "\n");
            return null;
        }
    }
}
