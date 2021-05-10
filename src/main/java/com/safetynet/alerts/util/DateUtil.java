package com.safetynet.alerts.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Component
public class DateUtil {
    
    public int calculateAge(LocalDate birthDate) {

        if (birthDate != null && birthDate.isBefore(getCurrentLocalDate())) {
            return Period.between(birthDate, getCurrentLocalDate()).getYears();
        } else {
            log.error("birthdate " + birthDate
                    + " is not valid (null or after current date " + getCurrentLocalDate() + ")");
            return -1;
        }
    }

    public LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

}
