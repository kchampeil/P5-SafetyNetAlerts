package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class MedicalRecordService implements IMedicalRecordService {

    private static final Logger logger = LogManager.getLogger(MedicalRecordService.class);

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * save a list of medical records in DB
     *
     * @param listOfMedicalRecords list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean savelistOfMedicalRecords(List<MedicalRecord> listOfMedicalRecords) {
            try {
                medicalRecordRepository.saveAll(listOfMedicalRecords);
                return true;
            } catch (IllegalArgumentException e) {
                logger.error("error when saving the list of medical records in DB : " + e.getMessage() + "\n");
                return false;
            }
    }
}
