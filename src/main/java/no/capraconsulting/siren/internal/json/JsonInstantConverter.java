package no.capraconsulting.siren.internal.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import no.capraconsulting.siren.internal.util.Datetime;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Custom json serializer for the {@link Instant Instant} class.
 */
final class JsonInstantConverter implements JsonSerializer<Instant> {
    @Override
    public JsonElement serialize(Instant value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(Json.toString(Datetime.from(value)));
    }
}
