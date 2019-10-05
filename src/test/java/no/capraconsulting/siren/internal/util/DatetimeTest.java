package no.capraconsulting.siren.internal.util;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class DatetimeTest {
    private static final String UTC = "2015-12-14T09:01:10.587Z";
    private static final String CET = "2015-12-14T10:01:10.587+01:00[Europe/Oslo]";

    @Test
    public void testFromInstantUTC() {
        assertEquals(UTC, Datetime.from(Instant.parse(UTC)).toString());
    }

    @Test
    public void testFromInstantCET() {
        Instant instant = ZonedDateTime.parse(CET).toInstant();
        assertEquals(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC), Datetime.from(instant));
    }

    @Test
    public void testFromStringUTC() {
        assertEquals(UTC, Datetime.from(UTC).toString());
    }

    @Test
    public void testFromStringCET() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(CET);
        assertEquals(zonedDateTime, Datetime.from(CET));
    }
}
