package com.safetynet.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonParserService implements IFileParserService {

    private static final Logger logger = LogManager.getLogger(JsonParserService.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    /*TOASK mettre objectMapper plutôt dans le contexte que de le déclarer ensuite ?
    @Autowired
    private ObjectMapper objectMapper;

     */

    @Value("${data.inputFilePath}")
    private String dataInputFilePath;


    /**
     * read information in the data.json file
     */
    public void readDataFromFile() {

        // read JSON file
        System.out.println("=== Reading JSON file ==="); //TTR
        try {

            InputStream jsonData = getClass().getClassLoader().getResourceAsStream(this.dataInputFilePath);

            if (jsonData != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonData);

                if (!rootNode.isEmpty()) {

                    //read medical records, save medical record data in DB
                    // and get back the list of medical records with their IDs in DB
                    logger.info("\n \n ***** Beginning of reading MedicalRecords in file *****");
                    List<MedicalRecord> listOfMedicalRecords = readMedicalRecordsFromJsonFile(rootNode);
                    logger.info("***** End of reading MedicalRecords in file *****");

                    if (listOfMedicalRecords != null && !listOfMedicalRecords.isEmpty()) {
                        medicalRecordService.saveListOfMedicalRecords(listOfMedicalRecords);
                        listOfMedicalRecords = (List<MedicalRecord>) medicalRecordService.getAllMedicalRecords();
                    } else {
                        logger.error("no medical record data found in file " + this.dataInputFilePath + "\n");
                    }

                    //read fire stations and save fire station data in DB
                    logger.info("\n \n ***** Beginning of reading FireStations in file *****");
                    List<FireStation> listOfFireStations = readFireStationsFromJsonFile(rootNode);
                    logger.info("***** End of reading FireStations in file *****");

                    if (listOfFireStations != null && !listOfFireStations.isEmpty()) {
                        fireStationService.saveListOfFireStations(listOfFireStations);
                    } else {
                        logger.error("no fire station data found in file " + this.dataInputFilePath + "\n");
                    }

                    //read persons in Json file
                    logger.info("\n \n ***** Beginning of reading Persons in file *****");
                    List<Person> listOfPersons = readPersonsFromJsonFile(rootNode);
                    logger.info("***** End of reading Persons in file *****");

                    if (listOfPersons != null && !listOfPersons.isEmpty()) {
                        // map the persons with their medical record and with their fire station
                        listOfPersons = mapMedicalRecordToPerson(listOfPersons, listOfMedicalRecords);
                        /*listOfPersons = mapFireStationToPerson(listOfPersons, listOfFireStations);

                         */

                        //and save person data in DB
                        personService.saveListOfPersons(listOfPersons);

                    } else {
                        logger.error("no person data found in file " + this.dataInputFilePath + "\n");
                    }

                    System.out.println("\n === End of Reading JSON file ===\n\n"); //TTR
                } else {
                    logger.error("input data file " + this.dataInputFilePath + " is empty");
                }
            } else {
                logger.error("input data file " + this.dataInputFilePath + " not found");
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

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //identify the node "persons"
            JsonNode personNode = rootNode.path("persons");

            //get all values of persons in the List listOfPersonsInFile
            listOfPersonsInFile = objectMapper.readValue(personNode.toString(), new TypeReference<List<Person>>() {
            });

            //TTR
            System.out.println("\n ---------- first person read in file ----------");
            System.out.println("getFirstName : " + listOfPersonsInFile.get(0).getFirstName());
            System.out.println("getLastName : " + listOfPersonsInFile.get(0).getLastName());
            System.out.println("getEmail : " + listOfPersonsInFile.get(0).getEmail());
            System.out.println("getAddress : " + listOfPersonsInFile.get(0).getAddress());
            System.out.println("getZip : " + listOfPersonsInFile.get(0).getZip());
            System.out.println("getCity : " + listOfPersonsInFile.get(0).getCity());
            System.out.println("getPhone : " + listOfPersonsInFile.get(0).getPhone());

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
    private List<FireStation> readFireStationsFromJsonFile(JsonNode
                                                                   rootNode) {

        List<FireStation> listOfFireStationsInFile = new ArrayList<>();

        try {

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //identify the node "firestations"
            JsonNode fireStationNode = rootNode.path("firestations");

            //get all values of persons in the List listOfFireStationsInFile
            listOfFireStationsInFile = objectMapper.readValue(fireStationNode.toString(), new TypeReference<List<FireStation>>() {
            });

            //TTR
            System.out.println("\n ---------- first fire station read in file ----------");
            System.out.println("getAddress : " + listOfFireStationsInFile.get(0).getAddress());
            System.out.println("getStationNumber : " + listOfFireStationsInFile.get(0).getStationNumber());

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
    private List<MedicalRecord> readMedicalRecordsFromJsonFile(JsonNode
                                                                       rootNode) {

        List<MedicalRecord> listOfMedicalRecordsInFile = new ArrayList<>();

        try {

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //identify the node "medicalrecords"
            JsonNode medicalRecordNode = rootNode.path("medicalrecords");

            //get all values of persons in the List listOfMedicalRecordsInFile
            listOfMedicalRecordsInFile = objectMapper.readValue(medicalRecordNode.toString(), new TypeReference<List<MedicalRecord>>() {
            });

            //TTR
            System.out.println("\n ---------- first medical record in file ----------");
            System.out.println("getFirstName : " + listOfMedicalRecordsInFile.get(0).getFirstName());
            System.out.println("getLastName : " + listOfMedicalRecordsInFile.get(0).getLastName());
            System.out.println("getBirthDate : " + listOfMedicalRecordsInFile.get(0).getBirthDate());
            System.out.println("getMedications : " + listOfMedicalRecordsInFile.get(0).getMedications());
            System.out.println("getAllergies : " + listOfMedicalRecordsInFile.get(0).getAllergies());
            System.out.println("1er medication : " + listOfMedicalRecordsInFile.get(0).getMedications().get(0));

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
                logger.warn("no medical record found for "
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
    /*private List<Person> mapFireStationToPerson(List<Person> listOfPersons, List<FireStation> listOfFireStations) {
        for (Person person : listOfPersons) {
            for (FireStation fireStation : listOfFireStations) {
                if (person.getAddress().equals(fireStation.getAddress())) {
                    person.setFireStation(fireStation);
                    break;
                }
            }
            if (person.getAddress() == null) {
                logger.warn("no fire station found for "
                        + person.getFirstName() + " " + person.getLastName()
                        + " at " + person.getAddress());
            }
        }
        return listOfPersons;
    }

     */
}
