package com.safetynet.alerts.service;

import com.safetynet.alerts.repository.FireStationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class FireStationService {

    @Autowired
    FireStationRepository fireStationRepository;

}
