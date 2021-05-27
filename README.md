SafetyNet Alerts
================
---

Description
-----------
Application enabling to send information to emergency services in case of disaster

Technical information
---------------------
* Build automation : `Maven`
* Language : `Java` *version 1.8*
* Framework `Spring Boot` *version 2.4.2* using starters :
  * Spring Boot Actuator *(Enabled actuators are: health, info, metrics, httptrace)*
  * Spring Web 
  * Lombok
  * H2 Database  
  * Spring Data JPA
  * Spring Boot Test
* `Jackson` libraries to manage the JSON files    
* Database `H2 database`
* Code coverage with `JaCoCo`

Input
-----
* a file containing the data on persons and fire stations named `data.json` in package src\main\resources\json

Endpoints
---------
The API accepts JSON-encoded request bodies and returns JSON-encoded files as well.
It uses standard HTTP response codes and verbs.

Main endpoints are : 
* the ones aiming to manage the persons: 
    * `POST /person` and `PUT /person` with person information to add or update a specified person
    * `DELETE /person?firstName=<firstName>&lastName=<lastName>` to delete the specified person

    
* the ones aiming to manage the medical records:
    * `POST /medicalRecord` and `PUT /medicalRecord` with medical record information to add or update a specified medical record
    * `DELETE /medicalRecord?firstName=<firstName>&lastName=<lastName>` to delete the specified medical record

    
* the ones aiming to manage the fire stations:
    * `POST /firestation` and `PUT /firestation` with fire station information to add or update a specified fire station
    * `DELETE /firestation/address?address=<address>` to delete the fire station of the specified address
    * `DELETE /firestation/station?stationNumber=<station_number>` to delete the specified fire station


* the ones aiming to retrieve information     
    * `/firestation?stationNumber=<station_number>` to get the list of persons covered by the given station, completed with the number of adults and children in the covered area
    * `/childAlert?address=<address>` to get the list of children living at the given address, completed with other family member names
    * `/phoneAlert?firestation=<firestation_number>` to get the list of phone numbers of persons living in the covered area of the given station
    * `/fire?address=<address>` to get the list of persons living at the given address, and the number of the covering station
    * `/flood/stations?stations=<a list of station_numbers>` to get the list of households (completed with medical records of persons) covered by the given stations
    * `/personInfo?firstName=<firstName>&lastName=<lastName>` to get the information of a given person named with firstname and lastname
    * `/communityEmail?city=<city>` to get the list of emails of all citizens for the given city