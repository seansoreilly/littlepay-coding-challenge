package younan.george.littlepaycodingchallenge.dto;

import org.junit.jupiter.api.Test;
import younan.george.littlepaycodingchallenge.enums.StopId;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TravelPriceTest {

    @Test
    void shouldNotBeEqualForDifferentStops() {
        TravelPrice betweenStop1And2 = new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.25"));
        TravelPrice betweenStop1And3 = new TravelPrice(StopId.STOP_1, StopId.STOP_3, new BigDecimal("7.30"));
        TravelPrice betweenStop2And3 = new TravelPrice(StopId.STOP_2, StopId.STOP_3, new BigDecimal("5.50"));

        assertThat(betweenStop1And2.equals(betweenStop1And3), is(false));
        assertThat(betweenStop1And2.equals(betweenStop2And3), is(false));
        assertThat(betweenStop1And3.equals(betweenStop2And3), is(false));
    }

    @Test
    void shouldNotBeEqualForDifferentCosts() {
        TravelPrice travelPrice = new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.25"));
        TravelPrice sameStopsHigherCost = new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.26"));
        TravelPrice sameStopsLowerCost = new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.24"));

        assertThat(travelPrice.equals(sameStopsHigherCost), is(false));
        assertThat(travelPrice.equals(sameStopsLowerCost), is(false));
    }

    @Test
    void shouldBeEqualRegardlessOfStopDirection() {
        TravelPrice betweenStop1And2 = new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.25"));
        assertThat(betweenStop1And2, equalTo(new TravelPrice(StopId.STOP_1, StopId.STOP_2, new BigDecimal("3.25"))));
        assertThat(betweenStop1And2, equalTo(new TravelPrice(StopId.STOP_2, StopId.STOP_1, new BigDecimal("3.25"))));

        TravelPrice betweenStop1And3 = new TravelPrice(StopId.STOP_1, StopId.STOP_3, new BigDecimal("7.30"));
        assertThat(betweenStop1And3, equalTo(new TravelPrice(StopId.STOP_3, StopId.STOP_1, new BigDecimal("7.30"))));
        assertThat(betweenStop1And3, equalTo(new TravelPrice(StopId.STOP_3, StopId.STOP_1, new BigDecimal("7.30"))));

        TravelPrice betweenStop2And3 = new TravelPrice(StopId.STOP_2, StopId.STOP_3, new BigDecimal("5.50"));
        assertThat(betweenStop2And3, equalTo(new TravelPrice(StopId.STOP_2, StopId.STOP_3, new BigDecimal("5.50"))));
        assertThat(betweenStop2And3, equalTo(new TravelPrice(StopId.STOP_3, StopId.STOP_2, new BigDecimal("5.50"))));
    }

}