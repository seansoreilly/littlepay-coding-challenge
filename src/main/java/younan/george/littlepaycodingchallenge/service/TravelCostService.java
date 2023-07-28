package younan.george.littlepaycodingchallenge.service;

import org.springframework.stereotype.Service;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TravelPrice;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;

@Service
public class TravelCostService {
    private HashSet<TravelPrice> travelPrices = new HashSet<>();

    public TravelCostService() {
        travelPrices.add(new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.25")));
        travelPrices.add(new TravelPrice(StopId.STOP_2, StopId.STOP_3, new BigDecimal("5.50")));
        travelPrices.add(new TravelPrice(StopId.STOP_1, StopId.STOP_3, new BigDecimal("7.30")));
    }

    public TripResult calculateCost(TapDetail currentTap, TapDetail nextTap) {
        if (currentTap == null) {
            throw new IllegalArgumentException("currentTap must be non null!");
        }
        if (isIncomplete(currentTap, nextTap)) {
            return calculateCostForIncompleteTrip(currentTap);
        }

        // TODO add tests and remove hardcoded data
        return new TripResult(
                currentTap.getDateTimeUTC(),
                nextTap.getDateTimeUTC(),
                900,
                currentTap.getStopId(),
                nextTap.getStopId(),
                new BigDecimal("3.25"),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.COMPLETED
        );
    }

    private TripResult calculateCostForIncompleteTrip(TapDetail currentTap) {
        TravelPrice maxCostForStop = getMaxCostForStop(currentTap.getStopId());

        return new TripResult(
                currentTap.getDateTimeUTC(),
                currentTap.getDateTimeUTC(),
                0,
                maxCostForStop.getStop1(),
                maxCostForStop.getStop2(),
                maxCostForStop.getCost(),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.INCOMPLETE
        );
    }

    boolean isIncomplete(TapDetail currentTap, TapDetail nextTap) {
        return nextTap == null || (currentTap.getTapType() == TapType.ON && currentTap.getTapType() == nextTap.getTapType());
    }

    TravelPrice getMaxCostForStop(StopId stopId) {
        TravelPrice result = travelPrices.stream()
                .filter(travelPrice -> travelPrice.getStop1() == stopId || travelPrice.getStop2() == stopId)
                .max(Comparator.comparing(TravelPrice::getCost))
                .orElseThrow();

        return result.getStop1() == stopId ? result : result.reverseStops();
    }
}
