package no.capraconsulting.siren.internal.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

private const val UTC = "2015-12-14T09:01:10.587Z"
private const val CET = "2015-12-14T10:01:10.587+01:00[Europe/Oslo]"

class DatetimeTest {

    @Test
    fun testFromInstantUTC() {
        assertEquals(UTC, Instant.parse(UTC).toZonedDateTime().toString())
    }

    @Test
    fun testFromInstantCET() {
        val instant = ZonedDateTime.parse(CET).toInstant()
        assertEquals(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC), instant.toZonedDateTime())
    }

    @Test
    fun testFromStringUTC() {
        assertEquals(UTC, UTC.toZonedDateTime().toString())
    }

    @Test
    fun testFromStringCET() {
        val zonedDateTime = ZonedDateTime.parse(CET)
        assertEquals(zonedDateTime, CET.toZonedDateTime())
    }
}
