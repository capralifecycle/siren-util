package no.capraconsulting.siren.internal.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for working with dates.
 */
public final class Datetime {
    private Datetime() {}

    public static ZonedDateTime from(final Instant timestamp) {
        return ZonedDateTime.ofInstant(timestamp, ZoneOffset.UTC);
    }

    public static ZonedDateTime from(final String value) {
        try {
            return ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch (Exception e) {
            throw new IllegalArgumentException(value);
        }
    }
}
