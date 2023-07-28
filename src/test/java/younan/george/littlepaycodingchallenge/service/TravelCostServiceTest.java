package younan.george.littlepaycodingchallenge.service;

import org.junit.jupiter.api.Test;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TravelPrice;
import younan.george.littlepaycodingchallenge.dto.TravelPriceId;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.*;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TravelCostServiceTest {
    public static final BigDecimal COST_BETWEEN_1_AND_2 = new BigDecimal("3.25");
    public static final BigDecimal COST_BETWEEN_2_AND_3 = new BigDecimal("5.50");
    public static final BigDecimal COST_BETWEEN_1_AND_3 = new BigDecimal("7.30");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneOffset.UTC);
    TravelCostService travelCostService = new TravelCostService();

    // matching the examples provided in the instructions
    List<TapDetail> tapExamples = Arrays.asList(
            new TapDetail(1, ZonedDateTime.parse("22-01-2023 13:00:00", formatter), TapType.ON, StopId.STOP1, "Company1", "Bus37", "5500005555555559"),
            new TapDetail(2, ZonedDateTime.parse("22-01-2023 13:05:00", formatter), TapType.OFF, StopId.STOP2, "Company1", "Bus37", "5500005555555559"),
            new TapDetail(3, ZonedDateTime.parse("22-01-2023 09:20:00", formatter), TapType.ON, StopId.STOP3, "Company1", "Bus36", "4111111111111111"),
            new TapDetail(4, ZonedDateTime.parse("23-01-2023 08:00:00", formatter), TapType.ON, StopId.STOP1, "Company1", "Bus37", "4111111111111111"),
            new TapDetail(5, ZonedDateTime.parse("23-01-2023 08:02:00", formatter), TapType.OFF, StopId.STOP1, "Company1", "Bus37", "4111111111111111"),
            new TapDetail(6, ZonedDateTime.parse("24-01-2023 16:30:00", formatter), TapType.OFF, StopId.STOP2, "Company1", "Bus37", "5500005555555559")
    );

    @Test
    void shouldIdentifyCompletedTrip_Stop1ToStop2() {
        TapDetail firstTap = new TapDetail(1, ZonedDateTime.parse("22-01-2023 13:00:00", formatter), TapType.ON, StopId.STOP1, "Company1", "Bus37", "5500005555555559");
        TapDetail secondTap = new TapDetail(2, ZonedDateTime.parse("22-01-2023 13:05:00", formatter), TapType.OFF, StopId.STOP2, "Company1", "Bus37", "5500005555555559");
        TripResult expectedResult = new TripResult(
                firstTap.getDateTimeUTC(),
                secondTap.getDateTimeUTC(),
                300,
                StopId.STOP1,
                StopId.STOP2,
                COST_BETWEEN_1_AND_2,
                firstTap.getCompanyId(),
                firstTap.getBusId(),
                firstTap.getPan(),
                TripStatus.COMPLETED
        );

        TripResult tripResult = travelCostService.calculateCost(firstTap, secondTap);

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyCompletedTrip_Stop1ToStop3() {
        TapDetail firstTap = new TapDetail(7, ZonedDateTime.parse("25-01-2023 12:30:00", formatter), TapType.ON, StopId.STOP1, "Company1", "Bus36", "5500005555555559");
        TapDetail secondTap = new TapDetail(8, ZonedDateTime.parse("25-01-2023 12:59:59", formatter), TapType.OFF, StopId.STOP3, "Company1", "Bus36", "5500005555555559");
        TripResult expectedResult = new TripResult(
                firstTap.getDateTimeUTC(),
                secondTap.getDateTimeUTC(),
                1799,
                StopId.STOP1,
                StopId.STOP3,
                COST_BETWEEN_1_AND_3,
                firstTap.getCompanyId(),
                firstTap.getBusId(),
                firstTap.getPan(),
                TripStatus.COMPLETED
        );

        TripResult tripResult = travelCostService.calculateCost(firstTap, secondTap);

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyCompletedTrip_Stop2ToStop3() {
        TapDetail firstTap = new TapDetail(7, ZonedDateTime.parse("25-01-2023 12:30:00", formatter), TapType.ON, StopId.STOP2, "Company1", "Bus36", "5500005555555559");
        TapDetail secondTap = new TapDetail(8, ZonedDateTime.parse("26-01-2023 12:30:01", formatter), TapType.OFF, StopId.STOP3, "Company1", "Bus36", "5500005555555559");
        TripResult expectedResult = new TripResult(
                firstTap.getDateTimeUTC(),
                secondTap.getDateTimeUTC(),
                86401,
                StopId.STOP2,
                StopId.STOP3,
                COST_BETWEEN_2_AND_3,
                firstTap.getCompanyId(),
                firstTap.getBusId(),
                firstTap.getPan(),
                TripStatus.COMPLETED
        );

        TripResult tripResult = travelCostService.calculateCost(firstTap, secondTap);

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyCompletedTrip_Stop3ToStop2() {
        TapDetail firstTap = new TapDetail(7, ZonedDateTime.parse("25-01-2023 12:30:00", formatter), TapType.ON, StopId.STOP3, "Company1", "Bus36", "5500005555555559");
        TapDetail secondTap = new TapDetail(8, ZonedDateTime.parse("26-01-2023 12:30:01", formatter), TapType.OFF, StopId.STOP2, "Company1", "Bus36", "5500005555555559");
        TripResult expectedResult = new TripResult(
                firstTap.getDateTimeUTC(),
                secondTap.getDateTimeUTC(),
                86401,
                StopId.STOP3,
                StopId.STOP2,
                COST_BETWEEN_2_AND_3,
                firstTap.getCompanyId(),
                firstTap.getBusId(),
                firstTap.getPan(),
                TripStatus.COMPLETED
        );

        TripResult tripResult = travelCostService.calculateCost(firstTap, secondTap);

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyIncompleteTrip() {
        TripResult expectedResult = new TripResult(
                ZonedDateTime.parse("22-01-2023 09:20:00", formatter),
                ZonedDateTime.parse("22-01-2023 09:20:00", formatter),
                0,
                StopId.STOP3,
                StopId.STOP1,
                COST_BETWEEN_1_AND_3,
                "Company1",
                "Bus36",
                "4111111111111111",
                TripStatus.INCOMPLETE
        );

        TripResult tripResult = travelCostService.calculateCost(tapExamples.get(2), tapExamples.get(3));

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyLastTapOnAsIncompleteTrip() {
        TripResult expectedResult = new TripResult(
                ZonedDateTime.parse("22-01-2023 09:20:00", formatter),
                ZonedDateTime.parse("22-01-2023 09:20:00", formatter),
                0,
                StopId.STOP3,
                StopId.STOP1,
                COST_BETWEEN_1_AND_3,
                "Company1",
                "Bus36",
                "4111111111111111",
                TripStatus.INCOMPLETE
        );

        TripResult tripResult = travelCostService.calculateCost(tapExamples.get(2), null);

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyCancelledTrip() {
        TapDetail firstTap = new TapDetail(4, ZonedDateTime.parse("23-01-2023 08:00:00", formatter), TapType.ON, StopId.STOP1, "Company1", "Bus37", "4111111111111111");
        TapDetail secondTap = new TapDetail(5, ZonedDateTime.parse("23-01-2023 08:02:00", formatter), TapType.OFF, StopId.STOP1, "Company1", "Bus37", "4111111111111111");

        TripResult expectedResult = new TripResult(
                firstTap.getDateTimeUTC(),
                secondTap.getDateTimeUTC(),
                120,
                StopId.STOP1,
                StopId.STOP1,
                BigDecimal.ZERO,
                "Company1",
                "Bus37",
                "4111111111111111",
                TripStatus.CANCELLED
        );

        TripResult tripResult = travelCostService.calculateCost(tapExamples.get(3), tapExamples.get(4));

        assertThat(tripResult, equalTo(expectedResult));
    }

    @Test
    void shouldIdentifyMaxCostForStops() {
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP1), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP1, StopId.STOP3), COST_BETWEEN_1_AND_3)));
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP2), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP2, StopId.STOP3), COST_BETWEEN_2_AND_3)));
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP3), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP1, StopId.STOP3), COST_BETWEEN_1_AND_3)));

        // reverse direction
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP1), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP3, StopId.STOP1), COST_BETWEEN_1_AND_3)));
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP2), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP3, StopId.STOP2), COST_BETWEEN_2_AND_3)));
        assertThat(travelCostService.getMaxCostForStop(StopId.STOP3), equalTo(new TravelPrice(new TravelPriceId(StopId.STOP3, StopId.STOP1), COST_BETWEEN_1_AND_3)));
    }

}
