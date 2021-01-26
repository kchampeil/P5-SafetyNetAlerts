package com.safetynet.alerts.service;

import com.safetynet.alerts.repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

}
