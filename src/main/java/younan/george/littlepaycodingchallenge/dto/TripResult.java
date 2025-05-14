package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Represents the result of a trip, including details about the start and end times,
 * locations, cost, and status.
 * This class is immutable.
 */
@Value
public class TripResult {
    /**
     * The date and time when the trip started (tap on event)
     */
    private ZonedDateTime started;

    /**
     * The date and time when the trip ended (tap off event)
     * For incomplete trips, this may be the same as the start time
     */
    private ZonedDateTime finished;

    /**
     * The duration of the trip in seconds
     * For incomplete trips, this may be 0
     */
    private long durationSecs;

    /**
     * The stop ID where the trip started
     */
    private StopId fromStopId;

    /**
     * The stop ID where the trip ended
     * For incomplete trips, this is the inferred destination
     */
    private StopId toStopId;

    /**
     * The amount charged for the trip
     * For cancelled trips, this is 0
     * For incomplete trips, this is the maximum possible fare
     */
    private BigDecimal chargeAmount;

    /**
     * The ID of the company operating the service
     */
    private String companyId;

    /**
     * The ID of the bus used for the trip
     */
    private String busID;

    /**
     * The Primary Account Number (credit card number) used for payment
     */
    private String pan;

    /**
     * The status of the trip (COMPLETED, INCOMPLETE, or CANCELLED)
     */
    private TripStatus status;
}
