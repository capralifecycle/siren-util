package no.capraconsulting.siren.internal.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val UTC = "2015-12-14T09:01:10.587Z"
private const val CET = "2015-12-14T10:01:10.587+01:00[Europe/Oslo]"

class DatetimeTest {

  @Test
  fun testFromInstantUTC() {
    assertThat(Instant.parse(UTC).toZonedDateTime().toString()).isEqualTo(UTC)
  }

  @Test
  fun testFromInstantCET() {
    val instant = ZonedDateTime.parse(CET).toInstant()
    assertThat(instant.toZonedDateTime())
        .isEqualTo(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC))
  }

  @Test
  fun testFromStringUTC() {
    assertThat(UTC.toZonedDateTime().toString()).isEqualTo(UTC)
  }

  @Test
  fun testFromStringCET() {
    val zonedDateTime = ZonedDateTime.parse(CET)
    assertThat(CET.toZonedDateTime()).isEqualTo(zonedDateTime)
  }
}
