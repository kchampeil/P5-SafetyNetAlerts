package com.safetynet.alerts.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class DateUtil {
    private static final Logger logger = LogManager.getLogger(DateUtil.class);

    public int calculateAge(LocalDate birthDate) {

        if (birthDate != null && birthDate.isBefore(getCurrentLocalDate())) {
            return Period.between(birthDate, getCurrentLocalDate()).getYears();
        } else {
            logger.error("birthdate " + birthDate
                    + " is not valid (null or after current date " + getCurrentLocalDate() + ")");
            return -1;
        }
    }

    private LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

}
