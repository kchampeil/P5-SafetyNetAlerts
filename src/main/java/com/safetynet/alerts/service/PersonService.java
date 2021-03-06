package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.ExceptionConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.ChildAlertDTO;
import com.safetynet.alerts.model.dto.FireStationCoverageDTO;
import com.safetynet.alerts.model.dto.HouseholdMemberDTO;
import com.safetynet.alerts.model.dto.PersonCoveredContactsDTO;
import com.safetynet.alerts.model.dto.PersonDTO;
import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.safetynet.alerts.constants.ChildAlertConstants.MAX_AGE_FOR_CHILD_ALERT;

@Slf4j
@Service
public class PersonService implements IPersonService {

    private static final DateUtil dateUtil = new DateUtil();

    private final PersonRepository personRepository;
    private final FireStationRepository fireStationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, FireStationRepository fireStationRepository, MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.fireStationRepository = fireStationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * save a list of persons in DB
     *
     * @param listOfPersons list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public Iterable<Person> saveListOfPersons(List<Person> listOfPersons) {
        try {
            return personRepository.saveAll(listOfPersons);
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("error when saving the list of persons in DB : " + illegalArgumentException.getMessage() + "\n");
            return null;
        }
    }


    /**
     * allow getting the list of all persons found in DB
     *
     * @return a list of Person
     */
    @Override
    public Iterable<PersonDTO> getAllPersons() {
        List<PersonDTO> listOfPersonsDTO = new ArrayList<>();

        try {
            Iterable<Person> listOfPersons = personRepository.findAll();
            listOfPersons.forEach(person ->
                    listOfPersonsDTO
                            .add(mapPersonToPersonDTO(person)));
        } catch (Exception exception) {
            log.error("error when getting the list of all persons " + exception.getMessage() + "\n");
        }

        return listOfPersonsDTO;
    }

    /**
     * map the Person object to the PersonDTO object
     *
     * @param person Person object to be mapped to PersonDTO
     * @return a PersonDTO
     */
    private PersonDTO mapPersonToPersonDTO(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(person, PersonDTO.class);
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
                //get the list of persons with the same firstName AND lastName
                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(firstName, lastName);

                if (listOfPersons == null) {
                    log.info("No person found with exactly the same firstname and lastname");
                    listOfPersons = new ArrayList<>();
                }

                //get the list of persons with the same lastName (but different firsName)
                // and add them all to the list of persons
                List<Person> listOfPersonsWithSameLastName = personRepository.findAllByFirstNameNotAndLastName(firstName, lastName);
                listOfPersons.addAll(listOfPersonsWithSameLastName);

                // complete information with calculation of their age
                // and populate the listOfPersonInfoDTO
                List<PersonInfoDTO> listOfPersonInfoDTO = new ArrayList<>();

                if (!listOfPersons.isEmpty()) {
                    listOfPersons.forEach(person -> {
                        person.setAge(dateUtil.calculateAge(person.getMedicalRecord().getBirthDate()));
                        listOfPersonInfoDTO.add(mapPersonToPersonInfoDTO(person));
                    });

                } else {
                    log.warn("No person found for firstname " + firstName +
                            " and lastname " + lastName + ", list of person information is empty");
                }
                return listOfPersonInfoDTO;

            } catch (Exception exception) {
                log.error("error when getting the list of person information " +
                        "for firstname " + firstName +
                        " and lastname " + lastName + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("Firstname AND lastname must be specified to get the list of person information");
            return null;
        }
    }

    /**
     * map the person information to the PersonInfoDTO
     *
     * @param person person information to be mapped to personInfoDTO
     * @return a PersonInfoDTO
     */
    private PersonInfoDTO mapPersonToPersonInfoDTO(Person person) {
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
                    log.warn("no person found for address " + address +
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
                    log.info(listOfPersons.size() + " persons found for the area covered by fire station n??: " + stationNumber);
                    for (Person person : listOfPersons) {
                        if (!listOfPhoneNumbers.contains(person.getPhone())) {
                            listOfPhoneNumbers.add(person.getPhone());
                        }
                    }
                    log.info(listOfPhoneNumbers.size()
                            + " distinct phone numbers found for the area covered by fire station n??: " + stationNumber);
                } else {
                    log.warn("no person found in the area covered by fire station n??: "
                            + stationNumber
                            + ", list of phone numbers is empty");
                }
                return listOfPhoneNumbers;


            } catch (Exception exception) {
                log.error("error when getting the list of phone numbers for the area covered by fire station n??: "
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
            FireStationCoverageDTO fireStationCoverageDTO = new FireStationCoverageDTO();
            List<PersonCoveredContactsDTO> listOfPersonCoveredContactsDTO = new ArrayList<>();
            int numberOfAdults = 0;
            int numberOfChildren = 0;
            try {
                //get the list of persons living in the area of the fire station
                List<Person> listOfPersons = personRepository.findAllByFireStation_StationNumber(stationNumber);

                //for each person, contact information are added in the list (after mapping)
                //and count of adults/children is incremented
                if (listOfPersons != null && !listOfPersons.isEmpty()) {
                    log.debug(listOfPersons.size() + " persons found for the area covered by fire station n??: " + stationNumber);

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
                    log.info(listOfPersonCoveredContactsDTO.size()
                            + " persons found for the area covered by fire station n??: " + stationNumber
                            + " of which " + numberOfAdults + " adults and " + numberOfChildren + " children");
                } else {
                    log.warn("no person found in the area covered by fire station n??: "
                            + stationNumber
                            + ", list of person information is empty");
                }
                fireStationCoverageDTO.setPersonCoveredContactsDTOList(listOfPersonCoveredContactsDTO);
                fireStationCoverageDTO.setNumberOfAdults(numberOfAdults);
                fireStationCoverageDTO.setNumberOfChildren(numberOfChildren);
                return fireStationCoverageDTO;

            } catch (Exception exception) {
                log.error("error when getting the list of person information for the area covered by fire station n??: "
                        + stationNumber + " : " + exception.getMessage());
                return null;
            }
        } else {
            log.error("a fire station number must be specified to get the list of person information");
            return null;
        }
    }


    /**
     * save a new person in the repository
     *
     * @param personDTOToAdd a new person to add
     * @return the added Person
     * @throws AlreadyExistsException      if the person to add already exists in repository
     * @throws MissingInformationException if there are missing properties for the person to save
     */
    @Override
    public Optional<PersonDTO> addPerson(PersonDTO personDTOToAdd) throws AlreadyExistsException, MissingInformationException {

        Optional<PersonDTO> addedPersonDTO;

        //check if the personDTOToAdd is correctly filled
        if (checkPersonDTO(personDTOToAdd)) {

            //check if the person does not already exist in the repository
            List<Person> listOfPersons =
                    personRepository.findAllByFirstNameAndLastName(personDTOToAdd.getFirstName(), personDTOToAdd.getLastName());

            if (listOfPersons.size() == 0) {
                //map DTO to DAO, add the covering fire station if exists,
                //save in repository and map back to PersonDTO for return
                ModelMapper modelMapper = new ModelMapper();
                Person personToAdd = modelMapper.map(personDTOToAdd, Person.class);

                personToAdd.setFireStation(fireStationRepository.findByAddress(personToAdd.getAddress()));

                Person addedPerson = personRepository.save(personToAdd);

                addedPersonDTO = Optional.ofNullable(modelMapper.map(addedPerson, PersonDTO.class));

            } else {
                throw new AlreadyExistsException(ExceptionConstants.ALREADY_EXIST_PERSON_FOR_FIRSTNAME_AND_LASTNAME
                        + personDTOToAdd.getFirstName() + " " + personDTOToAdd.getLastName());
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING);
        }

        return addedPersonDTO;
    }


    /**
     * update a person of a given firstname+lastname
     *
     * @param personDTOToUpdate a person to update
     * @return the updated person
     * @throws DoesNotExistException       if the person to update does not exist in repository
     * @throws MissingInformationException if there are missing properties for the person to update
     */
    @Override
    public Optional<PersonDTO> updatePerson(PersonDTO personDTOToUpdate) throws DoesNotExistException, MissingInformationException {
        Optional<PersonDTO> updatedPersonDTO;

        //check if the personDTOToUpdate is correctly filled
        if (checkPersonDTO(personDTOToUpdate)) {

            //check if the person exists in the repository
            Person existingPerson = personRepository
                    .findByFirstNameAndLastName(personDTOToUpdate.getFirstName(), personDTOToUpdate.getLastName());

            if (existingPerson != null) {
                //map DTO to DAO, save in repository and map back to PersonDTO for return
                ModelMapper modelMapper = new ModelMapper();
                Person personToUpdate = modelMapper.map(personDTOToUpdate, Person.class);
                personToUpdate.setPersonId(existingPerson.getPersonId());
                personToUpdate.setMedicalRecord(existingPerson.getMedicalRecord());

                //if address is updated, search and assign the fire station covering the new address
                if (!personToUpdate.getAddress().equals(existingPerson.getAddress())) {
                    personToUpdate.setFireStation(fireStationRepository.findByAddress(personToUpdate.getAddress()));
                }

                Person updatedPerson = personRepository.save(personToUpdate);
                updatedPersonDTO = Optional.ofNullable(modelMapper.map(updatedPerson, PersonDTO.class));

            } else {
                throw new DoesNotExistException(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                        + personDTOToUpdate.getFirstName() + " " + personDTOToUpdate.getLastName());
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING);
        }

        return updatedPersonDTO;
    }


    /**
     * delete the person for the given firstname+lastname in the repository
     *
     * @param firstName the firstname of the person we want to delete
     * @param lastName  the lastname of the person we want to delete
     * @return the deleted person
     * @throws DoesNotExistException       if no person has been found for the given firstname+lastname
     * @throws MissingInformationException if no firstname+lastname has been given
     */
    @Override
    public Person deletePersonByFirstNameAndLastName(String firstName, String lastName) throws DoesNotExistException, MissingInformationException {
        Person personToDelete;

        //check if the firstname+lastname is correctly filled
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {

            //check if there is a person associated to this firstname+lastname in the repository
            personToDelete = personRepository.findByFirstNameAndLastName(firstName, lastName);

            if (personToDelete != null) {

                //first dissociate medical record from person and delete the medical record
                if (personToDelete.getMedicalRecord().getMedicalRecordId() != null) {
                    Long idOfMedicalRecordToDelete = personToDelete.getMedicalRecord().getMedicalRecordId();
                    personToDelete.setMedicalRecord(null);
                    personRepository.save(personToDelete);
                    medicalRecordRepository.deleteById(idOfMedicalRecordToDelete);
                }

                //then delete the person
                personRepository.deleteById(personToDelete.getPersonId());

            } else {
                throw new DoesNotExistException(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                        + firstName + " " + lastName);
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_PERSON_WHEN_DELETING);
        }

        return personToDelete;
    }


    /**
     * check if personDTO input is correct
     *
     * @param personDTO personDTO information to be checked
     * @return true if correct
     */
    private boolean checkPersonDTO(PersonDTO personDTO) {
        return personDTO.getFirstName() != null && !personDTO.getFirstName().isEmpty()
                && personDTO.getLastName() != null && !personDTO.getLastName().isEmpty()
                && personDTO.getAddress() != null && !personDTO.getAddress().isEmpty()
                && personDTO.getCity() != null && !personDTO.getCity().isEmpty()
                && personDTO.getZip() != null && !personDTO.getZip().isEmpty()
                && personDTO.getEmail() != null && !personDTO.getEmail().isEmpty()
                && personDTO.getPhone() != null && !personDTO.getPhone().isEmpty();
    }

}
