package com.safetynet.alerts.constants;

import java.time.LocalDate;

public class TestConstants {
    public final static LocalDate ADULT_BIRTHDATE = LocalDate.of(1999, 9, 9);
    public final static LocalDate CHILD_BIRTHDATE = LocalDate.of(2019, 1, 1);
    public final static int ADULT_AGE = 21;


    public static final String EXISTING_FIRSTNAME = "Firstname of existing person";
    public static final String EXISTING_LASTNAME = "Lastname of existing person";
    public static final String FIRSTNAME_NOT_FOUND = "FirstName not found";
    public static final String LASTNAME_NOT_FOUND = "Lastname not found";

    public final static String EXISTING_ADDRESS = "Existing address";
    public final static String ADDRESS_NOT_FOUND = "Address not found";
    public final static String NEW_ADDRESS = "New address";

    public final static Integer EXISTING_STATION_NUMBER = 100;
    public final static Integer STATION_NUMBER_NOT_FOUND = 2021;
    public final static Integer NEW_STATION_NUMBER = 73;
}
