package no.capraconsulting.siren.internal.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Utility class for serializing and deserializing of json strings.
 */
public final class Json {
    private static final Gson GSON;

    private Json() {}

    static {
        GsonBuilder builder = new GsonBuilder();

        // Required for correct key serialization
        builder.enableComplexMapKeySerialization();
        // Required for correct ZonedDateTime serialization
        builder.registerTypeAdapter(ZonedDateTime.class, new JsonZonedDateTimeConverter());
        // Required for correct Instant serialization
        builder.registerTypeAdapter(Instant.class, new JsonInstantConverter());

        // For custom deserialization logic. Note that the other type adapters
        // registered above are not used for fromJsonToMap as it uses a type
        // adapter for Object, and not the specific types that the type
        // adapters covers.
        builder.registerTypeAdapterFactory(CustomObjectTypeAdapter.FACTORY);

        // Required for correct serialization of nulls
        builder.serializeNulls();

        GSON = builder.create();
    }

    public static Map<String, Object> fromJsonToMap(final String value) throws JsonParseException {
        return GSON.fromJson(value, new TypeToken<Map<String, CustomObject>>() {}.getType());
    }

    public static String toString(final ZonedDateTime value) {
        return value.format(DateTimeFormatter.ISO_INSTANT);
    }

    public static String toJson(final Object value) {
        return GSON.toJson(value);
    }
}

