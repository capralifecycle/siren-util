package no.capraconsulting.siren.internal.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DatetimeTest {

    @Test
    fun testFromInstantUTC() {
        assertEquals(UTC, Datetime.from(Instant.parse(UTC)).toString())
    }

    @Test
    fun testFromInstantCET() {
        val instant = ZonedDateTime.parse(CET).toInstant()
        assertEquals(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC), Datetime.from(instant))
    }

    @Test
    fun testFromStringUTC() {
        assertEquals(UTC, Datetime.from(UTC).toString())
    }

    @Test
    fun testFromStringCET() {
        val zonedDateTime = ZonedDateTime.parse(CET)
        assertEquals(zonedDateTime, Datetime.from(CET))
    }

    companion object {
        private const val UTC = "2015-12-14T09:01:10.587Z"
        private const val CET = "2015-12-14T10:01:10.587+01:00[Europe/Oslo]"
    }
}
