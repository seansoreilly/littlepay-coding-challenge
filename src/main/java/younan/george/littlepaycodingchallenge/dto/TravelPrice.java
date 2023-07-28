package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TravelPrice {
    private TravelPriceId travelPriceId;
    private BigDecimal cost;
}
