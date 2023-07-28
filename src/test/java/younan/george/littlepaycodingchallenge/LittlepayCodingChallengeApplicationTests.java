package younan.george.littlepaycodingchallenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class LittlepayCodingChallengeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void shouldWriteToSysout() throws Exception {
		List<String> expectedCsvContent = Arrays.asList("Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status",
				"2023-01-22T13:00Z,2023-01-22T13:05Z,300,STOP1,STOP2,3.25,Company1,Bus37,COMPLETED",
				"2023-01-22T13:05Z,2023-01-22T09:20Z,-13500,STOP2,STOP3,5.50,Company1,Bus37,COMPLETED",
				"2023-01-22T09:20Z,2023-01-22T09:20Z,0,STOP3,STOP1,7.30,Company1,Bus36,INCOMPLETE",
				"2023-01-23T08:00Z,2023-01-23T08:02Z,120,STOP1,STOP1,0,Company1,Bus37,CANCELLED",
				"2023-01-23T08:02Z,2023-01-24T16:30Z,116880,STOP1,STOP2,3.25,Company1,Bus37,COMPLETED");

		List<String> strings = Files.readAllLines(ResourceUtils.getFile("output.csv").toPath());

		assertThat(strings, equalTo(expectedCsvContent));
	}

}
