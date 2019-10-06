package no.capraconsulting.siren

import java.io.Serializable
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.skipNulls

/**
 * Embedded sub-entity representations retain all the characteristics of a [standard entity][Root],
 * but MUST also contain a rel attribute describing the relationship of the sub-entity to its parent.
 *
 * **See also:** [Embedded Representation specification](https://github.com/kevinswiber/siren.embedded-representation)
 *
 * @see Embedded
 * @see EmbeddedLink
 */
class EmbeddedRepresentation private constructor(
    clazz: List<String>?,
    /**
     * Descriptive text about the entity.
     *
     * @return the value of title attribute
     */
    val title: String?,
    rel: List<String>,
    private val _properties: Map<String, Any?>?,
    private val _links: List<Link>?,
    private val _entities: List<Embedded>?,
    private val _actions: List<Action>?
) : Embedded(clazz, rel), Serializable {

    /**
     * Entities which are of type [EmbeddedLink].
     */
    val embeddedLinks: List<EmbeddedLink>
        get() = _entities?.filterIsInstance<EmbeddedLink>() ?: emptyList()

    /**
     * Entities which are of type [EmbeddedRepresentation].
     */
    val embeddedRepresentations: List<EmbeddedRepresentation>
        get() = _entities?.filterIsInstance<EmbeddedRepresentation>() ?: emptyList()

    /**
     * A collection of related sub-entities.
     *
     * @return the value of entities attribute or an empty list if it is missing
     */
    val entities: List<Embedded> get() = _entities ?: emptyList()

    /**
     * A collection of items that describe navigational links, distinct from entity relationships.
     * Link items should contain a `rel` attribute to describe the relationship and an `href` attribute
     * to point to the target URI. Entities should include a link `rel` to `self`.
     *
     * @return the value of links attribute or an empty list if it is missing
     */
    val links: List<Link> get() = _links ?: emptyList()

    /**
     * A set of key-value pairs that describe the state of an entity.
     *
     * @return the value of properties attribute or an empty map if it is missing
     */
    val properties: Map<String, Any?> get() = _properties ?: emptyMap()

    /**
     * A collection of actions; actions show available behaviors an entity exposes.
     *
     * @return the value of actions attribute or an empty list if it is missing
     */
    val actions: List<Action>? get() = _actions ?: emptyList()

    override fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.REL] = rel
            this[Siren.PROPERTIES] = _properties
            this[Siren.LINKS] = _links?.map(Link::toRaw)
            this[Siren.ENTITIES] = _entities?.map(Embedded::toRaw)
            this[Siren.ACTIONS] = _actions?.map(Action::toRaw)
            this[Siren.TITLE] = title
        }.skipNulls()

    /**
     * Builder for [EmbeddedRepresentation].
     */
    class Builder internal constructor(private val rel: List<String>) {
        private var clazz: List<String>? = null
        private var title: String? = null
        private var properties: Map<String, Any?>? = null
        private var links: List<Link>? = null
        private var entities: List<Embedded>? = null
        private var actions: List<Action>? = null

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
         * Set value for title.
         *
         * @param title Descriptive text about the entity.
         * @return builder
         */
        fun title(title: String?) = apply { this.title = title }

        /**
         * Set value for properties.
         *
         * @param properties A set of key-value pairs that describe the state of an entity.
         * @return builder
         */
        fun properties(properties: Map<String, Any?>?) = apply { this.properties = properties }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational links, distinct from entity relationships.
         * Entities should include a link `rel` to `self`.
         * @return builder
         */
        fun links(links: List<Link>?) = apply { this.links = links }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational links, distinct from entity relationships.
         * Entities should include a link `rel` to `self`.
         * @return builder
         */
        fun links(vararg links: Link) = links(listOf(*links))

        /**
         * Set value for entities.
         *
         * @param entities A collection of related sub-entities.
         * @return builder
         */
        fun entities(entities: List<Embedded>?) = apply { this.entities = entities }

        /**
         * Set value for entities.
         *
         * @param entities A collection of related sub-entities.
         * @return builder
         */
        fun entities(vararg entities: Embedded) = entities(listOf(*entities))

        /**
         * Set value for actions.
         *
         * @param actions A collection of actions; actions show available behaviors an entity exposes.
         * @return builder
         */
        fun actions(actions: List<Action>?) = apply { this.actions = actions }

        /**
         * Set value for actions.
         *
         * @param actions A collection of actions; actions show available behaviors an entity exposes.
         * @return builder
         */
        fun actions(vararg actions: Action) = actions(listOf(*actions))

        /**
         * Build the [EmbeddedRepresentation].
         */
        // TODO: Ensure immutability
        fun build() = EmbeddedRepresentation(
            clazz,
            title,
            rel,
            properties,
            links,
            entities,
            actions
        )
    }

    /** @suppress */
    companion object {
        private const val serialVersionUID = 82962202068591847L

        /**
         * Create a new [Builder] using the required attributes.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
         */
        @JvmStatic
        fun newBuilder(rel: List<String>): Builder = Builder(rel)

        /**
         * Create a new [Builder] using the required attributes.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
         */
        @JvmStatic
        fun newBuilder(rel: String): Builder = Builder(listOf(rel))
    }
}
