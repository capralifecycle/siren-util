@file:JvmName("MapUtil")
package no.capraconsulting.siren.internal.util

@Deprecated("Use extension method",
    ReplaceWith("map.skipNulls()", "no.capraconsulting.siren.internal.util.MapUtil.skipNulls")
)
internal fun <K, V> skipNulls(map: Map<K, V>): Map<K, V> =
    map.skipNulls()

@JvmName("skipNullsFuture")
internal fun <K, V> Map<K, V>.skipNulls() =
    filterNot { it.value == null }

internal fun notNull(map: Map<String, Any>, key: String): Boolean {
    return map.containsKey(key) && map[key] != null
}
