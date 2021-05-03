package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.HouseholdMemberDTO;
import com.safetynet.alerts.model.dto.PersonCoveredContactsDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetynet.alerts.constants.ChildAlertConstants.MAX_AGE_FOR_CHILD_ALERT;

@Slf4j
@Service
public class PersonService implements IPersonService {

    private static final DateUtil dateUtil = new DateUtil();

    @Autowired
    private PersonRepository personRepository;

    /**
     * save a list of persons in DB
     *
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfPersons(List<Person> listOfPersons) {
        try {
            personRepository.saveAll(listOfPersons);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("error when saving the list of persons in DB : " + illegalArgumentException.getMessage() + "\n");
            return false;
        }
    }


    /**
     * allow getting the list of all persons found in DB
     *
     * @return a list of Person
     */
    @Override
    public Iterable<Person> getAllPersons() {
        try {
            return personRepository.findAll();
        } catch (Exception exception) {
            log.error("error when getting the list of all persons " + exception.getMessage() + "\n");
            return null;
        }
    }


    /**
     * allow getting the list of all citizens' emails for a given city found in DB
     *
     * @param cityName of the city we want citizens' emails
     * @return a list of emails
     */
    @Override
    public List<String> getAllEmailsByCity(String cityName) {
        if (cityName != null && !cityName.equals("")) {
            try {
                List<String> listOfEmails = new ArrayList<>();

                //get the list of persons living in the city called cityName
                List<Person> listOfPersons = personRepository.findAllByCity(cityName);

                //for each person, if listOfEmails does not already contains his email, add the email in the list
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the city : " + cityName);
                    for (Person person : listOfPersons) {
                        if (!listOfEmails.contains(person.getEmail())) {
                            listOfEmails.add(person.getEmail());
                        }
                    }
                    log.info(listOfEmails.size() + " distinct emails found for the city : " + cityName);
                } else {
                    log.warn("no person found for city " + cityName + ", list of emails is empty");
                }
                return listOfEmails;

            } catch (Exception exception) {
                log.error("error when getting the list of emails for city " + cityName + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a city name must be specified to get the list of emails");
            return null;
        }
    }


    /**
     * allow getting the list of person information found in repository
     * for given firstname and lastname
     *
     * @param firstName the firstname we want to get the person information from
     * @param lastName  the lastname we want to get the person information from
     * @return a list of person information
     */
    @Override
    public List<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(String firstName, String lastName) {
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {
            try {
                //get the list of persons with firstName and lastName
                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(firstName, lastName);

                // complete information with calculation of their age
                // and populate the listOfPersonInfoDTO
                List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    listOfPersons.forEach(person -> {
                        person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                        listOfPersonInfoDTO.add(mapToPersonInfoDTO(person));
                    });

                } else {
                    log.error("no person found for firstname " + firstName +
                            " and lastname " + lastName +
                            ", list of person information is empty");
                }
                return listOfPersonInfoDTO;

            } catch (Exception exception) {
                log.error("error when getting the list of person information " +
                        "for firstname " + firstName +
                        " and lastname " + lastName + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("firstname AND lastname must be specified to get the list of person information");
            return null;
        }
    }

    /**
     * map the person information to the PersonInfoDTO
     *
     * @param person person information to be mapped to personInfoDTO
     * @return a PersonInfoDTO
     */
    private PersonInfoDTO mapToPersonInfoDTO(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Person.class, PersonInfoDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getMedicalRecord().getMedications(), PersonInfoDTO::setMedications);
            mapper.map(src -> src.getMedicalRecord().getAllergies(), PersonInfoDTO::setAllergies);
        });

        return modelMapper.map(person, PersonInfoDTO.class);
    }


    /**
     * allow getting the list of child alert found in repository for given address
     *
     * @param address the address we want to get the child alert from
     * @return a list of child alert
     */
    @Override
    public List<ChildAlertDTO> getChildAlertByAddress(String address) {
        if (address != null && !address.equals("")) {
            try {
                //get the list of persons living at this address
                List<Person> listOfPersons = personRepository.findAllByAddress(address);

                List<ChildAlertDTO> listOfChildAlertDTO = new ArrayList<>();

                if (listOfPersons != null && !listOfPersons.isEmpty()) {

                    // complete information with calculation of their age
                    // and filter on age under the MAX_AGE_FOR_CHILD_ALERT
                    listOfPersons.forEach(person ->
                            person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate())));
                    List<Person> listOfChildren = listOfPersons.stream()
                            .filter(person -> person.getAge() <= MAX_AGE_FOR_CHILD_ALERT)
                            .collect(Collectors.toList());

                    // if at least one person is under the MAX_AGE_FOR_CHILD_ALERT, populate the listOfChildAlertDTO
                    if (!listOfChildren.isEmpty()) {
                        listOfChildren.forEach(child
                                -> listOfChildAlertDTO.add(mapToChildrenAlertDTO(child, listOfPersons)));
                    } else {
                        log.info("no child under " + MAX_AGE_FOR_CHILD_ALERT + " found for address " + address +
                                ", list of child alert is empty");
                    }

                } else {
                    log.error("no person found for address " + address +
                            ", list of child alert is empty");
                }
                return listOfChildAlertDTO;

            } catch (Exception exception) {
                log.error("error when getting the list of person information " +
                        "for address " + address + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("address must be specified to get the list of child alert");
            return null;
        }
    }

    /**
     * map the child information to the ChildAlertDTO
     *
     * @param child         person information to be mapped to childAlertDTO
     * @param listOfPersons list of person living at the address
     * @return a ChildAlertDTO
     */
    private ChildAlertDTO mapToChildrenAlertDTO(Person child, List<Person> listOfPersons) {
        ModelMapper modelMapper = new ModelMapper();

        ChildAlertDTO childAlertDTO = modelMapper.map(child, ChildAlertDTO.class);
        childAlertDTO.setListOfOtherHouseholdMembers(
                getListOfOtherHouseholdMembers(listOfPersons, child.getFirstName(), child.getLastName()));

        return childAlertDTO;
    }

    /**
     * return the list of household members for a given lastname
     * (assuming all household members have the same lastname...)
     *
     * @param listOfPersons a list of persons
     * @param lastName      the lastname we want to filter on
     * @return a list of persons having the same given lastname
     */
    private List<HouseholdMemberDTO> getListOfOtherHouseholdMembers(List<Person> listOfPersons, String firstName, String lastName) {

        List<Person> filteredListOfPersons = listOfPersons.stream()
                .filter(person -> person.getLastName().equals(lastName) && !person.getFirstName().equals(firstName))
                .collect(Collectors.toList());

        List<HouseholdMemberDTO> listOfHouseholdMemberDTO = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        filteredListOfPersons.forEach(person -> {
            HouseholdMemberDTO householdMemberDTO = modelMapper.map(person, HouseholdMemberDTO.class);
            listOfHouseholdMemberDTO.add(householdMemberDTO);
        });

        return listOfHouseholdMemberDTO;
    }


    /**
     * allow getting the list of all phone numbers for citizens
     * covered by a given fire station found in repository
     *
     * @param stationNumber the fire station number we want to get the citizen' phone numbers from
     * @return a list of phone numbers
     */
    @Override
    public List<String> getPhoneAlertByFireStation(Integer stationNumber) {
        if (stationNumber != null) {
            try {
                List<String> listOfPhoneNumbers = new ArrayList<>();

                //get the list of persons living in the area of the fire station
                List<Person> listOfPersons = personRepository.findAllByFireStation_StationNumber(stationNumber);

                //for each person, if listOfPhoneNumbers does not already contains this phone number,
                // add the phone number in the list
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the area covered by fire station n°: " + stationNumber);
                    for (Person person : listOfPersons) {
                        if (!listOfPhoneNumbers.contains(person.getPhone())) {
                            listOfPhoneNumbers.add(person.getPhone());
                        }
                    }
                    log.info(listOfPhoneNumbers.size()
                            + " distinct phone numbers found for the area covered by fire station n°: " + stationNumber);
                } else {
                    log.warn("no person found in the area covered by fire station n°: "
                            + stationNumber
                            + ", list of phone numbers is empty");
                }
                return listOfPhoneNumbers;


            } catch (Exception exception) {
                log.error("error when getting the list of phone numbers for the area covered by fire station n°: "
                        + stationNumber + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a fire station number must be specified to get the list of phone numbers");
            return null;
        }
    }

    /**
     * allow getting the list of information of all citizens
     * covered by a given fire station found in repository
     * completed with a count of adults and children
     *
     * @param stationNumber the fire station number we want to get the citizen' information from
     * @return a list of information of all citizens covered by a given fire station found in repository
     * completed with a count of adults and children
     */
    @Override
    public FireStationCoverageDTO getFireStationCoverageByStationNumber(Integer stationNumber) {
        if (stationNumber != null) {
            try {
                FireStationCoverageDTO fireStationCoverageDTO = new FireStationCoverageDTO();
                int numberOfAdults = 0;
                int numberOfChildren = 0;

                //get the list of persons living in the area of the fire station
                List<Person> listOfPersons = personRepository.findAllByFireStation_StationNumber(stationNumber);

                //for each person, contact information are added in the list (after mapping)
                //and count of adults/children is incremented
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.info(listOfPersons.size() + " persons found for the area covered by fire station n°: " + stationNumber);

                    List<PersonCoveredContactsDTO> listOfPersonCoveredContactsDTO = new ArrayList<>();

                    for (Person person : listOfPersons) {
                        ModelMapper modelMapper = new ModelMapper();
                        PersonCoveredContactsDTO personCoveredContactsDTO = modelMapper.map(person, PersonCoveredContactsDTO.class);
                        listOfPersonCoveredContactsDTO.add(personCoveredContactsDTO);

                        if (dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()) <= MAX_AGE_FOR_CHILD_ALERT) {
                            numberOfChildren++;
                        } else {
                            numberOfAdults++;
                        }
                    }

                    fireStationCoverageDTO.setPersonCoveredContactsDTOList(listOfPersonCoveredContactsDTO);
                    fireStationCoverageDTO.setNumberOfAdults(numberOfAdults);
                    fireStationCoverageDTO.setNumberOfChildren(numberOfChildren);
                    log.info(listOfPersonCoveredContactsDTO.size()
                            + " persons found for the area covered by fire station n°: " + stationNumber
                            + " of which " + numberOfAdults + " adults and " + numberOfChildren + " children");
                } else {
                    log.warn("no person found in the area covered by fire station n°: "
                            + stationNumber
                            + ", list of person information is empty");
                }
                return fireStationCoverageDTO;


            } catch (Exception exception) {
                log.error("error when getting the list of person information for the area covered by fire station n°: "
                        + stationNumber + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a fire station number must be specified to get the list of person information");
            return null;
        }
    }

}
