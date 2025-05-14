package younan.george.littlepaycodingchallenge.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;

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
     * Reads tap data from the input CSV file, processes it to calculate trip costs,
     * and writes the results to the output CSV file.
     * This method is automatically triggered when the application is ready.
     *
     * @throws Exception if there is an error reading from or writing to the files
     */
    @EventListener(ApplicationReadyEvent.class)
    public void readCsvAndProduceResult() throws Exception {
        System.out.println("Starting to read taps from " + inputFile);
        List<TapDetail> tapDetails = new ArrayList<>();

        // Read and parse the input CSV file
        try (Stream<String> fileStream = Files.lines(ResourceUtils.getFile(inputFile).toPath())) {
            List<String[]> lines = fileStream.map(line -> line.split(",")).toList();
            for (String[] line : lines) {
                // Skip the header row
                if (!"ID".equals(line[0])) {
                    tapDetails.add(new TapDetail(
                            Integer.parseInt(line[0].trim()),
                            ZonedDateTime.parse(line[1].trim(), formatter),
                            TapType.valueOf(line[2].trim()),
                            StopId.valueOf(line[3].trim().toUpperCase()),
                            line[4].trim(),
                            line[5].trim(),
                            line[6].trim()
                    ));
                }
            }
        }

        // Calculate trip costs for all tap details
        List<TripResult> results = travelCostService.calculateCostForAll(tapDetails);

        // Prepare the output file
        Path filePath = Paths.get(outputFile);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);

        // Write the header row to the output file
        String headerRow = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status" + System.lineSeparator();

        // Convert trip results to CSV format
        List<String> resultsAsStrings = results.stream()
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
        System.out.println("Finished writing results to " + outputFile);
    }
}
