package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;

import java.time.ZonedDateTime;

@Value
public class TapDetail {
    private int id;
    private ZonedDateTime dateTimeUTC;
    private TapType tapType;
    private StopId stopId;
    private String companyId;
    private String busId;
    private String pan;
}


