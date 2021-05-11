package com.safetynet.alerts.controller;

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
                        + " with id: " + addedMedicalRecord.getMedicalRecordId() + "\n");
                return new ResponseEntity<>(addedMedicalRecord, HttpStatus.CREATED);
            } else {
                log.error("new person " + medicalRecordToAdd + " has not been saved \n");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            //TOASK comment remonter le message de l'exception ?
        }

    }
}
