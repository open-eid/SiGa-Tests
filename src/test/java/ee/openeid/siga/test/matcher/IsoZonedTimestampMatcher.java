package ee.openeid.siga.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class IsoZonedTimestampMatcher extends TypeSafeMatcher<String> {

    @Override
    protected boolean matchesSafely(String timestamp) {
        Instant timestampInstant = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant();
        Instant now = Instant.now();
        Instant oneHourBefore = now.minusSeconds(3600);
        Instant oneHourAfter = now.plusSeconds(3600);
        return !timestampInstant.isBefore(oneHourBefore) && !timestampInstant.isAfter(oneHourAfter);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Timestamp within one hour of the current time");
    }

    @Override
    protected void describeMismatchSafely(String dateString, Description mismatchDescription) {
        mismatchDescription.appendText(dateString + " is not within one hour of the current time");
    }

    public static IsoZonedTimestampMatcher withinOneHourOfCurrentTime() {
        return new IsoZonedTimestampMatcher();
    }
}
