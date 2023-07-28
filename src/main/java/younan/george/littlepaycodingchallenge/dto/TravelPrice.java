package younan.george.littlepaycodingchallenge.dto;

import lombok.NonNull;
import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;

import java.math.BigDecimal;

@Value
public class TravelPrice {
    @NonNull
    private StopId stop1;
    @NonNull
    private StopId stop2;
    @NonNull
    private BigDecimal cost;

    /**
     * Considers objects as equal if cost and stops match, ignoring the direction of stops.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TravelPrice other = (TravelPrice) o;

        return this.cost.equals(other.cost) &&
                ((this.stop1 == other.stop1 && this.stop2 == other.stop2) || (this.stop1 == other.stop2 && this.stop2 == other.stop1));

    }

    /**
     *
     * @return a copy of this object where stop 1 becomes stop 2 and vice versa.
     */
    public TravelPrice reverseStops() {
        return new TravelPrice(stop2, stop1, cost);
    }
}
