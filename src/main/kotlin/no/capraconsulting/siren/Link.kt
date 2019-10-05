package no.capraconsulting.siren

import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls
import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Arrays.asList
import java.util.Collections.emptyList
import java.util.LinkedHashMap

/**
 * Links represent navigational transitions in the Siren specification.
 * In JSON Siren, links are represented as an array inside the entity,
 * such as { "links": [{ "rel": [ "self" ], "href": "http://api.x.io/orders/42"}] }
 *
 * @see [Link specification](https://github.com/kevinswiber/siren.links-1)
 */
class Link private constructor(
    private val _clazz: List<String>?,
    /**
     * Text describing the nature of a link.
     *
     * @return the value of title attribute
     */
    val title: String?,
    /**
     * Defines the relationship of the link to its entity, per Web Linking (RFC5988).
     *
     * @return the value of rel attribute
     */
    val rel: List<String>,
    /**
     * The URI of the linked resource.
     *
     * @return the value of href attribute
     */
    val href: URI,
    /**
     * Defines media type of the linked resource, per Web Linking (RFC5988). For the syntax, see
     * RFC2045 (section 5.1), RFC4288 (section 4.2), RFC6838 (section 4.2)
     *
     * @return the value of type attribute
     */
    val type: String?
) : Serializable {

    /**
     * The first rel of the link.
     *
     * Per specification there should always be at least one element in the rel attribute.
     *
     * Only use this method if you have full control over the Siren document as there is no guarantee
     * what will come first when having multiple rel values.
     *
     * @return string or null if missing
     * @see .getRel
     */
    val firstRel: String get() = rel[0]

    /**
     * The first class of the link.
     *
     * Only use this if you have full control over the Siren document as there is no guarantee
     * what will come first when having multiple class values.
     *
     * @return string or null if missing
     * @see .getClazz
     */
    val firstClass: String? get() = _clazz?.firstOrNull()

    /**
     * Describes aspects of the link based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    val clazz: List<String> get() = _clazz ?: emptyList()

    internal fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.TITLE] = title
            this[Siren.REL] = rel
            this[Siren.HREF] = href
            this[Siren.TYPE] = type
        }.skipNulls()

    /**
     * Builder for Link.
     *
     * @see Link
     */
    class Builder internal constructor(private val rel: List<String>, private val href: URI) {
        private var clazz: List<String>? = null
        private var title: String? = null
        private var type: String? = null

        /**
         * Add value for title.
         *
         * @param title Text describing the nature of a link.
         * @return builder
         */
        fun title(title: String?) = apply { this.title = title }

        /**
         * Add value for class.
         *
         * @param clazz Describes aspects of the link based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }

        /**
         * Add value for class.
         *
         * @param clazz Describes aspects of the link based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))

        /**
         * Add value for type.
         *
         * @param type Defines media type of the linked resource, per Web Linking (RFC5988). For the syntax,
         * see RFC2045 (section 5.1), RFC4288 (section 4.2), RFC6838 (section 4.2)
         * @return builder
         */
        fun type(type: String?) = apply { this.type = type }

        /**
         * Build.
         *
         * @return new Link
         */
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

        /**
         * Create a new builder using the required attributes.
         *
         * @param rel Defines the relationship of the link to its entity, per Web Linking (RFC5988).
         * @param href The URI of the linked resource.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(rel: List<String>, href: URI): Builder = Builder(rel, href)

        /**
         * Create a new builder using the required attributes.
         *
         * @param rel Defines the relationship of the link to its entity, per Web Linking (RFC5988).
         * @param href The URI of the linked resource.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(rel: String, href: URI): Builder = Builder(listOf(rel), href)
    }
}
