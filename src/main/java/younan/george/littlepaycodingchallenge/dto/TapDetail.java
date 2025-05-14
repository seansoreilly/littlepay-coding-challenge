package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;

import java.time.ZonedDateTime;

/**
 * Represents a tap event in the public transport system.
 * A tap event occurs when a passenger taps their card on or off a bus.
 * This class is immutable.
 */
@Value
public class TapDetail {
    /**
     * The unique identifier for this tap event
     */
    private int id;

    /**
     * The date and time when the tap event occurred, in UTC timezone
     */
    private ZonedDateTime dateTimeUTC;

    /**
     * The type of tap event (ON or OFF)
     */
    private TapType tapType;

    /**
     * The stop where the tap event occurred
     */
    private StopId stopId;

    /**
     * The ID of the company operating the service
     */
    private String companyId;

    /**
     * The ID of the bus where the tap event occurred
     */
    private String busId;

    /**
     * The Primary Account Number (credit card number) used for the tap event
     */
    private String pan;
}
