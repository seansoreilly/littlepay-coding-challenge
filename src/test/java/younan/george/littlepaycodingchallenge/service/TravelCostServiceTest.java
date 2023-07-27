package younan.george.littlepaycodingchallenge.service;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.*;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class TravelCostServiceTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneOffset.UTC);
    TravelCostService travelCostService = new TravelCostService();

    List<TapDetail> tapExamples = Arrays.asList(
            new TapDetail(1, ZonedDateTime.parse("22-01-2023 13:00:00", formatter), TapType.ON, StopId.STOP_1, CompanyId.COMPANY_1, BusId.BUS_37, "5500005555555559"),
            new TapDetail(2, ZonedDateTime.parse("22-01-2023 13:05:00", formatter), TapType.OFF, StopId.STOP_2, CompanyId.COMPANY_1, BusId.BUS_37, "5500005555555559"),
            new TapDetail(3, ZonedDateTime.parse("22-01-2023 09:20:00", formatter), TapType.ON, StopId.STOP_3, CompanyId.COMPANY_1, BusId.BUS_36, "4111111111111111"),
            new TapDetail(4, ZonedDateTime.parse("23-01-2023 08:00:00", formatter), TapType.ON, StopId.STOP_1, CompanyId.COMPANY_1, BusId.BUS_37, "4111111111111111"),
            new TapDetail(5, ZonedDateTime.parse("23-01-2023 08:02:00", formatter), TapType.OFF, StopId.STOP_1, CompanyId.COMPANY_1, BusId.BUS_37, "4111111111111111"),
            new TapDetail(6, ZonedDateTime.parse("24-01-2023 16:30:00", formatter), TapType.OFF, StopId.STOP_2, CompanyId.COMPANY_1, BusId.BUS_37, "5500005555555559")
    );

    @Test
    void shouldIdentifyCompletedTripBetweenTaps() {
        TripResult expectedResult = new TripResult(
                ZonedDateTime.parse("22-01-2023 13:00:00", formatter),
                ZonedDateTime.parse("22-01-2023 13:05:00", formatter),
                900,
                StopId.STOP_1,
                StopId.STOP_2,
                new BigDecimal("3.25"),
                CompanyId.COMPANY_1,
                BusId.BUS_37,
                "5500005555555559",
                TripStatus.COMPLETED
        );

        TripResult tripResult = travelCostService.calculateCost(tapExamples.get(0), tapExamples.get(1));

        MatcherAssert.assertThat(tripResult, CoreMatchers.equalTo(expectedResult));
    }

}
