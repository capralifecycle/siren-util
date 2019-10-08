package no.capraconsulting.siren

import java.io.Serializable
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.json.parseJsonToMap
import no.capraconsulting.siren.internal.json.toJson
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls

/**
 * An Entity is a URI-addressable resource that has properties and actions
 * associated with it. It may contain sub-entities and navigational links.
 *
 * **See also:** [Entity specification](https://github.com/kevinswiber/siren.entities)
 *
 * @see Link
 * @see Field
 * @see Action
 * @see Embedded
 *
 * Prefer using [newBuilder] to create a new instance instead of using the
 * constructor, as the constructor has some relaxed checking which should
 * only be used when deserializing an existing representation.
 */
data class Root(
    /**
     * Describes the nature of an entity's content based on the current
     * representation. Possible values are implementation-dependent and should
     * be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    val clazz: List<String> = emptyList(),
    /**
     * Descriptive text about the entity.
     *
     * @return the value of title attribute
     */
    val title: String? = null,
    /**
     * A set of key-value pairs that describe the state of an entity.
     *
     * @return the value of properties attribute or an empty map if it is
     * missing
     */
    val properties: Map<String, Any?> = emptyMap(),
    /**
     * A collection of items that describe navigational links, distinct from
     * entity relationships. Link items should contain a `rel` attribute to
     * describe the relationship and an `href` attribute to point to the target
     * URI. Entities should include a link `rel` to `self`.
     *
     * @return the value of links attribute or an empty list if it is missing
     */
    val links: List<Link> = emptyList(),
    /**
     * A collection of related sub-entities.
     *
     * @return the value of entities attribute or an empty list if it is
     * missing
     */
    val entities: List<Embedded> = emptyList(),
    /**
     * A collection of actions; actions show available behaviors an entity
     * exposes.
     *
     * @return the value of actions attribute or an empty list if it is missing
     */
    val actions: List<Action> = emptyList()
) : Serializable {

    /**
     * The first class of the entity.
     *
     * Only use this if you have full control over the Siren document as there
     * is no guarantee what will come first when having multiple class values.
     */
    val firstClass: String? get() = clazz.firstOrNull()

    /**
     * Entities which are embedded links.
     */
    val embeddedLinks: List<EmbeddedLink>
        get() = entities.filterIsInstance<EmbeddedLink>()

    /**
     * Entities which are embedded representations.
     */
    val embeddedRepresentations: List<EmbeddedRepresentation>
        get() = entities.filterIsInstance<EmbeddedRepresentation>()

    /**
     * Create a new builder using the current data.
     */
    fun toBuilder() = Root
        .newBuilder()
        .clazz(clazz)
        .title(title)
        .properties(properties)
        .links(links)
        .entities(entities)
        .actions(actions)

    /**
     * Generate a JSON string representation of this entity.
     *
     * The value will be contained in a single line.
     *
     * The representation will be idempotent unless some special user data
     * is used as properties that does not guarantee ordering.
     *
     * @return json string
     */
    fun toJson(): String = toRaw().toJson()

    /**
     * Generate a representation of this entity by using generic java objects
     * such as Map and List.
     *
     * Attributes in the Siren specific structure that are null is not included
     * as it covers optional data. Attributes that is an empty list of empty
     * map is treated as null and not included.
     *
     * @return object
     */
    fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = if (clazz.isEmpty()) null else clazz
            this[Siren.TITLE] = title
            this[Siren.PROPERTIES] = if (properties.isEmpty()) null else properties
            this[Siren.ENTITIES] = if (entities.isEmpty()) null else entities.map(Embedded::toRaw)
            this[Siren.ACTIONS] = if (actions.isEmpty()) null else actions.map(Action::toRaw)
            this[Siren.LINKS] = if (links.isEmpty()) null else links.map(Link::toRaw)
        }.skipNulls()

    /**
     * Builder for [Root].
     */
    class Builder internal constructor() {
        private var clazz: List<String> = emptyList()
        private var title: String? = null
        private var properties: Map<String, Any?> = emptyMap()
        private var links: List<Link> = emptyList()
        private var entities: List<Embedded> = emptyList()
        private var actions: List<Action> = emptyList()

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an entity's content based on
         * the current representation. Possible values are
         * implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz ?: emptyList() }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an entity's content based on
         * the current representation. Possible values are
         * implementation-dependent and should be documented.
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
         * @param properties A set of key-value pairs that describe the state
         * of an entity.
         * @return builder
         */
        fun properties(properties: Map<String, Any?>?) =
            apply { this.properties = properties ?: emptyMap() }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational
         * links, distinct from entity relationships. Entities should include
         * a link `rel` to `self`.
         * @return builder
         */
        fun links(links: List<Link>?) = apply { this.links = links ?: emptyList() }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational
         * links, distinct from entity relationships. Entities should include
         * a link `rel` to `self`.
         * @return builder
         */
        fun links(vararg links: Link) = links(listOf(*links))

        /**
         * Set value for entities.
         *
         * @param entities A collection of related sub-entities.
         * @return builder
         */
        fun entities(entities: List<Embedded>?) =
            apply { this.entities = entities ?: emptyList() }

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
         * @param actions A collection of actions; actions show available
         * behaviors an entity exposes.
         * @return builder
         */
        fun actions(actions: List<Action>?) =
            apply { this.actions = actions ?: emptyList() }

        /**
         * Set value for actions.
         *
         * @param actions A collection of actions; actions show available
         * behaviors an entity exposes.
         * @return builder
         */
        fun actions(vararg actions: Action) = actions(listOf(*actions))

        /**
         * Build the [Root].
         */
        // TODO: Ensure immutability
        fun build() = Root(
            clazz = clazz,
            title = title,
            properties = properties,
            links = links,
            entities = entities,
            actions = actions
        )
    }

    /** @suppress */
    companion object {
        private const val serialVersionUID = -6380321936545122329L

        /**
         * Create a Root by generic java objects such as Map and List.
         *
         * This is effectively the inverse of [Root.toRaw].
         *
         * If extra attributes not specified in the Siren specification is
         * included they will be discarded.
         *
         * Prefer using the builder instead of this to produce more readable
         * code and avoid data loss.
         *
         * @param map object
         * @return a new Root
         */
        @JvmStatic
        fun fromRaw(map: Map<String, Any?>): Root = Root(
            clazz = map[Siren.CLASS]?.asNonNullStringList() ?: emptyList(),
            title = map[Siren.TITLE] as String?,
            properties = map[Siren.PROPERTIES]?.asMap() ?: emptyMap(),
            links = map[Siren.LINKS]?.asList()?.map { Link.fromRaw(it) } ?: emptyList(),
            entities = map[Siren.ENTITIES]?.asList()?.map { Embedded.fromRaw(it) } ?: emptyList(),
            actions = map[Siren.ACTIONS]?.asList()?.map { Action.fromRaw(it) } ?: emptyList()
        )

        /**
         * Create a Root by parsing a JSON value that follows the Siren
         * specification.
         *
         * If extra attributes not specified in the Siren specification is
         * present in the JSON value it will be discarded.
         *
         * @param json valid Siren JSON value
         * @return a new Root
         */
        @JvmStatic
        fun fromJson(json: String): Root = fromRaw(json.parseJsonToMap().asMap())

        /**
         * Create a new builder. No attributes are required.
         *
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(): Builder = Builder()
    }
}
