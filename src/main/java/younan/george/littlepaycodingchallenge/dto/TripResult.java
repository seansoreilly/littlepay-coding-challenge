package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
public class TripResult {
    private ZonedDateTime started;
    private ZonedDateTime finished;
    private long durationSecs;
    private StopId fromStopId;
    private StopId toStopId;
    private BigDecimal chargeAmount;
    private String companyId;
    private String busID;
    private String pan;
    private TripStatus status;
}