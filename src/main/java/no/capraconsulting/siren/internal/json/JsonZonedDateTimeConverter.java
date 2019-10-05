package no.capraconsulting.siren.internal.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import no.capraconsulting.siren.internal.util.Datetime;

/**
 * Custom json serializer and deserializer for the {@link ZonedDateTime ZonedDateTime} class.
 */
final class JsonZonedDateTimeConverter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        return Datetime.from(element.getAsString());
    }

    @Override
    public JsonElement serialize(ZonedDateTime value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(Json.toString(value));
    }
}
