package no.capraconsulting.siren

import java.io.Serializable
import java.util.Collections.emptyList
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls

class Field private constructor(
    val name: String,
    private val _clazz: List<String>?,
    val type: String?,
    val title: String?,
    val value: Any?
) : Serializable {

    val clazz: List<String> get() = _clazz ?: emptyList()

    internal fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.NAME] = name
            this[Siren.CLASS] = _clazz
            this[Siren.TYPE] = type
            this[Siren.TITLE] = title
            this[Siren.VALUE] = value
        }.skipNulls()

    class Builder internal constructor(private val name: String) {
        private var clazz: List<String>? = null
        private var type: String? = null
        private var title: String? = null
        private var value: Any? = null

        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }
        fun clazz(vararg clazz: String): Builder = clazz(listOf(*clazz))
        fun type(type: String?) = apply { this.type = type }
        fun type(type: Type?) = apply { this.type = type?.value }
        fun title(title: String?) = apply { this.title = title }
        fun value(value: Any?) = apply { this.value = value }

        // TODO: Ensure immutability
        fun build(): Field = Field(name, clazz, type, title, value)
    }

    enum class Type constructor(val value: String) {
        HIDDEN("hidden"),
        TEXT("text"),
        SEARCH("search"),
        TEL("tel"),
        URL("url"),
        EMAIL("email"),
        PASSWORD("password"),
        DATETIME("datetime"),
        DATE("date"),
        MONTH("month"),
        WEEK("week"),
        TIME("time"),
        DATETIME_LOCAL("datetime-local"),
        NUMBER("number"),
        RANGE("range"),
        COLOR("color"),
        CHECKBOX("checkbox"),
        RADIO("radio"),
        FILE("file")
    }

    companion object {
        private const val serialVersionUID = -4600180928453411445L

        internal fun fromRaw(map: Any?): Field = fromRaw(map!!.asMap())

        private fun fromRaw(map: Map<String, Any?>): Field = Field
            .newBuilder(map[Siren.NAME] as String)
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .type(map[Siren.TYPE] as String?)
            .title(map[Siren.TITLE] as String?)
            .value(map[Siren.VALUE])
            .build()

        @JvmStatic
        fun newBuilder(name: String): Builder = Builder(name)
    }
}
