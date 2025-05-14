package younan.george.littlepaycodingchallenge.dto;

import lombok.NonNull;
import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a unique identifier for travel prices between two stops.
 * This class is immutable and uses a set of stops to identify a route regardless of direction.
 */
@Value
public class TravelPriceId {

    /**
     * The set of stops that define this travel route.
     * This is an immutable set to prevent modification after creation.
     */
    @NonNull
    private Set<StopId> stops;

    /**
     * Creates a new TravelPriceId for travel between two stops.
     *
     * @param stop1 The first stop in the route
     * @param stop2 The second stop in the route
     */
    public TravelPriceId(StopId stop1, StopId stop2) {
        Set<StopId> tempSet = new HashSet<>(2);
        tempSet.add(stop1);
        tempSet.add(stop2);
        this.stops = Collections.unmodifiableSet(tempSet);
    }
}
