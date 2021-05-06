package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.FireDTO;
import com.safetynet.alerts.model.dto.FloodDTO;
import com.safetynet.alerts.model.dto.PersonCoveredDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FireStationService implements IFireStationService {

    @Autowired
    FireStationRepository fireStationRepository;

    @Autowired
    PersonRepository personRepository;

    private static final DateUtil dateUtil = new DateUtil();

    /**
     * save a list of fire stations in DB
     *
     * @param listOfFireStations list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfFireStations(List<FireStation> listOfFireStations) {
        try {
            fireStationRepository.saveAll(listOfFireStations);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("error when saving the list of fire stations in DB : " + e.getMessage() + "\n");
            return false;
        }
    }


    /**
     * allow getting the list of all fire stations found in DB
     *
     * @return a list of FireStation
     */
    @Override
    public Iterable<FireStation> getAllFireStations() {
        try {
            return fireStationRepository.findAll();
        } catch (Exception exception) {
            log.error("error when getting the list of fire stations " + exception.getMessage() + "\n");
            return null;
        }
    }


    /**
     * allow getting the list of persons for a given address with its fire station number, found in DB
     *
     * @return the fire station coverage for the address
     */
    @Override
    public FireDTO getFireStationCoverageByAddress(String address) {
        if (address != null && !address.equals("")) {
            try {
                FireDTO fireDTO = new FireDTO();

                //get the list of persons living at the address
                List<Person> listOfPersons = personRepository.findAllByAddress(address);

                //for each person, populate the list of persons covered
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the address : " + address);

                    List<PersonCoveredDTO> personCoveredDTOList = new ArrayList<>();
                    listOfPersons.forEach(person -> {
                                person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                                personCoveredDTOList.add(mapToPersonCoveredDTO(person));
                            }
                    );

                    log.info(personCoveredDTOList.size() + " persons found for address : " + address);
                    fireDTO.setPersonCoveredDTOList(personCoveredDTOList);

                } else {
                    log.warn("no person found for address " + address + ", list of emails is empty");
                }

                //get the station number of the fire station which covers this address
                //assuming there is only one fire station covering a given address
                fireDTO.setStationNumber(fireStationRepository.findByAddress(address).getStationNumber());
                return fireDTO;

            } catch (Exception exception) {
                log.error("error when getting the fire station coverage information for address " + address + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("an address must be specified to get the fire station coverage information");
            return null;
        }

    }


    /**
     * allow getting person information about people covered by fire stations
     * for a given list of station number et grouped by station number and address, found in DB
     *
     * @return the flood for the fire stations
     */
    @Override
    public List<FloodDTO> getFloodByStationNumbers(List<Integer> listOfStationNumbers) {
        if (listOfStationNumbers != null && !listOfStationNumbers.isEmpty()) {
            try {
                List<FloodDTO> listOfFloodDTO = new ArrayList<>();

                listOfStationNumbers.forEach(station -> {
                            //get the list of persons covered by the fire station
                            List<Person> listOfPersons = new ArrayList<>();
                            listOfPersons.addAll(personRepository.findAllByFireStation_StationNumber(station));

                            if (!listOfPersons.isEmpty()) {
                                log.info(listOfPersons.size() + " persons found for the stations : " + listOfStationNumbers);

                                //group persons by address
                                Map<String, List<Person>> personsGroupedByAddress = listOfPersons.stream().collect(Collectors.groupingBy(Person::getAddress));

                                //then convert to DTO
                                Map<String, List<PersonCoveredDTO>> personsCoveredDTOByAddress = new HashMap<>();
                                FloodDTO floodDTO = new FloodDTO();
                                for (String address : personsGroupedByAddress.keySet()) {
                                    List<PersonCoveredDTO> personCoveredDTOList = new ArrayList<>();
                                    for (Map.Entry<String, List<Person>> listOfPersonsForAddress : personsGroupedByAddress.entrySet()) {
                                        for (Person person : listOfPersonsForAddress.getValue()) {
                                            person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                                            personCoveredDTOList.add(mapToPersonCoveredDTO(person));
                                        }
                                    }
                                    personsCoveredDTOByAddress.put(address, personCoveredDTOList);
                                }

                                //and populate the floodDTO and add it to the list of FloodDTO
                                floodDTO.setPersonsCoveredByAddress(personsCoveredDTOByAddress);
                                floodDTO.setStationNumber(station);

                                listOfFloodDTO.add(floodDTO);

                            } else {
                                log.warn("no person found for station " + station + ", list of person information is empty for this station");
                            }
                        }
                );

                return listOfFloodDTO;

            } catch (Exception exception) {
                log.error("error when getting the fire station coverage information for stations " + listOfStationNumbers + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a list of station numbers must be specified to get the fire station coverage information");
            return null;
        }
    }


    /**
     * save a new address/fire station in the repository
     *
     * @param fireStationToAdd a new address / fire station relationship to add
     * @return the added fireStation
     */
    @Override
    public FireStation addFireStation(FireStation fireStationToAdd) {

        if (fireStationToAdd != null
                && fireStationToAdd.getStationNumber() != null
                && fireStationToAdd.getAddress() != null && !fireStationToAdd.getAddress().isEmpty()) {

            //check the address does not already exist in the repository
            if (fireStationRepository.findByAddress(fireStationToAdd.getAddress()) == null) {
                FireStation addedFireStation = fireStationRepository.save(fireStationToAdd);
                if (addedFireStation.getFireStationId()!=null){
                    log.info("new address/fire station relationship has been added");
                }else {
                    log.info("new address/fire station relationship has not been added");
                }
                return addedFireStation;
            } else {
                log.error("address: " + fireStationToAdd.getAddress() + " has already one fire station assigned");
                return null;
            }

        } else {
            log.error("all fire station information must be specified for saving");
            return null;
        }
    }


    /**
     * map the person information to the PersonCoveredDTO
     *
     * @param person person information to be mapped to PersonCoveredDTO
     * @return a PersonCoveredDTO
     */
    private PersonCoveredDTO mapToPersonCoveredDTO(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Person.class, PersonCoveredDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getMedicalRecord().getMedications(), PersonCoveredDTO::setMedications);
            mapper.map(src -> src.getMedicalRecord().getAllergies(), PersonCoveredDTO::setAllergies);
        });

        return modelMapper.map(person, PersonCoveredDTO.class);
    }
}
