package no.capraconsulting.siren.internal.json

import no.capraconsulting.siren.internal.util.toFormattedString
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Collections

class JsonTest {

    @Test
    fun verifyNumberTypesDontChange() {
        val json = "{\"double\":123.0,\"wholeNumber\":123,\"scientific\":3.7E-5}"
        val parsed = json.parseJsonToMap()

        val result = parsed.toJson()
        assertEquals("Has not changed format", json, result)
    }

    @Test
    fun serializesNullValues() {
        val data = Collections.singletonMap<String, String>("something", null)
        val result = data.toJson()
        assertEquals("Contains null value", "{\"something\":null}", result)
    }

    @Test
    fun testZoneDateTimeToString() {
        assertEquals(
            "2019-01-01T02:23:59Z",
            ZonedDateTime.of(
                LocalDate.parse("2019-01-01"),
                LocalTime.of(2, 23, 59),
                ZoneOffset.UTC
            ).toFormattedString()
        )
        assertEquals(
            "2019-01-01T01:23:59Z",
            ZonedDateTime.of(
                LocalDate.parse("2019-01-01"),
                LocalTime.of(2, 23, 59),
                ZoneId.of("Europe/Oslo")
            ).toFormattedString()
        )
        assertEquals(
            "2019-01-01T01:23:59.028290833Z",
            ZonedDateTime.of(
                LocalDate.parse("2019-01-01"),
                LocalTime.of(2, 23, 59, 28290833),
                ZoneId.of("Europe/Oslo")
            ).toFormattedString()
        )
        assertEquals(
            "2016-08-22T12:30:00.120Z",
            ZonedDateTime.parse("2016-08-22T14:30:00.120+02:00[Europe/Paris]").toFormattedString()
        )
    }

}
