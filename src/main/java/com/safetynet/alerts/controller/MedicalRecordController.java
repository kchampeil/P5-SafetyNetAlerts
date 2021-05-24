package com.safetynet.alerts.controller;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.service.IMedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class MedicalRecordController {

    private final IMedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(IMedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Read - Get all medical records
     *
     * @return - An Iterable object of MedicalRecord full filled
     */
    @GetMapping("/medicalrecords")
    public ResponseEntity<Iterable<MedicalRecordDTO>> getAllMedicalRecords() {
        log.info("GET request on endpoint /medicalrecords received");

        List<MedicalRecordDTO> listOfMedicalRecordsDTO = (List<MedicalRecordDTO>) medicalRecordService.getAllMedicalRecords();

        log.info("response to GET request on endpoint /medicalrecords sent with "
                + listOfMedicalRecordsDTO.size() + " values \n");
        return new ResponseEntity<>(listOfMedicalRecordsDTO, HttpStatus.OK);
    }


    /**
     * Create - Post a new medical record
     *
     * @param medicalRecordDTOToAdd to add to repository
     * @return the added MedicalRecordDTO
     */
    @PostMapping(value = "/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(@RequestBody MedicalRecordDTO medicalRecordDTOToAdd) {

        log.info("POST request on endpoint /medicalRecord received for medical record "
                + medicalRecordDTOToAdd.getFirstName() + " " + medicalRecordDTOToAdd.getLastName());

        try {
            Optional<MedicalRecordDTO> addedMedicalRecordDTO = medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd);

            if (addedMedicalRecordDTO.isPresent()) {
                log.info("new medical record " + medicalRecordDTOToAdd.getFirstName() + medicalRecordDTOToAdd.getLastName() + " has been saved "
                        + " with id: " + addedMedicalRecordDTO.get().getMedicalRecordId() + "\n");
                return new ResponseEntity<>(addedMedicalRecordDTO.get(), HttpStatus.CREATED);
            } else {
                log.error("new medical record " + medicalRecordDTOToAdd + " has not been saved \n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (AlreadyExistsException alreadyExistsException) {
            log.error(alreadyExistsException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.CONFLICT, alreadyExistsException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    /**
     * Update - Put a medical record for a given firstname+lastname
     *
     * @param medicalRecordDTOToUpdate to update in repository
     * @return the added MedicalRecordDTO
     */
    @PutMapping(value = "/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(@RequestBody MedicalRecordDTO medicalRecordDTOToUpdate) {

        log.info("PUT request on endpoint /medicalRecord received for person "
                + medicalRecordDTOToUpdate.getFirstName() + " " + medicalRecordDTOToUpdate.getLastName());

        try {
            Optional<MedicalRecordDTO> updatedMedicalRecordDTO = medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate);

            if (updatedMedicalRecordDTO.isPresent()) {
                log.info("Medical record " + medicalRecordDTOToUpdate.getFirstName() + medicalRecordDTOToUpdate.getLastName() + " has been updated "
                        + " with id: " + updatedMedicalRecordDTO.get().getMedicalRecordId() + "\n");
                return new ResponseEntity<>(updatedMedicalRecordDTO.get(), HttpStatus.OK);
            } else {
                log.error("Medical record " + medicalRecordDTOToUpdate + " has not been updated \n");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (DoesNotExistException doesNotExistException) {
            log.error(doesNotExistException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, doesNotExistException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    /**
     * Delete - Delete a medical record for a firstname+lastname
     *
     * @param firstName the firstname of the person we want to delete the medical record
     * @param lastName  the lastname of the person we want to delete the medical record
     * @return Http status
     */
    @DeleteMapping(value = "/medicalRecord")
    public ResponseEntity<?> deleteMedicalRecordByFirstNameAndLastName(
            @RequestParam String firstName, @RequestParam String lastName) {

        log.info("DELETE request on endpoint /medicalRecord received for person: " + firstName + " " + lastName);

        try {
            MedicalRecord deletedMedicalRecord = medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);

            if (deletedMedicalRecord != null) {
                log.info("Medical record with id :" + deletedMedicalRecord.getMedicalRecordId()
                        + " has been deleted for person " + deletedMedicalRecord.getFirstName()
                        + " " + deletedMedicalRecord.getLastName() + " \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            } else {
                log.info("No medical record has been deleted for person: " + firstName + " " + lastName + " \n");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (DoesNotExistException doesNotExistException) {
            log.error(doesNotExistException.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, doesNotExistException.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage() + " \n");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
