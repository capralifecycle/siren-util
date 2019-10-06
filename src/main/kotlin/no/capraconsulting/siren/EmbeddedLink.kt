package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.skipNulls

/**
 * Represents an embedded sub-entity that contains a URI link.
 *
 * **See also:** [Embedded Link specification](https://github.com/kevinswiber/siren.embedded-link)
 *
 * @see Embedded
 * @see EmbeddedRepresentation
 */
class EmbeddedLink private constructor(
    clazz: List<String>?,
    rel: List<String>,
    /**
     * The URI of the linked sub-entity.
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
    val type: String?,
    /**
     * Descriptive text about the entity.
     *
     * @return the value of title attribute
     */
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

    /**
     * Builder for [EmbeddedLink].
     */
    class Builder internal constructor(private val rel: List<String>, private val href: URI) {
        private var clazz: List<String>? = null
        private var type: String? = null
        private var title: String? = null

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an entity's content based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an entity's content based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))

        /**
         * Set value for type.
         *
         * @param type Defines media type of the linked resource, per Web Linking (RFC5988). For the syntax,
         * see RFC2045 (section 5.1), RFC4288 (section 4.2), RFC6838 (section 4.2)
         * @return builder
         */
        fun type(type: String?) = apply { this.type = type }

        /**
         * Set value for title.
         *
         * @param title Descriptive text about the entity.
         * @return builder
         */
        fun title(title: String?) = apply { this.title = title }

        /**
         * Build the [EmbeddedLink].
         */
        // TODO: Ensure immutability
        fun build(): EmbeddedLink = EmbeddedLink(clazz, rel, href, type, title)
    }

    /** @suppress */
    companion object {
        private const val serialVersionUID = 7663303509287365613L

        /**
         * Create a new builder using the required attributes.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
         * @param href The URI of the linked sub-entity.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(rel: List<String>, href: URI): Builder = Builder(rel, href)

        /**
         * Create a new builder using the required attributes.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
         * @param href The URI of the linked sub-entity.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(rel: String, href: URI): Builder = Builder(listOf(rel), href)
    }
}
