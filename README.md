# Littlepay Coding Challenge

# Read Me First
I generated the project using Spring Initializr (https://start.spring.io/) and have included some links below. 
You can build and run the application via the supplied Maven scripts (mvnw and mvnw.cmd for Windows) or your own installed version.

# Running the application
## Compile and run tests
``mvn clean install`` or
``./mvnw clean install``
## Run the application
``mvn spring-boot:run`` or
``./mvnw spring-boot:run``
Once Spring Boot has finished started (see ``LittlepayCodingChallengeApplication``), the CSV producer ``TravelCostCsvProducer`` to generate a CSV output file.  

## Inputs/outputs
Uses file locations as configured in ``application.properties`` (see ``file.input`` and ``file.output``). 
I didn't focus much on the CSV part and wrote something quite barebones with limited testing (focusing more on testing various use cases).
Note: I consider this part incomplete to be honest as I didn't finish the business logic for various cases. 
For instance, the system currently produces one output line for each input line, where you'd expect fewer given at least one tap on/off pair.  

TBC

## Assumptions 
### Incomplete trips
* A trip is considered incomplete when consecutive taps are both ``ON``. This may mean the bus is also different, but I didn't see the need to check this in code as a change of bus should mean there is also two consecutive tap ``ON``. 
* When the last tap is ON, you could consider the trip to still be ongoing. I'd assume a real implementation would deal with this using a timeout of sorts. For this coding exercise, I will mark the trip as incomplete for simplicity.
* Incomplete trips will always have a duration of 0 seconds and an end time matching the start of the tap on. 

# Todo
* Adding full support for CSV (as mentioned in input/output section).
* I haven't tested for different credit cards (e.g. tap ON with card 1, tap OFF with card 2), my assumption would be that these should be seen as separate activities. Skipped for time.   


# Getting Started - Documentation & links from Spring Boot Initializr

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)

