# Littlepay Coding Challenge - Technical Summary

## Project Overview
The Littlepay Coding Challenge is a Spring Boot application designed to process tap on/off events from public transport systems and calculate trip costs. The application reads tap events from a CSV file, processes them to determine trip status and cost, and outputs the results to another CSV file.

## Architecture
The application follows a simple service-oriented architecture with the following components:

1. **Data Models (DTOs)**: Represent the core data structures used in the application
2. **Service Layer**: Contains the business logic for processing tap events and calculating trip costs
3. **CSV Processing**: Handles reading input data and writing output data

The application is built using Spring Boot, which provides dependency injection, configuration management, and application lifecycle events.

## Component Descriptions

### Data Models (DTOs)
- **TapDetail**: Represents a tap event with properties like ID, timestamp, tap type (ON/OFF), stop ID, company ID, bus ID, and PAN (credit card number)
- **TravelPrice**: Represents the cost of travel between two stops
- **TravelPriceId**: Identifies a unique travel route between two stops (regardless of direction)
- **TripResult**: Represents the result of a trip, including start and end times, duration, start and end stops, charge amount, and status

### Enums
- **StopId**: Represents the different stops in the system (STOP1, STOP2, STOP3)
- **TapType**: Represents the types of taps (ON, OFF)
- **TripStatus**: Represents the possible statuses of a trip (COMPLETED, INCOMPLETE, CANCELLED)

### Services
- **TravelCostService**: Contains the core business logic for calculating trip costs based on tap events
- **TravelCostCsvProducer**: Handles reading tap data from a CSV file, processing it using the TravelCostService, and writing the results to another CSV file

## Data Flow
1. The application starts and the Spring Boot framework initializes all components
2. When the application is ready, the `TravelCostCsvProducer` is triggered by the `ApplicationReadyEvent`
3. The `TravelCostCsvProducer` reads tap data from the input CSV file specified in `application.properties`
4. The tap data is converted into `TapDetail` objects
5. The `TravelCostService` processes the tap details to determine trip status and calculate costs
6. The results are converted to CSV format and written to the output file specified in `application.properties`

## Technical Details

### Trip Status Determination
- **Completed Trip**: A trip with a tap ON followed by a tap OFF at a different stop
- **Incomplete Trip**: A trip with a tap ON but no corresponding tap OFF, or two consecutive tap ONs
- **Cancelled Trip**: A trip with a tap ON followed by a tap OFF at the same stop

### Cost Calculation
- The cost of travel between stops is predefined in the `TravelCostService`:
  - STOP1 to STOP2: $3.25
  - STOP2 to STOP3: $5.50
  - STOP1 to STOP3: $7.30
- For incomplete trips, the maximum possible fare from the tap ON stop is charged
- For cancelled trips, no charge is applied ($0)

### CSV Processing
- Input CSV format: ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
- Output CSV format: Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status

### Configuration
- Input and output file paths are configured in `application.properties`:
  - `file.input`: Path to the input CSV file (default: classpath:taps-example-input.csv)
  - `file.output`: Path to the output CSV file (default: output.csv)

## Limitations and Future Improvements
- The CSV processing is basic and could be enhanced with more robust error handling and validation
- The system currently produces one output line for each input line, which may not be optimal for representing trips
- There's no handling for different credit cards (e.g., tap ON with card 1, tap OFF with card 2)
- The system could benefit from a timeout mechanism for handling incomplete trips at the end of the day