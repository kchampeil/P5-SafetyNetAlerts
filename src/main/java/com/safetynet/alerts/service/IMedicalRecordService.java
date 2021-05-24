package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;

import java.util.List;
import java.util.Optional;

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
     * @throws AlreadyExistsException if the medical record to add already exists in repository
     * @throws MissingInformationException if there are missing properties for the medical to save
     * @throws DoesNotExistException if the person related to medical record to add does not exist in repository
     */
    Optional<MedicalRecordDTO> addMedicalRecord(MedicalRecordDTO medicalRecordToAdd) throws AlreadyExistsException, MissingInformationException, DoesNotExistException;

    /**
     * update a medical record of a given firstname+lastname
     *
     * @param medicalRecordDTOToUpdate a medical record to update
     * @return the updated medical record
     * @throws DoesNotExistException if the medical record to update does not exist in repository
     * @throws MissingInformationException if there are missing properties for the medical to update
     */
    Optional<MedicalRecordDTO> updateMedicalRecord(MedicalRecordDTO medicalRecordDTOToUpdate) throws DoesNotExistException, MissingInformationException;

    /**
     * delete the medical record for the given firstname+lastname in the repository
     *
     * @param firstName the firstname of the person we want to delete the medical record
     * @param lastName the lastname of the person we want to delete the medical record
     * @return the deleted medical record
     * @throws DoesNotExistException       if no medical record has been found for the given firstname+lastname
     * @throws MissingInformationException if no firstname+lastname has been given
     */
    MedicalRecord deleteMedicalRecordByFirstNameAndLastName(String firstName, String lastName) throws DoesNotExistException, MissingInformationException;
}
