package younan.george.littlepaycodingchallenge.dto;

import lombok.Value;
import younan.george.littlepaycodingchallenge.enums.StopId;
import younan.george.littlepaycodingchallenge.enums.TapType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

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
    @Min(value = 1, message = "ID must be positive")
    private int id;

    /**
     * The date and time when the tap event occurred, in UTC timezone
     */
    @NotNull(message = "Date and time cannot be null")
    @PastOrPresent(message = "Date and time must be in the past or present")
    private ZonedDateTime dateTimeUTC;

    /**
     * The type of tap event (ON or OFF)
     */
    @NotNull(message = "Tap type cannot be null")
    private TapType tapType;

    /**
     * The stop where the tap event occurred
     */
    @NotNull(message = "Stop ID cannot be null")
    private StopId stopId;

    /**
     * The ID of the company operating the service
     */
    @NotBlank(message = "Company ID cannot be blank")
    private String companyId;

    /**
     * The ID of the bus where the tap event occurred
     */
    @NotBlank(message = "Bus ID cannot be blank")
    private String busId;

    /**
     * The Primary Account Number (credit card number) used for the tap event
     */
    @NotBlank(message = "PAN cannot be blank")
    private String pan;
}
