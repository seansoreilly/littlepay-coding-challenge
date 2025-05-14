package younan.george.littlepaycodingchallenge.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service responsible for reading tap data from a CSV file, processing it using the TravelCostService,
 * and writing the results to another CSV file.
 * This service is triggered automatically when the application is ready.
 */
@Service
public class TravelCostCsvProducer {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TravelCostCsvProducer.class);

    /**
     * Validator for bean validation
     */
    private final jakarta.validation.Validator validator;

    /**
     * Constructor initializes the validator
     */
    public TravelCostCsvProducer() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }
    /**
     * Date-time formatter for parsing timestamps in the input CSV file.
     * The format is "dd-MM-yyyy HH:mm:ss" in UTC timezone.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneOffset.UTC);

    /**
     * Path to the input CSV file containing tap data.
     * This value is injected from application.properties.
     */
    @Value("${file.input}")
    private String inputFile;

    /**
     * Path to the output CSV file where trip results will be written.
     * This value is injected from application.properties.
     */
    @Value("${file.output}")
    private String outputFile;

    /**
     * Service for calculating travel costs based on tap events.
     */
    @Autowired
    private TravelCostService travelCostService;

    /**
     * Validates a TapDetail object using the validator.
     * 
     * @param tapDetail The TapDetail object to validate
     * @return true if the object is valid, false otherwise
     */
    private boolean isValid(TapDetail tapDetail) {
        if (tapDetail == null) {
            return false;
        }

        var violations = validator.validate(tapDetail);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<TapDetail> violation : violations) {
                logger.warn("Validation error for tap detail {}: {} - {}", 
                    tapDetail.getId(), violation.getPropertyPath(), violation.getMessage());
            }
            return false;
        }
        return true;
    }

    /**
     * Validates a TripResult object using the validator.
     * 
     * @param tripResult The TripResult object to validate
     * @return true if the object is valid, false otherwise
     */
    private boolean isValidTripResult(TripResult tripResult) {
        if (tripResult == null) {
            return false;
        }

        var violations = validator.validate(tripResult);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<TripResult> violation : violations) {
                logger.warn("Validation error for trip result from {} to {}: {} - {}", 
                    tripResult.getFromStopId(), tripResult.getToStopId(), 
                    violation.getPropertyPath(), violation.getMessage());
            }
            return false;
        }
        return true;
    }

    /**
     * Reads tap data from the input CSV file, processes it to calculate trip costs,
     * and writes the results to the output CSV file.
     * This method is automatically triggered when the application is ready.
     *
     * @throws Exception if there is an error reading from or writing to the files
     */
    @EventListener(ApplicationReadyEvent.class)
    public void readCsvAndProduceResult() throws Exception {
        logger.info("Starting to read taps from {}", inputFile);
        List<TapDetail> tapDetails = new ArrayList<>();

        // Read and parse the input CSV file
        try (Stream<String> fileStream = Files.lines(ResourceUtils.getFile(inputFile).toPath())) {
            List<String[]> lines = fileStream.map(line -> line.split(",")).toList();
            logger.debug("Found {} lines in the input file", lines.size());

            for (String[] line : lines) {
                // Skip the header row
                if (!"ID".equals(line[0])) {
                    try {
                        TapDetail tapDetail = new TapDetail(
                                Integer.parseInt(line[0].trim()),
                                ZonedDateTime.parse(line[1].trim(), formatter),
                                TapType.valueOf(line[2].trim()),
                                StopId.valueOf(line[3].trim().toUpperCase()),
                                line[4].trim(),
                                line[5].trim(),
                                line[6].trim()
                        );

                        // Validate the tap detail before adding it
                        if (isValid(tapDetail)) {
                            tapDetails.add(tapDetail);
                        } else {
                            logger.warn("Skipping invalid tap detail: {}", String.join(",", line));
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing line: {}", String.join(",", line), e);
                    }
                }
            }
            logger.info("Successfully parsed {} tap details", tapDetails.size());
        } catch (Exception e) {
            logger.error("Error reading input file: {}", inputFile, e);
            throw e;
        }

        // Calculate trip costs for all tap details
        logger.debug("Calculating trip costs for {} tap details", tapDetails.size());
        List<TripResult> allResults = travelCostService.calculateCostForAll(tapDetails);

        // Validate trip results
        List<TripResult> validResults = new ArrayList<>();
        for (TripResult result : allResults) {
            if (isValidTripResult(result)) {
                validResults.add(result);
            } else {
                logger.warn("Skipping invalid trip result from {} to {}", 
                    result.getFromStopId(), result.getToStopId());
            }
        }

        logger.info("Generated {} trip results, {} valid", allResults.size(), validResults.size());

        // Prepare the output file
        Path filePath = Paths.get(outputFile);
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            logger.debug("Created output file: {}", outputFile);

            // Write the header row to the output file
            String headerRow = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status" + System.lineSeparator();

            // Convert trip results to CSV format
            List<String> resultsAsStrings = validResults.stream()
                    .map(result -> StringUtils.join(
                            result.getStarted(),
                            ",",
                            result.getFinished(),
                            ",",
                            result.getDurationSecs(),
                            ",",
                            result.getFromStopId(),
                            ",",
                            result.getToStopId(),
                            ",",
                            result.getChargeAmount(),
                            ",",
                            result.getCompanyId(),
                            ",",
                            result.getBusID(),
                            ",",
                            result.getStatus(),
                            System.lineSeparator())
                    ).collect(Collectors.toList());

            // Write the results to the output file
            Files.writeString(filePath, headerRow, StandardOpenOption.APPEND);
            for (String str : resultsAsStrings) {
                Files.writeString(filePath, str, StandardOpenOption.APPEND);
            }
            logger.info("Successfully wrote {} trip results to {}", validResults.size(), outputFile);
        } catch (Exception e) {
            logger.error("Error writing to output file: {}", outputFile, e);
            throw e;
        }
    }
}
