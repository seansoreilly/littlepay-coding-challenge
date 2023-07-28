package younan.george.littlepaycodingchallenge.dto;

import org.junit.jupiter.api.Test;
import younan.george.littlepaycodingchallenge.enums.StopId;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// These tests might seem overkill since the equality is handled by Lombok.
// I chose to keep them since it's vital that the direction of stops is ignored for equality checks, as we use this ID as a key when retrieving prices from a HashMap.
class TravelPriceIdTest {

    @Test
    void shouldNotBeEqualForDifferentStops() {
        TravelPriceId betweenStop1And2 = new TravelPriceId(StopId.STOP_1, StopId.STOP_2);
        TravelPriceId betweenStop1And3 = new TravelPriceId(StopId.STOP_1, StopId.STOP_3);
        TravelPriceId betweenStop2And3 = new TravelPriceId(StopId.STOP_2, StopId.STOP_3);

        assertThat(betweenStop1And2.equals(betweenStop1And3), is(false));
        assertThat(betweenStop1And2.equals(betweenStop2And3), is(false));
        assertThat(betweenStop1And3.equals(betweenStop2And3), is(false));
    }

    @Test
    void shouldBeEqualForSameStops() {
        assertThat(new TravelPriceId(StopId.STOP_1, StopId.STOP_2).equals(new TravelPriceId(StopId.STOP_1, StopId.STOP_2)), is(true));
        assertThat(new TravelPriceId(StopId.STOP_1, StopId.STOP_3).equals(new TravelPriceId(StopId.STOP_1, StopId.STOP_3)), is(true));
        assertThat(new TravelPriceId(StopId.STOP_2, StopId.STOP_3).equals(new TravelPriceId(StopId.STOP_2, StopId.STOP_3)), is(true));
    }

    @Test
    void shouldBeEqualRegardlessOfStopDirection() {
        TravelPriceId betweenStop1And2 = new TravelPriceId(StopId.STOP_1, StopId.STOP_2);
        assertThat(betweenStop1And2, equalTo(new TravelPriceId(StopId.STOP_1, StopId.STOP_2)));
        assertThat(betweenStop1And2, equalTo(new TravelPriceId(StopId.STOP_2, StopId.STOP_1)));

        TravelPriceId betweenStop1And3 = new TravelPriceId(StopId.STOP_1, StopId.STOP_3);
        assertThat(betweenStop1And3, equalTo(new TravelPriceId(StopId.STOP_3, StopId.STOP_1)));
        assertThat(betweenStop1And3, equalTo(new TravelPriceId(StopId.STOP_3, StopId.STOP_1)));

        TravelPriceId betweenStop2And3 = new TravelPriceId(StopId.STOP_2, StopId.STOP_3);
        assertThat(betweenStop2And3, equalTo(new TravelPriceId(StopId.STOP_2, StopId.STOP_3)));
        assertThat(betweenStop2And3, equalTo(new TravelPriceId(StopId.STOP_3, StopId.STOP_2)));
    }

}