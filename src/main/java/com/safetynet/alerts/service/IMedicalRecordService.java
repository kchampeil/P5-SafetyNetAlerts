package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;

import java.util.List;

public interface IMedicalRecordService {

    /**
     * save a list of medical records in DB
     *
     * @param listOfMedicalRecords list to be saved in DB
     * @return true if data saved, else false
     */
    Iterable<MedicalRecord> saveListOfMedicalRecords(List<MedicalRecord> listOfMedicalRecords);

    /**
     * allow getting the list of all medical records found in DB
     *
     * @return a list of MedicalRecord
     */
    Iterable<MedicalRecordDTO> getAllMedicalRecords();

    /**
     * save a new medical record in the repository
     *
     * @param medicalRecordToAdd a new medical record to add
     * @return the added medical record
     * @throws AlreadyExistsException
     * @throws MissingInformationException
     */
    MedicalRecordDTO addMedicalRecord(MedicalRecordDTO medicalRecordToAdd) throws AlreadyExistsException, MissingInformationException;

    /**
     * update a medical record of a given firstname+lastname
     *
     * @param medicalRecordDTOToUpdate a medical record to update
     * @return the updated medical record
     * @throws DoesNotExistException
     * @throws MissingInformationException
     */
    MedicalRecordDTO updateMedicalRecord(MedicalRecordDTO medicalRecordDTOToUpdate) throws DoesNotExistException, MissingInformationException;
}
