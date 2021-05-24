package com.safetynet.alerts.service;

import com.safetynet.alerts.constants.ExceptionConstants;
import com.safetynet.alerts.exceptions.AlreadyExistsException;
import com.safetynet.alerts.exceptions.DoesNotExistException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MedicalRecordService implements IMedicalRecordService {

    private static final DateUtil dateUtil = new DateUtil();

    private final MedicalRecordRepository medicalRecordRepository;

    private final PersonRepository personRepository;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PersonRepository personRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.personRepository = personRepository;
    }

    /**
     * save a list of medical records in DB
     *
     * @param listOfMedicalRecords list to be saved in DB
     * @return true if data saved, else false
     */
    @Override
    public Iterable<MedicalRecord> saveListOfMedicalRecords(List<MedicalRecord> listOfMedicalRecords) {

        try {
            return medicalRecordRepository.saveAll(listOfMedicalRecords);
        } catch (IllegalArgumentException e) {
            log.error("error when saving the list of medical records in DB : " + e.getMessage() + "\n");
            return null;
        }
    }


    /**
     * allow getting the list of all medical records found in DB
     *
     * @return a list of MedicalRecord
     */
    @Override
    public Iterable<MedicalRecordDTO> getAllMedicalRecords() {
        List<MedicalRecordDTO> listOfMedicalRecordDTO = new ArrayList<>();

        try {
            Iterable<MedicalRecord> listOfMedicalRecords = medicalRecordRepository.findAll();
            listOfMedicalRecords.forEach(medicalRecord ->
                    listOfMedicalRecordDTO
                            .add(mapMedicalRecordToMedicalRecordDTO(medicalRecord)));

        } catch (Exception exception) {
            log.error("error when getting the list of medical records " + exception.getMessage() + "\n");
        }

        return listOfMedicalRecordDTO;
    }

    /**
     * map the MedicalRecord object to the MedicalRecordDTO object
     *
     * @param medicalRecord MedicalRecord object to be mapped to MedicalRecordDTO
     * @return a MedicalRecordDTO
     */
    private MedicalRecordDTO mapMedicalRecordToMedicalRecordDTO(MedicalRecord medicalRecord) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }


    /**
     * save a new medical record in the repository
     *
     * @param medicalRecordDTOToAdd a new medical record to add
     * @return the added medical record
     * @throws AlreadyExistsException      if the medical record to add already exists in repository
     * @throws MissingInformationException if there are missing properties for the medical to save
     * @throws DoesNotExistException       if the person related to medical record to add does not exist in repository
     */
    @Override
    public Optional<MedicalRecordDTO> addMedicalRecord(MedicalRecordDTO medicalRecordDTOToAdd)
            throws AlreadyExistsException, MissingInformationException, DoesNotExistException {

        Optional<MedicalRecordDTO> addedMedicalRecordDTO;

        //check if the medicalRecordDTO is correctly filled
        if (checkMedicalRecordDTO(medicalRecordDTOToAdd)) {

            //check if the medical record does not already exist in the repository
            List<MedicalRecord> listOfMedicalRecords = medicalRecordRepository.findAllByFirstNameAndLastName(
                    medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());

            if (listOfMedicalRecords.size() == 0) {

                Person personToUpdate = personRepository.findByFirstNameAndLastName(
                        medicalRecordDTOToAdd.getFirstName(), medicalRecordDTOToAdd.getLastName());

                //check if the person already exist in the repository
                if (personToUpdate != null) {
                    //map DTO to DAO, save in repository,
                    ModelMapper modelMapper = new ModelMapper();
                    MedicalRecord medicalRecordToAdd = modelMapper.map(medicalRecordDTOToAdd, MedicalRecord.class);
                    MedicalRecord addedMedicalRecord = medicalRecordRepository.save(medicalRecordToAdd);

                    //add the medical record to the person, save the person in repository
                    personToUpdate.setMedicalRecord(addedMedicalRecord);
                    personRepository.save(personToUpdate);

                    //and map back to MedicalRecordDTO for return
                    addedMedicalRecordDTO = Optional.ofNullable(modelMapper.map(addedMedicalRecord, MedicalRecordDTO.class));
                } else {
                    throw new DoesNotExistException(ExceptionConstants.NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME
                            + medicalRecordDTOToAdd.getFirstName() + " " + medicalRecordDTOToAdd.getLastName()
                            + ", medical record can not be saved.");
                }

            } else {
                throw new AlreadyExistsException(ExceptionConstants.ALREADY_EXIST_MEDICAL_RECORD_FOR_FIRSTNAME_AND_LASTNAME
                        + medicalRecordDTOToAdd.getFirstName() + " " + medicalRecordDTOToAdd.getLastName());
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING);
        }

        return addedMedicalRecordDTO;
    }


    /**
     * update a medical record of a given firstname+lastname
     *
     * @param medicalRecordDTOToUpdate a medical record to update
     * @return the updated medical record
     * @throws DoesNotExistException       if the medical record to update does not exist in repository
     * @throws MissingInformationException if there are missing properties for the medical to update
     */
    @Override
    public Optional<MedicalRecordDTO> updateMedicalRecord(MedicalRecordDTO medicalRecordDTOToUpdate) throws DoesNotExistException, MissingInformationException {

        Optional<MedicalRecordDTO> updatedMedicalRecordDTO;

        //check if the medicalRecordDTOToUpdate is correctly filled
        if (checkMedicalRecordDTO(medicalRecordDTOToUpdate)) {

            //check if the medical record exists in the repository
            MedicalRecord existingMedicalRecord = medicalRecordRepository
                    .findByFirstNameAndLastName(medicalRecordDTOToUpdate.getFirstName(), medicalRecordDTOToUpdate.getLastName());

            if (existingMedicalRecord != null) {
                //map DTO to DAO, save in repository and map back to FireStationDTO for return
                ModelMapper modelMapper = new ModelMapper();
                MedicalRecord medicalRecordToUpdate = modelMapper.map(medicalRecordDTOToUpdate, MedicalRecord.class);
                medicalRecordToUpdate.setMedicalRecordId(existingMedicalRecord.getMedicalRecordId());

                MedicalRecord updatedMedicalRecord = medicalRecordRepository.save(medicalRecordToUpdate);

                updatedMedicalRecordDTO = Optional.ofNullable(modelMapper.map(updatedMedicalRecord, MedicalRecordDTO.class));

            } else {
                throw new DoesNotExistException(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON
                        + medicalRecordDTOToUpdate.getFirstName() + " " + medicalRecordDTOToUpdate.getLastName());
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING);
        }

        return updatedMedicalRecordDTO;
    }


    /**
     * delete the medical record for the given firstname+lastname in the repository
     *
     * @param firstName the firstname of the person we want to delete the medical record
     * @param lastName  the lastname of the person we want to delete the medical record
     * @return the deleted medical record
     * @throws DoesNotExistException       if no medical record has been found for the given firstname+lastname
     * @throws MissingInformationException if no firstname+lastname has been given
     */
    @Override
    public MedicalRecord deleteMedicalRecordByFirstNameAndLastName(String firstName, String lastName) throws DoesNotExistException, MissingInformationException {
        MedicalRecord medicalRecordToDelete;

        //check if the firstname+lastname is correctly filled
        if (firstName != null && !firstName.equals("")
                && lastName != null && !lastName.equals("")) {

            //check if there is a medical record associated to this firstname+lastname in the repository
            medicalRecordToDelete = medicalRecordRepository.findByFirstNameAndLastName(firstName, lastName);
            if (medicalRecordToDelete != null) {

                //delete the medical record id for person
                Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
                if (person != null) {
                    person.setMedicalRecord(null);
                    personRepository.save(person);
                }

                medicalRecordRepository.deleteById(medicalRecordToDelete.getMedicalRecordId());

            } else {
                throw new DoesNotExistException(ExceptionConstants.NO_MEDICAL_RECORD_FOUND_FOR_PERSON + firstName + " " + lastName);
            }

        } else {
            throw new MissingInformationException(ExceptionConstants.MISSING_INFORMATION_MEDICAL_RECORD_WHEN_DELETING);
        }

        return medicalRecordToDelete;
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
