package younan.george.littlepaycodingchallenge.service;

import org.springframework.stereotype.Service;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TravelPrice;
import younan.george.littlepaycodingchallenge.dto.TravelPriceId;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class TravelCostService {
    private HashMap<TravelPriceId, BigDecimal> travelPrices;

    public TravelCostService() {
        travelPrices = new HashMap<>();
        travelPrices.put(new TravelPriceId(StopId.STOP1, StopId.STOP2), new BigDecimal("3.25"));
        travelPrices.put(new TravelPriceId(StopId.STOP2, StopId.STOP3), new BigDecimal("5.50"));
        travelPrices.put(new TravelPriceId(StopId.STOP1, StopId.STOP3), new BigDecimal("7.30"));
    }

    public TripResult calculateCost(TapDetail currentTap, TapDetail nextTap) {
        if (currentTap == null) {
            throw new IllegalArgumentException("currentTap must be non null!");
        }
        if (isIncomplete(currentTap, nextTap)) {
            return calculateCostForIncompleteTrip(currentTap);
        }
        if (isCancelled(currentTap, nextTap)) {
            return calculateCostForCancelledTrip(currentTap, nextTap);
        }

        return calculateCostForCompletedTrip(currentTap, nextTap);
    }

    private TripResult calculateCostForCompletedTrip(TapDetail currentTap, TapDetail nextTap) {
        BigDecimal chargeAmount = travelPrices.get(new TravelPriceId(currentTap.getStopId(), nextTap.getStopId()));
        if (chargeAmount == null) {
            throw new IllegalArgumentException("Unknown travel cost between stops " + currentTap.getStopId() + ", " + nextTap.getStopId());
        }

        return new TripResult(
                currentTap.getDateTimeUTC(),
                nextTap.getDateTimeUTC(),
                getElapsedTime(currentTap, nextTap),
                currentTap.getStopId(),
                nextTap.getStopId(),
                chargeAmount,
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.COMPLETED
        );
    }

    private TripResult calculateCostForIncompleteTrip(TapDetail currentTap) {
        TravelPrice maxCostForStop = getMaxCostForStop(currentTap.getStopId());

        StopId[] stops = convertStopsToArray(maxCostForStop.getTravelPriceId().getStops());
        StopId nextStop = currentTap.getStopId() == stops[0] ? stops[1] : stops[0];
        return new TripResult(
                currentTap.getDateTimeUTC(),
                currentTap.getDateTimeUTC(),
                0,
                currentTap.getStopId(),
                nextStop,
                maxCostForStop.getCost(),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.INCOMPLETE
        );
    }

    private TripResult calculateCostForCancelledTrip(TapDetail currentTap, TapDetail nextTap) {
        return new TripResult(
                currentTap.getDateTimeUTC(),
                nextTap.getDateTimeUTC(),
                getElapsedTime(currentTap, nextTap),
                currentTap.getStopId(),
                nextTap.getStopId(),
                BigDecimal.ZERO,
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.CANCELLED
        );
    }

    boolean isIncomplete(TapDetail currentTap, TapDetail nextTap) {
        return nextTap == null || (currentTap.getTapType() == TapType.ON && currentTap.getTapType() == nextTap.getTapType());
    }

    boolean isCancelled(TapDetail currentTap, TapDetail nextTap) {
        return currentTap.getTapType() == TapType.ON && nextTap.getTapType() == TapType.OFF && currentTap.getStopId() == nextTap.getStopId();
    }

    TravelPrice getMaxCostForStop(StopId stopId) {
        return travelPrices.entrySet().stream()
                .filter(travelPrice -> travelPrice.getKey().getStops().contains(stopId))
                .max(Map.Entry.comparingByValue())
                .map(entry -> new TravelPrice(entry.getKey(), entry.getValue()))
                .orElseThrow();
    }

    private long getElapsedTime(TapDetail currentTap, TapDetail nextTap) {
        return nextTap.getDateTimeUTC().toEpochSecond() - currentTap.getDateTimeUTC().toEpochSecond();
    }

    private static StopId[] convertStopsToArray(Set<StopId> stopSet) {
        StopId[] stops = new StopId[2];
        stopSet.toArray(stops);
        return stops;
    }

}
