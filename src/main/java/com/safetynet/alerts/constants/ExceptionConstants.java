package com.safetynet.alerts.constants;

public class ExceptionConstants {

    //MissingInformationException
    public static final String MISSING_INFORMATION_FIRE_STATION_ALL = "All fire station information must be specified";
    public static final String MISSING_INFORMATION_FIRE_STATION_STATION_NUMBER = "Station number must be specified";
    public static final String MISSING_INFORMATION_FIRE_STATION_ADDRESS = "Address must be specified";

    //DoesNotExistException
    public static final String NO_FIRE_STATION_FOUND_FOR_ADDRESS = "No fire station found for address: ";
    public static final String NO_FIRE_STATION_FOUND_FOR_STATION_NUMBER = "No fire station found for station n°";

    //AlreadyExistException
    public static final String ALREADY_EXIST_FIRE_STATION_FOUND_FOR_ADDRESS = "There is already one fire station assigned to address: ";
}
