package younan.george.littlepaycodingchallenge.service;

import org.springframework.stereotype.Service;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TravelPrice;
import younan.george.littlepaycodingchallenge.dto.TravelPriceId;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;
import younan.george.littlepaycodingchallenge.enums.TripStatus;
import younan.george.littlepaycodingchallenge.exception.InvalidTapException;
import younan.george.littlepaycodingchallenge.exception.InvalidTripException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Service responsible for calculating travel costs based on tap events.
 * This service handles the core business logic for determining trip status
 * (completed, incomplete, or cancelled) and calculating the appropriate fare.
 */
@Service
public class TravelCostService {
    /**
     * Map of travel routes to their corresponding prices.
     * The key is a TravelPriceId which represents a route between two stops.
     * The value is the price for that route.
     */
    private HashMap<TravelPriceId, BigDecimal> travelPrices;

    /**
     * Initializes the service with predefined travel prices between stops.
     * In a production environment, these prices would typically be loaded from
     * a configuration file or database.
     */
    public TravelCostService() {
        travelPrices = new HashMap<>();
        travelPrices.put(new TravelPriceId(StopId.STOP1, StopId.STOP2), new BigDecimal("3.25"));
        travelPrices.put(new TravelPriceId(StopId.STOP2, StopId.STOP3), new BigDecimal("5.50"));
        travelPrices.put(new TravelPriceId(StopId.STOP1, StopId.STOP3), new BigDecimal("7.30"));
    }

    /**
     * Calculates the cost for all tap events in the provided list.
     * This method processes the list sequentially, pairing each tap with the next one
     * to determine trip status and cost.
     *
     * @param tapDetails List of tap events to process
     * @return List of trip results with calculated costs and statuses
     */
    public List<TripResult> calculateCostForAll(List<TapDetail> tapDetails) {
        int lastIndex = tapDetails.size() - 1;

        return IntStream.range(0, lastIndex)
                .mapToObj(index ->
                        calculateCost(
                                tapDetails.get(index),
                                index < lastIndex ? tapDetails.get(index + 1) : null))
                .toList();
    }

    /**
     * Calculates the cost for a single trip based on the current tap and the next tap.
     * This method determines if the trip is completed, incomplete, or cancelled,
     * and calculates the appropriate cost.
     *
     * @param currentTap The current tap event
     * @param nextTap The next tap event, or null if there is no next tap
     * @return A TripResult containing the calculated cost and trip status
     * @throws InvalidTapException if currentTap is null
     * @throws InvalidTripException if the travel cost between stops is unknown
     */
    public TripResult calculateCost(TapDetail currentTap, TapDetail nextTap) {
        if (currentTap == null) {
            throw new younan.george.littlepaycodingchallenge.exception.InvalidTapException("currentTap must be non null!");
        }
        if (isIncomplete(currentTap, nextTap)) {
            return calculateCostForIncompleteTrip(currentTap);
        }
        if (isCancelled(currentTap, nextTap)) {
            return calculateCostForCancelledTrip(currentTap, nextTap);
        }

        return calculateCostForCompletedTrip(currentTap, nextTap);
    }

    /**
     * Calculates the cost for a completed trip.
     * A completed trip is one where the passenger tapped on at one stop and tapped off at another stop.
     *
     * @param currentTap The tap-on event
     * @param nextTap The tap-off event
     * @return A TripResult with the COMPLETED status and the appropriate charge
     * @throws InvalidTripException if the travel cost between the stops is unknown
     */
    private TripResult calculateCostForCompletedTrip(TapDetail currentTap, TapDetail nextTap) {
        BigDecimal chargeAmount = travelPrices.get(new TravelPriceId(currentTap.getStopId(), nextTap.getStopId()));
        if (chargeAmount == null) {
            throw new InvalidTripException("Unknown travel cost between stops " + currentTap.getStopId() + ", " + nextTap.getStopId(), 
                currentTap, nextTap);
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

    /**
     * Calculates the cost for an incomplete trip.
     * An incomplete trip is one where the passenger tapped on but did not tap off.
     * In this case, the maximum possible fare from the tap-on stop is charged.
     *
     * @param currentTap The tap-on event
     * @return A TripResult with the INCOMPLETE status and the maximum possible charge
     */
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

    /**
     * Calculates the cost for a cancelled trip.
     * A cancelled trip is one where the passenger tapped on and then tapped off at the same stop.
     * In this case, no charge is applied.
     *
     * @param currentTap The tap-on event
     * @param nextTap The tap-off event at the same stop
     * @return A TripResult with the CANCELLED status and zero charge
     */
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

    /**
     * Determines if a trip is incomplete based on the current and next tap events.
     * A trip is incomplete if there is no next tap, or if both the current and next taps are ON taps.
     *
     * @param currentTap The current tap event
     * @param nextTap The next tap event, or null if there is no next tap
     * @return true if the trip is incomplete, false otherwise
     */
    boolean isIncomplete(TapDetail currentTap, TapDetail nextTap) {
        return nextTap == null || (currentTap.getTapType() == TapType.ON && currentTap.getTapType() == nextTap.getTapType());
    }

    /**
     * Determines if a trip is cancelled based on the current and next tap events.
     * A trip is cancelled if the current tap is ON, the next tap is OFF, and both taps are at the same stop.
     *
     * @param currentTap The current tap event
     * @param nextTap The next tap event
     * @return true if the trip is cancelled, false otherwise
     */
    boolean isCancelled(TapDetail currentTap, TapDetail nextTap) {
        return currentTap.getTapType() == TapType.ON && nextTap.getTapType() == TapType.OFF && currentTap.getStopId() == nextTap.getStopId();
    }

    /**
     * Gets the maximum cost for travel from a given stop.
     * This is used for incomplete trips where the destination is unknown.
     *
     * @param stopId The stop ID to get the maximum cost for
     * @return A TravelPrice object containing the maximum cost and the corresponding route
     * @throws java.util.NoSuchElementException if no travel prices are defined for the given stop
     */
    TravelPrice getMaxCostForStop(StopId stopId) {
        return travelPrices.entrySet().stream()
                .filter(travelPrice -> travelPrice.getKey().getStops().contains(stopId))
                .max(Map.Entry.comparingByValue())
                .map(entry -> new TravelPrice(entry.getKey(), entry.getValue()))
                .orElseThrow();
    }

    /**
     * Calculates the elapsed time between two tap events in seconds.
     *
     * @param currentTap The first tap event
     * @param nextTap The second tap event
     * @return The elapsed time in seconds
     */
    private long getElapsedTime(TapDetail currentTap, TapDetail nextTap) {
        return nextTap.getDateTimeUTC().toEpochSecond() - currentTap.getDateTimeUTC().toEpochSecond();
    }

    /**
     * Converts a set of stop IDs to an array.
     * This is a utility method used for processing stop IDs in the incomplete trip calculation.
     *
     * @param stopSet The set of stop IDs to convert
     * @return An array containing the stop IDs from the set
     */
    private static StopId[] convertStopsToArray(Set<StopId> stopSet) {
        StopId[] stops = new StopId[2];
        stopSet.toArray(stops);
        return stops;
    }
}
