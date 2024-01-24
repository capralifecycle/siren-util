package no.capraconsulting.siren.internal.json

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Collections
import no.capraconsulting.siren.internal.util.toFormattedString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JsonTest {

  @Test
  fun verifyNumberTypesDontChange() {
    val json = "{\"double\":123.0,\"wholeNumber\":123,\"scientific\":3.7E-5}"
    val parsed = json.parseJsonToMap()

    val result = parsed.toJson()

    assertThat(result).`as` { "Has not changed format" }.isEqualTo(json)
  }

  @Test
  fun serializesNullValues() {
    val data = Collections.singletonMap<String, String>("something", null)
    val result = data.toJson()
    assertThat(result).`as` { "Contains null value" }.isEqualTo("{\"something\":null}")
  }

  @Test
  fun testZoneDateTimeToString() {
    val dateString1 =
        ZonedDateTime.of(LocalDate.parse("2019-01-01"), LocalTime.of(2, 23, 59), ZoneOffset.UTC)
            .toFormattedString()

    assertThat(dateString1).isEqualTo("2019-01-01T02:23:59Z")

    val dateString2 =
        ZonedDateTime.of(
                LocalDate.parse("2019-01-01"), LocalTime.of(2, 23, 59), ZoneId.of("Europe/Oslo"))
            .toFormattedString()

    assertThat(dateString2).isEqualTo("2019-01-01T01:23:59Z")

    val dateString3 =
        ZonedDateTime.of(
                LocalDate.parse("2019-01-01"),
                LocalTime.of(2, 23, 59, 28290833),
                ZoneId.of("Europe/Oslo"))
            .toFormattedString()

    assertThat(dateString3).isEqualTo("2019-01-01T01:23:59.028290833Z")

    val dateString4 =
        ZonedDateTime.parse("2016-08-22T14:30:00.120+02:00[Europe/Paris]").toFormattedString()
    assertThat(dateString4).isEqualTo("2016-08-22T12:30:00.120Z")
  }
}
