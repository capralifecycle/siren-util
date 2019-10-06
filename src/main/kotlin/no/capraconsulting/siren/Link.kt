package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.emptyList
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls

class Link private constructor(
    private val _clazz: List<String>?,
    val title: String?,
    val rel: List<String>,
    val href: URI,
    val type: String?
) : Serializable {

    val firstRel: String get() = rel[0]
    val firstClass: String? get() = _clazz?.firstOrNull()
    val clazz: List<String> get() = _clazz ?: emptyList()

    internal fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.TITLE] = title
            this[Siren.REL] = rel
            this[Siren.HREF] = href
            this[Siren.TYPE] = type
        }.skipNulls()

    class Builder internal constructor(private val rel: List<String>, private val href: URI) {
        private var clazz: List<String>? = null
        private var title: String? = null
        private var type: String? = null

        fun title(title: String?) = apply { this.title = title }
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))
        fun type(type: String?) = apply { this.type = type }

        // TODO: Ensure immutability
        fun build() = Link(clazz, title, rel, href, type)
    }

    companion object {
        private const val serialVersionUID = -5250035724727313356L

        internal fun fromRaw(map: Any?): Link = fromRaw(map!!.asMap())

        private fun parseHref(value: String): URI =
            try {
                URI(value)
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(String.format("Invalid %s in Link", Siren.HREF), e)
            }

        private fun fromRaw(map: Map<String, Any?>): Link = Link
            .newBuilder(
                map.getValue(Siren.REL)!!.asNonNullStringList(),
                parseHref(map[Siren.HREF].toString())
            )
            .title(map[Siren.TITLE] as String?)
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .type(map[Siren.TYPE] as String?)
            .build()

        @JvmStatic
        fun newBuilder(rel: List<String>, href: URI): Builder = Builder(rel, href)

        @JvmStatic
        fun newBuilder(rel: String, href: URI): Builder = Builder(listOf(rel), href)
    }
}
