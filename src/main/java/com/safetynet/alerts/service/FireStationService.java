package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class FireStationService implements IFireStationService {

    private static final Logger logger = LogManager.getLogger(PersonService.class);

    @Autowired
    FireStationRepository fireStationRepository;

    /**
     * save a list of fire stations in DB
     * @param listOfFireStations list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean savelistOfFireStations(List<FireStation> listOfFireStations) {
        if (listOfFireStations != null && !listOfFireStations.isEmpty()) {
            try {
                fireStationRepository.saveAll(listOfFireStations);
                return true;
            } catch (IllegalArgumentException e) {
                logger.error("error when saving the list of fire stations in DB : " + e.getMessage() + "\n");
                return false;
            }
        } else {
            return false;
        }
    }
}
