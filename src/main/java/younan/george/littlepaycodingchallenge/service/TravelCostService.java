package younan.george.littlepaycodingchallenge.service;

import org.springframework.stereotype.Service;
import younan.george.littlepaycodingchallenge.dto.TapDetail;
import younan.george.littlepaycodingchallenge.dto.TripResult;
import younan.george.littlepaycodingchallenge.enums.TripStatus;

import java.math.BigDecimal;

@Service
public class TravelCostService {

    public TripResult calculateCost(TapDetail currentTap, TapDetail nextTap) {
        return new TripResult(
                currentTap.getDateTimeUTC(),
                nextTap.getDateTimeUTC(),
                900,
                currentTap.getStopId(),
                nextTap.getStopId(),
                new BigDecimal("3.25"),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.COMPLETED
        );
    }
}
