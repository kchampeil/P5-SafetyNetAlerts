package com.safetynet.alerts.testconstants;

import java.time.LocalDate;

public class TestConstants {
    public final static LocalDate ADULT_BIRTHDATE = LocalDate.of(1999, 9, 9);
    public final static LocalDate CHILD_BIRTHDATE = LocalDate.of(2019, 1, 1);
    public final static int ADULT_AGE = 21;
    public final static int CHILD_AGE = 2;

    public static final String EXISTING_FIRSTNAME = "John";
    public static final String EXISTING_LASTNAME = "Boyd";
    public static final String FIRSTNAME_NOT_FOUND = "FirstName not found";
    public static final String LASTNAME_NOT_FOUND = "Lastname not found";
    public static final String NEW_FIRSTNAME = "New firstName";
    public static final String NEW_LASTNAME = "New lastName";

    public final static String EXISTING_ADDRESS = "1509 Culver St";
    public final static String ADDRESS_NOT_FOUND = "Address not found";
    public final static String NEW_ADDRESS = "New address";

    public final static String EXISTING_CITY = "Culver";
    public final static String CITY_NOT_FOUND = "City not found";

    public final static Integer EXISTING_STATION_NUMBER = 3;
    public final static Integer STATION_NUMBER_NOT_FOUND = 2021;
    public final static Integer NEW_STATION_NUMBER = 100;
}
