package no.capraconsulting.siren.internal.json

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant
import no.capraconsulting.siren.internal.util.toFormattedString
import no.capraconsulting.siren.internal.util.toZonedDateTime

/** Custom json serializer for the [Instant] class. */
internal class JsonInstantConverter : JsonSerializer<Instant> {
  override fun serialize(
      value: Instant,
      type: Type,
      context: JsonSerializationContext,
  ): JsonElement {
    return JsonPrimitive(value.toZonedDateTime().toFormattedString())
  }
}
