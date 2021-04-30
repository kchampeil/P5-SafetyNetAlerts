package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.IMedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
