package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

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
    @NotNull(message = "Start time cannot be null")
    @PastOrPresent(message = "Start time must be in the past or present")
    private ZonedDateTime started;

    /**
     * The date and time when the trip ended (tap off event)
     * For incomplete trips, this may be the same as the start time
     */
    @NotNull(message = "End time cannot be null")
    @PastOrPresent(message = "End time must be in the past or present")
    private ZonedDateTime finished;

    /**
     * The duration of the trip in seconds
     * For incomplete trips, this may be 0
     */
    @Min(value = 0, message = "Duration cannot be negative")
    private long durationSecs;

    /**
     * The stop ID where the trip started
     */
    @NotNull(message = "From stop ID cannot be null")
    private StopId fromStopId;

    /**
     * The stop ID where the trip ended
     * For incomplete trips, this is the inferred destination
     */
    @NotNull(message = "To stop ID cannot be null")
    private StopId toStopId;

    /**
     * The amount charged for the trip
     * For cancelled trips, this is 0
     * For incomplete trips, this is the maximum possible fare
     */
    @NotNull(message = "Charge amount cannot be null")
    private BigDecimal chargeAmount;

    /**
     * The ID of the company operating the service
     */
    @NotBlank(message = "Company ID cannot be blank")
    private String companyId;

    /**
     * The ID of the bus used for the trip
     */
    @NotBlank(message = "Bus ID cannot be blank")
    private String busID;

    /**
     * The Primary Account Number (credit card number) used for payment
     */
    @NotBlank(message = "PAN cannot be blank")
    private String pan;

    /**
     * The status of the trip (COMPLETED, INCOMPLETE, or CANCELLED)
     */
    @NotNull(message = "Trip status cannot be null")
    private TripStatus status;
}
