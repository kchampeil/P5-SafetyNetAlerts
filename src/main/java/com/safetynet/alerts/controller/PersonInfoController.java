package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.dto.PersonInfoDTO;
import com.safetynet.alerts.service.IPersonInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PersonInfoController {

    @Autowired
    private IPersonInfoService personInfoService;

    /**
     * Read - Get information on persons for a given firstname and lastname
     *
     * @param firstName of the person(s) we want related information
     * @param lastName  of the person(s) we want related information
     * @return - A list of PersonInfoDTO
     */
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {

        log.info("GET request on endpoint /personInfo received for person(s) named : " + firstName + " " + lastName);
        List<PersonInfoDTO> returnedListOfPersonInfo
                = personInfoService.getPersonInfoByFirstNameAndLastName(firstName, lastName);

        if (returnedListOfPersonInfo == null) {
            log.error("error when getting the person information for " + firstName + " " + lastName);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            if (returnedListOfPersonInfo.isEmpty()) {
                log.warn("response to GET request on endpoint /personInfo for person "
                        + firstName + " " + lastName + " is empty, no person information found");
                return new ResponseEntity<>(returnedListOfPersonInfo, HttpStatus.NOT_FOUND);

            } else {
                log.info("response to GET request on endpoint /personInfo sent for person(s) "
                        + firstName + " " + lastName + " with " + returnedListOfPersonInfo.size() + " values");
                return new ResponseEntity<>(returnedListOfPersonInfo, HttpStatus.OK);
            }
        }

    }

}
