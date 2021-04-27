package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;

import java.util.List;

public interface IMedicalRecordService {

    /**
     * save a list of medical records in DB
     * @param listOfMedicalRecords list to be saved in DB
     * @return true if data saved, else false
     */
    boolean saveListOfMedicalRecords(List<MedicalRecord> listOfMedicalRecords);

    /**
     * allow getting the list of all medical records found in DB
     * @return a list of MedicalRecord
     */
    Iterable<MedicalRecord> getAllMedicalRecords();
}
