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

TBC

## Assumptions 
### Incomplete trips
* A trip is considered incomplete when consecutive taps are both ``ON``. This may mean the bus is also different, but I didn't see the need to code this in for now as a change of bus should mean there is also two consecutive tap ``ON``. 
* When the last tap is ON, you could consider the trip to still be. I'd assume a real implementation would deal with this using a timeout of sorts. For this coding exercise, I will mark the trip as incomplete for simplicity.
* Incomplete trips will always have a duration of 0 seconds and an end time matching the start of the tap on. 

# Getting Started - Documentation & links from Spring Boot Initializr

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)

