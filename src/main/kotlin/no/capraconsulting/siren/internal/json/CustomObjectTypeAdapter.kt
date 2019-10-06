package no.capraconsulting.siren.internal.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.util.ArrayList

/**
 * A special adapter to override the built-in Object adapter, which cannot be overridden
 * in GSON. This adapter covers special logic for mapping JSON values to Java objects.
 *
 * This file is based on com.google.gson.internal.bind.ObjectTypeAdapter.
 */
internal class CustomObjectTypeAdapter(private val gson: Gson) : TypeAdapter<Any?>() {

    override fun read(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                ArrayList<Any>().also { list ->
                    reader.beginArray()
                    while (reader.hasNext()) {
                        list.add(read(reader)!!)
                    }
                    reader.endArray()
                }
            }

            JsonToken.BEGIN_OBJECT -> {
                LinkedTreeMap<String, Any>().also { map ->
                    reader.beginObject()
                    while (reader.hasNext()) {
                        map[reader.nextName()] = read(reader)
                    }
                    reader.endObject()
                }
            }

            JsonToken.STRING -> reader.nextString()

            JsonToken.NUMBER -> {
                val next = reader.nextString()
                if (next.contains(".")) {
                    next.toDouble()
                } else {
                    next.toLong()
                }
            }

            JsonToken.BOOLEAN -> reader.nextBoolean()

            JsonToken.NULL -> {
                reader.nextNull()
                null
            }

            else -> throw IllegalStateException()
        }
    }

    override fun write(out: JsonWriter, value: Any?) {
        if (value == null) {
            out.nullValue()
            return
        }

        val typeAdapter = gson.getAdapter(value.javaClass) as TypeAdapter<Any?>
        if (typeAdapter is CustomObjectTypeAdapter) {
            out.beginObject()
            out.endObject()
            return
        }

        typeAdapter.write(out, value)
    }

    companion object {
        val FACTORY = object : TypeAdapterFactory {
            override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? =
                if (type.rawType == CustomObject::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    CustomObjectTypeAdapter(gson) as TypeAdapter<T>
                } else null
        }
    }
}
