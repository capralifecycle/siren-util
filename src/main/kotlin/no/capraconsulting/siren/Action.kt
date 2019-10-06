package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.emptyList
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls

class Action private constructor(
    val name: String,
    private val _clazz: List<String>?,
    val method: String?,
    val href: URI,
    val title: String?,
    val type: String?,
    private val _fields: List<Field>?
) : Serializable {

    val clazz: List<String> get() = _clazz ?: emptyList()
    val fields: List<Field> get() = _fields ?: emptyList()

    internal fun toRaw(): Map<String, Any?> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.NAME] = name
            this[Siren.TITLE] = title
            this[Siren.CLASS] = _clazz
            this[Siren.METHOD] = method
            this[Siren.HREF] = href
            this[Siren.TYPE] = type
            this[Siren.FIELDS] = _fields?.map(Field::toRaw)
        }.skipNulls()

    class Builder internal constructor(private val name: String, private val href: URI) {
        private var clazz: List<String>? = null
        private var method: String? = null
        private var title: String? = null
        private var type: String? = null
        private var fields: List<Field>? = null

        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))
        fun method(method: String?) = apply { this.method = method }
        fun method(method: Method?) = apply { this.method = method?.name }
        fun title(title: String?) = apply { this.title = title }
        fun type(type: String?) = apply { this.type = type }
        fun fields(fields: List<Field>?) = apply { this.fields = fields }
        fun fields(vararg fields: Field) = fields(listOf(*fields))

        // TODO: Ensure immutability
        fun build() = Action(name, clazz, method, href, title, type, fields)
    }

    enum class Method {
        HEAD,
        GET,
        PUT,
        POST,
        OPTIONS,
        DELETE
    }

    companion object {
        private const val serialVersionUID = -8092791402843123679L

        internal fun fromRaw(map: Any?): Action = fromRaw(map!!.asMap())

        private fun parseHref(value: String): URI =
            try {
                URI(value)
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(String.format("Invalid %s in Action", Siren.HREF), e)
            }

        private fun fromRaw(map: Map<String, Any?>): Action = Action
            .newBuilder(
                map[Siren.NAME] as String,
                parseHref(map[Siren.HREF].toString())
            )
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .method(map[Siren.METHOD] as String?)
            .title(map[Siren.TITLE] as String?)
            .type(map[Siren.TYPE] as String?)
            .fields(map[Siren.FIELDS]?.asList()?.map { Field.fromRaw(it) })
            .build()

        @JvmStatic
        fun newBuilder(name: String, href: URI): Builder = Builder(name, href)
    }
}
