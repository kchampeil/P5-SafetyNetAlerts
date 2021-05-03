package com.safetynet.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JsonParserService implements IFileParserService {
    
    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${data.inputFilePath}")
    private String dataInputFilePath;


    /**
     * read information in the data.json file
     */
    public void readDataFromFile() {

        // read JSON file
        log.info(" Reading JSON file "); //TTR
        try {

            InputStream jsonData = getClass().getClassLoader().getResourceAsStream(this.dataInputFilePath);

            if (jsonData != null) {
                JsonNode rootNode = objectMapper.readTree(jsonData);

                if (!rootNode.isEmpty()) {

                    //read medical records, save medical record data in DB
                    // and get back the list of medical records with their IDs in DB
                    log.info(" reading MedicalRecords in file");
                    List<MedicalRecord> listOfMedicalRecords = readMedicalRecordsFromJsonFile(rootNode);

                    if (!listOfMedicalRecords.isEmpty()) {
                        log.info(listOfMedicalRecords.size() + " medical record(s) found");
                        medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords);
                        listOfMedicalRecords = (List<MedicalRecord>) medicalRecordService.getAllMedicalRecords();
                    } else {
                        log.error("no medical record data found in file " + this.dataInputFilePath + "\n");
                    }

                    //read fire stations and save fire station data in DB
                    log.info("reading FireStations in file");
                    List<FireStation> listOfFireStations = readFireStationsFromJsonFile(rootNode);

                    if (!listOfFireStations.isEmpty()) {
                        log.info(listOfFireStations.size() + " fire station(s) found");
                        fireStationService.saveListOfFireStations(listOfFireStations);
                        listOfFireStations = (List<FireStation>) fireStationService.getAllFireStations();
                    } else {
                        log.error("no fire station data found in file " + this.dataInputFilePath + "\n");
                    }

                    //read persons in Json file
                    log.info("reading Persons in file");
                    List<Person> listOfPersons = readPersonsFromJsonFile(rootNode);

                    if (!listOfPersons.isEmpty()) {
                        log.info(listOfPersons.size() + " person(s) found");

                        // map the persons with their medical record and with their fire station
                        listOfPersons = mapMedicalRecordToPerson(listOfPersons, listOfMedicalRecords);
                        listOfPersons = mapFireStationToPerson(listOfPersons, listOfFireStations);

                        //and save person data in DB
                        personService.saveListOfPersons(listOfPersons);

                    } else {
                        log.error("no person data found in file " + this.dataInputFilePath + "\n");
                    }

                    log.info(" End of Reading JSON file "); //TTR
                } else {
                    log.error("input data file " + this.dataInputFilePath + " is empty");
                }
            } else {
                log.error("input data file " + this.dataInputFilePath + " not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * read the persons in the JSON file
     *
     * @param rootNode of the JSON file
     * @return a list of Person
     */

    private List<Person> readPersonsFromJsonFile(JsonNode rootNode) {

        List<Person> listOfPersonsInFile = new ArrayList<>();

        try {

            //identify the node "persons"
            JsonNode personNode = rootNode.path("persons");

            //get all values of persons in the List listOfPersonsInFile
            listOfPersonsInFile = objectMapper.readValue(personNode.toString(), new TypeReference<List<Person>>() {
            });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return listOfPersonsInFile;
    }


    /**
     * read the fire stations in the JSON file
     *
     * @param rootNode of the JSON file
     * @return a list of FireStation
     */
    private List<FireStation> readFireStationsFromJsonFile(JsonNode rootNode) {

        List<FireStation> listOfFireStationsInFile = new ArrayList<>();

        try {

            //identify the node "firestations"
            JsonNode fireStationNode = rootNode.path("firestations");

            //get all values of persons in the List listOfFireStationsInFile
            listOfFireStationsInFile = objectMapper
                    .readValue(fireStationNode.toString(), new TypeReference<List<FireStation>>() {
                    });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return listOfFireStationsInFile;
    }


    /**
     * read the medical records in the JSON file
     *
     * @param rootNode of the JSON file
     * @return a list of MedicalRecord
     */
    private List<MedicalRecord> readMedicalRecordsFromJsonFile(JsonNode rootNode) {

        List<MedicalRecord> listOfMedicalRecordsInFile = new ArrayList<>();

        try {

            //identify the node "medicalrecords"
            JsonNode medicalRecordNode = rootNode.path("medicalrecords");

            //get all values of persons in the List listOfMedicalRecordsInFile
            listOfMedicalRecordsInFile = objectMapper
                    .readValue(medicalRecordNode.toString(), new TypeReference<List<MedicalRecord>>() {
                    });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return listOfMedicalRecordsInFile;
    }


    /**
     * for each Person in a list of persons, get the relative Medical Record in the given list of medical records
     *
     * @param listOfPersons        list of persons
     * @param listOfMedicalRecords list of all medical records
     * @return listOfPersons populated with relative medical records
     */
    private List<Person> mapMedicalRecordToPerson(List<Person> listOfPersons, List<MedicalRecord> listOfMedicalRecords) {
        for (Person person : listOfPersons) {
            for (MedicalRecord medicalRecord : listOfMedicalRecords) {
                if (person.getFirstName().equals(medicalRecord.getFirstName())
                        && person.getLastName().equals(medicalRecord.getLastName())) {
                    person.setMedicalRecord(medicalRecord);
                    break;
                }
            }
            if (person.getMedicalRecord() == null) {
                log.warn("no medical record found for "
                        + person.getFirstName() + " " + person.getLastName());
            }
        }
        return listOfPersons;
    }


    /**
     * for each Person in a list of persons,
     * get the Fire Station they are attached to in the given list of fire stations
     *
     * @param listOfPersons      list of persons
     * @param listOfFireStations list of all fire stations
     * @return listOfPersons populated with the fire station they are attached to
     */
    private List<Person> mapFireStationToPerson(List<Person> listOfPersons, List<FireStation> listOfFireStations) {
        for (Person person : listOfPersons) {
            for (FireStation fireStation : listOfFireStations) {
                if (person.getAddress().equals(fireStation.getAddress())) {
                    person.setFireStation(fireStation);
                    break;
                }
            }
            if (person.getAddress() == null) {
                log.warn("no fire station found for "
                        + person.getFirstName() + " " + person.getLastName()
                        + " at " + person.getAddress());
            }
        }
        return listOfPersons;
    }

}
