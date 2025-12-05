package no.capraconsulting.siren.internal.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.ZonedDateTime
import no.capraconsulting.siren.internal.util.toFormattedString
import no.capraconsulting.siren.internal.util.toZonedDateTime

/** Custom json serializer and deserializer for the [ZonedDateTime] class. */
internal class JsonZonedDateTimeConverter :
    JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
  override fun deserialize(
      element: JsonElement,
      type: Type,
      context: JsonDeserializationContext,
  ): ZonedDateTime {
    return element.asString.toZonedDateTime()
  }

  override fun serialize(
      value: ZonedDateTime,
      type: Type,
      context: JsonSerializationContext,
  ): JsonElement {
    return JsonPrimitive(value.toFormattedString())
  }
}
