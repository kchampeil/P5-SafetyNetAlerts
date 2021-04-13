package com.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class JsonParserService {

    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Value("${data.inputFilePath}")
    private String dataInputFilePath;

    /**
     * read information in the data.json file
     */
    public void readDataFromJsonFile() {


// IN-PROGRESS

        // read JSON file
        System.out.println("---------------- Reading JSON file --------------------"); //TTR
        try {

            //JSONParser jsonParser = new JSONParser();
            //create JsonParser object
            //TTR celui là ne fonctionne pas
            //TTR celui là ne fonctionne pas-fin

            //TTR ceux-là fonctionnent
            InputStream jsonData = getClass().getClassLoader().getResourceAsStream(this.dataInputFilePath);
            //InputStream jsonData = JsonParserService.class.getClassLoader().getResourceAsStream(this.dataInputFilePath);
            //read json file data to String
            //byte[] jsonData = Files.readAllBytes(Paths.get(this.dataInputFilePath));
            //TTR ceux-là fonctionnent-fin

            //JsonParser jsonParser = new JsonFactory().createParser(fileInputStream);

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            /*TTR celui là ne fonctionne pas
            List<Person> listOfPersons = objectMapper.readValue(jsonData, new TypeReference<List<Person>>() {});
            //List<Person> listOfPersons = objectMapper.readValue(jsonData, List.class);
            System.out.println("List using TypeReference: " + listOfPersons);
            TTR celui là ne fonctionne pas */

            //TTR celui là fonctionne avec byte[] jsonData
            JsonNode rootNode = objectMapper.readTree(jsonData);
            System.out.println(rootNode.getNodeType()); //TTR

            JsonNode personNode = rootNode.path("persons");
            System.out.println(personNode.getNodeType()); //TTR

            Iterator<JsonNode> elements = personNode.elements();

            while (elements.hasNext()) {
                JsonNode person = elements.next();
                System.out.println("person = " + person);
            }
            //TTR celui là fonctionne-fin

            System.out.println("---------------- End of Reading JSON file --------------------"); //TTR

 /*       } catch (JsonParseException jsonParseException) {
            System.out.println(jsonParseException);
 */
        } catch (IOException ioException) {
            //TODO gestion de l'exception à revoir
            System.out.println(ioException);


        }

        //InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("data.json");
                            /*FileReader fileReader = new FileReader("C:\\Git\\P5-SafetyNetAlertsAPI\\src\\main\\resources\\data.json");
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

                            JSONArray jsonArray = (JSONArray) jsonObject.get("persons");
                            Iterator i = jsonArray.iterator();

                            ObjectMapper objectMapper = new ObjectMapper();
                            while (i.hasNext()) {
                                System.out.println(" " + i.next());
                            }*/
        //person.setZip(objectMapper.readValue(jsonArray));

                        /*Person person = new Person();
                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            String name = jsonParser.getCurrentName();
                            if ("zip".equals(name)) {
                                jsonParser.nextToken();
                                person.setZip(jsonParser.getValueAsString());
                                System.out.println(person.getZip());
                            } else if ("address".equals(name)) {
                                jsonParser.nextToken();
                                person.setAddress(jsonParser.getValueAsString());
                                System.out.println(person.getAddress());
                            } else if ("email".equals(name)) {
                                jsonParser.nextToken();
                                person.setEmail(jsonParser.getValueAsString());
                                System.out.println(person.getEmail());
                            }*/
        //Person person = objectMapper.readValue(fileInputStream, Person.class);

            /*for (int i=0;i<array.length();i++)
            {
                String firstName = array.getJSONObject(i).getString("firstName");
                String lastName = array.getJSONObject(i).getString("lastName");

            }*/

        //create JsonParser object
        //JsonParser jsonParser = new JsonFactory().createParser(new File("data.json"));

        //TODO close à revoir
        // jsonParser.close();


        //create ObjectMapper instance
        //ObjectMapper objectMapper = new ObjectMapper();

        //convert json string to object
        //Person person = objectMapper.readValue(jsonData, Person.class);

        //read persons
        //List<Person> listOfPersons = readPersonsFromJsonFile();

        //read fire stations
        //List<FireStation> listOfFireStations = readFireStationsFromJsonFile();
        //read medical records
        //List<MedicalRecord> listOfMedicalRecords = readMedicalRecordsFromJsonFile();


    }

    /**
     * read the persons in the JSON file
     *
     * @return a list of Person
     */
    private List<Person> readPersonsFromJsonFile() {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //Person testPerson = objectMapper.readValue();
        List<Person> listOfPersonsInFile = new ArrayList<>();
        return listOfPersonsInFile;
    }

    /**
     * read the fire stations in the JSON file
     *
     * @return a list of FireStation
     */
    private List<FireStation> readFireStationsFromJsonFile() {
        List<FireStation> listOfFireStationsInFile = new ArrayList<>();
        return listOfFireStationsInFile;
    }

    /**
     * read the medical records in the JSON file
     *
     * @return a list of MedicalRecord
     */
    private List<MedicalRecord> readMedicalRecordsFromJsonFile() {
        List<MedicalRecord> listOfMedicalRecordsInFile = new ArrayList<>();
        return listOfMedicalRecordsInFile;
    }
}


