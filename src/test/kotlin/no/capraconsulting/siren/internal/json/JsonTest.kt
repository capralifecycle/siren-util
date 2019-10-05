package no.capraconsulting.siren.internal.json

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
        val parsed = Json.fromJsonToMap(json)

        val result = Json.toJson(parsed)
        assertEquals("Has not changed format", json, result)
    }

    @Test
    fun serializesNullValues() {
        val data = Collections.singletonMap<String, String>("something", null)
        val result = Json.toJson(data)
        assertEquals("Contains null value", "{\"something\":null}", result)
    }

    @Test
    fun testZoneDateTimeToString() {
        assertEquals(
            "2019-01-01T02:23:59Z",
            Json.toString(
                ZonedDateTime.of(
                    LocalDate.parse("2019-01-01"),
                    LocalTime.of(2, 23, 59),
                    ZoneOffset.UTC
                )
            )
        )
        assertEquals(
            "2019-01-01T01:23:59Z",
            Json.toString(
                ZonedDateTime.of(
                    LocalDate.parse("2019-01-01"),
                    LocalTime.of(2, 23, 59),
                    ZoneId.of("Europe/Oslo")
                )
            )
        )
        assertEquals(
            "2019-01-01T01:23:59.028290833Z",
            Json.toString(
                ZonedDateTime.of(
                    LocalDate.parse("2019-01-01"),
                    LocalTime.of(2, 23, 59, 28290833),
                    ZoneId.of("Europe/Oslo")
                )
            )
        )
        assertEquals(
            "2016-08-22T12:30:00.120Z",
            Json.toString(
                ZonedDateTime.parse("2016-08-22T14:30:00.120+02:00[Europe/Paris]")
            )
        )
    }

}
