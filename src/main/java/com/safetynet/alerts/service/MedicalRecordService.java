package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.MissingInformationException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.MedicalRecordDTO;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MedicalRecordService implements IMedicalRecordService {

    private static final DateUtil dateUtil = new DateUtil();

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     * save a list of medical records in DB
     *
     * @param listOfMedicalRecords list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public boolean saveListOfMedicalRecords(List<MedicalRecord> listOfMedicalRecords) {
        try {
            medicalRecordRepository.saveAll(listOfMedicalRecords);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("error when saving the list of medical records in DB : " + e.getMessage() + "\n");
            return false;
        }
    }


    /**
     * allow getting the list of all medical records found in DB
     *
     * @return a list of MedicalRecord
     */
    @Override
    public Iterable<MedicalRecord> getAllMedicalRecords() {
        try {
            return medicalRecordRepository.findAll();
        } catch (Exception exception) {
            log.error("error when getting the list of medical records " + exception.getMessage() + "\n");
            return null;
        }
    }


    /**
     * save a new medical record in the repository
     *
     * @param medicalRecordDTOToAdd a new medical record to add
     * @return the added medical record
     */
    @Override
    public MedicalRecordDTO addMedicalRecord(MedicalRecordDTO medicalRecordDTOToAdd) throws AlreadyExistsException, MissingInformationException {
        MedicalRecordDTO addedMedicalRecordDTO;

        //check if the medicalRecordDTO is correctly filled
        if (checkMedicalRecordDTO(medicalRecordDTOToAdd)) {

            //check if the medical record does not already exist in the repository
            List<MedicalRecord> listOfMedicalRecords = medicalRecordRepository.findAllByFirstNameAndLastName(
                    medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());

            if (listOfMedicalRecords.size() == 0) {

                List<Person> listOfPersons = personRepository.findAllByFirstNameAndLastName(
                        medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());

                if (listOfPersons.size() != 0) {
                    //map DTO to DAO, save in repository,
                    //TODO-0 add the medical record to the person, save the person in repository
                    //and map back to MedicalRecordDTO for return
                    ModelMapper modelMapper = new ModelMapper();
                    MedicalRecord medicalRecordToAdd = modelMapper.map(medicalRecordDTOToAdd, MedicalRecord.class);

                    MedicalRecord addedMedicalRecord = medicalRecordRepository.save(medicalRecordToAdd);

                    /* TODO-0 à faire une fois update dans personService implémenté (tests à faire aussi dans MRST)
                                            Person personToUpdate=listOfPersons.get(0);
                                            personToUpdate.setMedicalRecord(addedMedicalRecord);
                                            Person updatedPerson = personService.update(personToUpdate);

                     */

                    addedMedicalRecordDTO = modelMapper.map(addedMedicalRecord, MedicalRecordDTO.class);
                } else {
                    log.error("No person found for this firstname & lastname, medical record can not be saved.");
                    throw new MissingInformationException("No person found for this firstname & lastname, medical record can not be saved.");
                }

            } else {
                log.error("medical record for person: " + medicalRecordDTOToAdd.getFirstName()
                        + " " + medicalRecordDTOToAdd.getLastName() + " already exists");
                throw new AlreadyExistsException("medical record for person: " + medicalRecordDTOToAdd.getFirstName()
                        + " " + medicalRecordDTOToAdd.getLastName() + " already exists");
            }

        } else {
            log.error("At least firstname, lastname and birthdate must be specified for saving");
            throw new MissingInformationException("At least firstname, lastname and birthdate must be specified for saving");
        }

        return addedMedicalRecordDTO;
    }


    /**
     * check if medicalRecordDTO input is correct
     *
     * @param medicalRecordDTO medicalRecordDTO information to be checked
     * @return true if correct
     */
    private boolean checkMedicalRecordDTO(MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordDTO.getFirstName() != null && !medicalRecordDTO.getFirstName().isEmpty()
                && medicalRecordDTO.getLastName() != null && !medicalRecordDTO.getLastName().isEmpty()
                && medicalRecordDTO.getBirthDate() != null
                && medicalRecordDTO.getBirthDate().isBefore(dateUtil.getCurrentLocalDate());
    }
}
