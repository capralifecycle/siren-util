package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.skipNulls

class EmbeddedLink private constructor(
    clazz: List<String>?,
    rel: List<String>,
    val href: URI,
    val type: String?,
    val title: String?
) : Embedded(clazz, rel), Serializable {

    override fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.REL] = rel
            this[Siren.HREF] = href
            this[Siren.TYPE] = type
            this[Siren.TITLE] = title
        }.skipNulls()

    class Builder internal constructor(private val rel: List<String>, private val href: URI) {
        private var clazz: List<String>? = null
        private var type: String? = null
        private var title: String? = null

        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))
        fun type(type: String?) = apply { this.type = type }
        fun title(title: String?) = apply { this.title = title }

        // TODO: Ensure immutability
        fun build(): EmbeddedLink = EmbeddedLink(clazz, rel, href, type, title)
    }

    companion object {
        private const val serialVersionUID = 7663303509287365613L

        @JvmStatic
        fun newBuilder(rel: List<String>, href: URI): Builder = Builder(rel, href)

        @JvmStatic
        fun newBuilder(rel: String, href: URI): Builder = Builder(listOf(rel), href)
    }
}
