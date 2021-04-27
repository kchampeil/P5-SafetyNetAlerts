package com.safetynet.alerts.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

class DateUtilTest {

    private DateUtil dateUtil = new DateUtil();

    @Test
    @DisplayName("GIVEN null birthdate WHEN calculating age THEN returned value is -1")
    void calculateAgeTest_WithNullBirthdate() {
        assertEquals(-1, dateUtil.calculateAge(null));
    }

    @Test
    @DisplayName("GIVEN a birthdate after current date WHEN calculating age THEN returned value is -1")
    void calculateAgeTest_WithBirthdateAfterCurrentDate() {
        assertEquals(-1, dateUtil.calculateAge(LocalDate.of(2050,1,1)));
    }
}