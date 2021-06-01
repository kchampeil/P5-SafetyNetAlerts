package com.safetynet.alerts.constants;

public class ExceptionConstants {

    //MissingInformationException
    public static final String MISSING_INFORMATION_FIRE_STATION_ALL = "All fire station information must be specified";
    public static final String MISSING_INFORMATION_FIRE_STATION_STATION_NUMBER = "Station number must be specified";
    public static final String MISSING_INFORMATION_FIRE_STATION_ADDRESS = "Address must be specified";
    public static final String MISSING_INFORMATION_MEDICAL_RECORD_WHEN_DELETING = "Firstname AND lastname must be specified";
    public static final String MISSING_INFORMATION_MEDICAL_RECORD_WHEN_ADDING_OR_UPDATING = "Firstname, lastname AND birthdate must be specified";
    public static final String MISSING_INFORMATION_PERSON_WHEN_ADDING_OR_UPDATING="All information must be specified";
    public static final String MISSING_INFORMATION_PERSON_WHEN_DELETING = "Firstname AND lastname must be specified";

    //DoesNotExistException
    public static final String NO_FIRE_STATION_FOUND_FOR_ADDRESS = "No fire station found for address: ";
    public static final String NO_FIRE_STATION_FOUND_FOR_STATION_NUMBER = "No fire station found for station n°";
    public static final String NO_MEDICAL_RECORD_FOUND_FOR_PERSON = "No medical record found for person: ";
    public static final String NO_PERSON_FOUND_FOR_FIRSTNAME_AND_LASTNAME = "No person found for ";

    //AlreadyExistException
    public static final String ALREADY_EXIST_FIRE_STATION_FOR_ADDRESS = "There is already one fire station assigned to address: ";
    public static final String ALREADY_EXIST_MEDICAL_RECORD_FOR_FIRSTNAME_AND_LASTNAME = "There is already one medical record assigned to: ";
    public static final String ALREADY_EXIST_PERSON_FOR_FIRSTNAME_AND_LASTNAME = "There is already one person assigned to: ";
}
