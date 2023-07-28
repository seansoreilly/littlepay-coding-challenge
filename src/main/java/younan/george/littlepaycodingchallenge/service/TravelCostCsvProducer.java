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

@Service
public class TravelCostCsvProducer {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneOffset.UTC);

    @Value("${file.input}")
    private String inputFile;

    @Value("${file.output}")
    private String outputFile;

    @Autowired
    private TravelCostService travelCostService;

    @EventListener(ApplicationReadyEvent.class)
    public void readCsvAndProduceResult() throws Exception {
        System.out.println("Starting to read taps from " + inputFile);
        List<TapDetail> tapDetails = new ArrayList<>();

        try (Stream<String> fileStream = Files.lines(ResourceUtils.getFile(inputFile).toPath())) {
            List<String[]> lines = fileStream.map(line -> line.split(",")).toList();
            for (String[] line : lines) {
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
        List<TripResult> results = travelCostService.calculateCostForAll(tapDetails);

        Path filePath = Paths.get(outputFile);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);

        String headerRow = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status" + System.lineSeparator();
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

        Files.writeString(filePath, headerRow, StandardOpenOption.APPEND);
        for (String str : resultsAsStrings) {
            Files.writeString(filePath, str, StandardOpenOption.APPEND);
        }
        System.out.println("Finished writing results to " + outputFile);
    }


}
