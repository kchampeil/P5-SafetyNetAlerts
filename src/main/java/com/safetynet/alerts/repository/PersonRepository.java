package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {

    List<Person> findAllByCity(String cityName);

    List<Person> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findAllByAddress(String address);

    List<Person> findAllByFireStation_StationNumber(Integer stationNumber);

    Person findByFirstNameAndLastName(String firstName, String lastName);

    @Transactional
    Integer deleteByFirstNameAndLastName(String firstName, String lastName);
}
