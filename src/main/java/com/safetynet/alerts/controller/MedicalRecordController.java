package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.service.IMedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MedicalRecordController {

    @Autowired
    private IMedicalRecordService medicalRecordService;


    /**
     * Read - Get all medical records
     * @return - An Iterable object of MedicalRecord full filled
     */
    @GetMapping("/medicalrecords")
    public Iterable<MedicalRecord> getAllMedicalRecords() {
        log.info("GET request on endpoint /medicalrecords received \n");
        return medicalRecordService.getAllMedicalRecords();
    }


    /**
     * Create - Post a new medical record
     *
     * @param medicalRecordToAdd to add to repository
     * @return the added MedicalRecordDTO
     */
    @PostMapping(value = "/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> addPerson(@RequestBody MedicalRecordDTO medicalRecordToAdd) {

        log.info("POST request on endpoint /medicalRecord received for person "
                + medicalRecordToAdd.getFirstName() + " " + medicalRecordToAdd.getLastName());

        try {
            MedicalRecordDTO addedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecordToAdd);

            if (addedMedicalRecord != null) {
                log.info("new person " + medicalRecordToAdd.getFirstName() + medicalRecordToAdd.getLastName() + " has been saved "
                        + " with id: " + addedMedicalRecord.getMedicalRecordId());
                return new ResponseEntity<>(addedMedicalRecord, HttpStatus.CREATED);
            } else {
                log.error("new person " + medicalRecordToAdd + " has not been saved");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            //TOASK comment remonter le message de l'exception ?
        }

    }
}
