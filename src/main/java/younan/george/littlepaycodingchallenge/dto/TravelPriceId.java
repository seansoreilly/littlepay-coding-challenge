package younan.george.littlepaycodingchallenge.dto;

import lombok.NonNull;
import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;

import java.util.HashSet;
import java.util.Set;

@Value
public class TravelPriceId {

    @NonNull
    private Set<StopId> stops;

    public TravelPriceId(StopId stop1, StopId stop2) {
        this.stops = new HashSet<>(2);
        this.stops.add(stop1);
        this.stops.add(stop2);
    }
}
