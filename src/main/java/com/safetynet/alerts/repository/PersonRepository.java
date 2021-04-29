package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {

    List<Person> findAllByCity(String cityName);

    List<Person> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findAllByAddress(String address);

    List<Person> findAllByFireStation_StationNumber(Integer stationNumber);
}
