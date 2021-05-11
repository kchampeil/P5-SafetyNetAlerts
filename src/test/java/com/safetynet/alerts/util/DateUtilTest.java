package com.safetynet.alerts.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DateUtilTest {

    private final DateUtil dateUtil = new DateUtil();

    @Test
    @DisplayName("GIVEN null birthdate WHEN calculating age THEN returned value is -1")
    void calculateAgeTest_WithNullBirthdate() {
        Assertions.assertEquals(-1, dateUtil.calculateAge(null));
    }

    @Test
    @DisplayName("GIVEN a birthdate after current date WHEN calculating age THEN returned value is -1")
    void calculateAgeTest_WithBirthdateAfterCurrentDate() {
        Assertions.assertEquals(-1, dateUtil.calculateAge(LocalDate.of(2050,1,1)));
    }
}