package no.capraconsulting.siren.internal.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GenericsUtil {
    private GenericsUtil() {}

    @NotNull
    public static List<Object> objectAsList(@NotNull final Object value) {
        if (!(value instanceof List)) {
            //noinspection ConstantConditions
            throw new IllegalArgumentException("Casting to List failed. Found type " + (value == null ? "null" : value.getClass()));
        }

        //noinspection unchecked
        return (List<Object>) value;
    }

    @NotNull
    public static List<String> objectAsStringList(@NotNull final Object value) {
        if (!(value instanceof List)) {
            //noinspection ConstantConditions
            throw new IllegalArgumentException("Casting to List failed. Found type " + (value == null ? "null" : value.getClass()));
        }

        //noinspection unchecked
        ((List<Object>) value).forEach(item -> {
            if (!(item instanceof String)) {
                throw new IllegalArgumentException("Casting to List of Strings. Found item " + item.getClass());
            }
        });

        //noinspection unchecked
        return (List<String>) value;
    }

    @NotNull
    public static Map<String, Object> objectAsMap(@NotNull final Object value) {
        if (!(value instanceof Map)) {
            //noinspection ConstantConditions
            throw new IllegalArgumentException("Casting to Map failed. Found type " + (value == null ? "null" : value.getClass()));
        }

        //noinspection unchecked
        return (Map<String, Object>) value;
    }
}
