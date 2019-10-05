@file:JvmName("GenericsUtil")
package no.capraconsulting.siren.internal.util

internal fun objectAsList(value: Any): List<Any?> {
    require(value is List<*>) { "Casting to List failed. Found type ${value.javaClass}" }
    return value
}

internal fun objectAsStringList(value: Any): List<String?> {
    require(value is List<*>) { "Casting to List failed. Found type ${value.javaClass}" }
    value.forEach { item ->
        require(item is String?) { "Casting to List of Strings. Found item ${item?.javaClass}" }
    }

    @Suppress("UNCHECKED_CAST")
    return value as List<String?>
}

internal fun objectAsMap(value: Any): Map<String, Any?> {
    require(value is Map<*, *>) { "Casting to Map failed. Found type ${value.javaClass}" }

    @Suppress("UNCHECKED_CAST")
    return value as Map<String, Any>
}
