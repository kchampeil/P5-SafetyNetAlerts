package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FireStationRepository extends CrudRepository<FireStation, Long> {

    FireStation findByAddress(String address);

    List<FireStation> findAllByStationNumber(Integer stationNumber);

    @Transactional
    FireStation deleteByAddress(String address);

    @Transactional
    List<FireStation> deleteAllByStationNumber(Integer stationNumber);
}
