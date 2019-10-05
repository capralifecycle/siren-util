package no.capraconsulting.siren.internal.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.ZonedDateTime

private val GSON: Gson = GsonBuilder()
    // Required for correct key serialization
    .enableComplexMapKeySerialization()
    // Required for correct ZonedDateTime serialization
    .registerTypeAdapter(ZonedDateTime::class.java, JsonZonedDateTimeConverter())
    // Required for correct Instant serialization
    .registerTypeAdapter(Instant::class.java, JsonInstantConverter())
    // For custom deserialization logic. Note that the other type adapters
    // registered above are not used for fromJsonToMap as it uses a type
    // adapter for Object, and not the specific types that the type
    // adapters covers.
    .registerTypeAdapterFactory(CustomObjectTypeAdapter.FACTORY)
    // Required for correct serialization of nulls
    .serializeNulls()
    .create()

internal fun String.parseJsonToMap(): Map<String, Any?> =
    GSON.fromJson(this, object : TypeToken<Map<String, CustomObject>>() {}.type)

internal fun Any?.toJson(): String = GSON.toJson(this)
