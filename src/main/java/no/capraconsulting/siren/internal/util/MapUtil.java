package no.capraconsulting.siren.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MapUtil {
    private MapUtil() {}

    public static <K, V> Map<K, V> skipNulls(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (v != null) {
                result.put(k, v);
            }
        });
        return result;
    }

    public static boolean notNull(Map<String, Object> map, String key) {
        return map.containsKey(key) && map.get(key) != null;
    }
}
