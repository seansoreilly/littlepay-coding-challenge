package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;

import java.math.BigDecimal;

@Value
public class TravelPrice {
    private StopId from;
    private StopId to;
    private BigDecimal cost;
}
