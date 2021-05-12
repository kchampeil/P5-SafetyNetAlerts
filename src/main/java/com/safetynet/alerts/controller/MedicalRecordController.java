package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.service.IMedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class MedicalRecordController {

    @Autowired
    private IMedicalRecordService medicalRecordService;


    /**
     * Read - Get all medical records
     *
     * @return - An Iterable object of MedicalRecord full filled
     */
    @GetMapping("/medicalrecords")
    public ResponseEntity<Iterable<MedicalRecordDTO>> getAllMedicalRecords() {
        log.info("GET request on endpoint /medicalrecords received");

        List<MedicalRecordDTO> listOfMedicalRecordsDTO = (List<MedicalRecordDTO>) medicalRecordService.getAllMedicalRecords();

        if (listOfMedicalRecordsDTO.isEmpty()) {
            log.warn("response to GET request on endpoint /medicalrecords is empty, " +
                    "no medical record found \n");
            return new ResponseEntity<>(listOfMedicalRecordsDTO, HttpStatus.NOT_FOUND);

        } else {
            log.info("response to GET request on endpoint /medicalrecords sent with "
                    + listOfMedicalRecordsDTO.size() + " values \n");
            return new ResponseEntity<>(listOfMedicalRecordsDTO, HttpStatus.OK);
        }
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
            MedicalRecordDTO addedMedicalRecordDTO = medicalRecordService.addMedicalRecord(medicalRecordDTOToAdd);

            if (addedMedicalRecordDTO != null) {
                log.info("new medical record " + medicalRecordDTOToAdd.getFirstName() + medicalRecordDTOToAdd.getLastName() + " has been saved "
                        + " with id: " + addedMedicalRecordDTO.getMedicalRecordId() + "\n");
                return new ResponseEntity<>(addedMedicalRecordDTO, HttpStatus.CREATED);
            } else {
                log.error("new medical record " + medicalRecordDTOToAdd + " has not been saved \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            //TOASK comment remonter le message de l'exception ?
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
            MedicalRecordDTO updatedMedicalRecordDTO = medicalRecordService.updateMedicalRecord(medicalRecordDTOToUpdate);

            if (updatedMedicalRecordDTO != null) {
                log.info("Medical record " + medicalRecordDTOToUpdate.getFirstName() + medicalRecordDTOToUpdate.getLastName() + " has been updated "
                        + " with id: " + updatedMedicalRecordDTO.getMedicalRecordId() + "\n");
                return new ResponseEntity<>(updatedMedicalRecordDTO, HttpStatus.OK);
            } else {
                log.error("Medical record " + medicalRecordDTOToUpdate + " has not been updated \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            //TOASK comment remonter le message de l'exception ?
        }

    }

}
