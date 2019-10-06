package no.capraconsulting.siren

import java.io.Serializable
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.skipNulls

class EmbeddedRepresentation private constructor(
    clazz: List<String>?,
    val title: String?,
    rel: List<String>,
    private val _properties: Map<String, Any?>?,
    private val _links: List<Link>?,
    private val _entities: List<Embedded>?,
    private val _actions: List<Action>?
) : Embedded(clazz, rel), Serializable {

    val embeddedLinks: List<EmbeddedLink>
        get() = _entities?.filterIsInstance<EmbeddedLink>() ?: emptyList()

    val embeddedRepresentations: List<EmbeddedRepresentation>
        get() = _entities?.filterIsInstance<EmbeddedRepresentation>() ?: emptyList()

    val entities: List<Embedded> get() = _entities ?: emptyList()
    val links: List<Link> get() = _links ?: emptyList()
    val properties: Map<String, Any?> get() = _properties ?: emptyMap()
    val actions: List<Action>? get() = _actions ?: emptyList()

    override fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.REL] = rel
            this[Siren.PROPERTIES] = _properties
            this[Siren.LINKS] = _links?.map(Link::toRaw)
            this[Siren.ENTITIES] = _entities?.map(Embedded::toRaw)
            this[Siren.ACTIONS] = _actions?.map(Action::toRaw)
        }.skipNulls()

    class Builder internal constructor(private val rel: List<String>) {
        private var clazz: List<String>? = null
        private var title: String? = null
        private var properties: Map<String, Any?>? = null
        private var links: List<Link>? = null
        private var entities: List<Embedded>? = null
        private var actions: List<Action>? = null
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))
        fun title(title: String?) = apply { this.title = title }
        fun properties(properties: Map<String, Any?>?) = apply { this.properties = properties }
        fun links(links: List<Link>?) = apply { this.links = links }
        fun links(vararg links: Link) = links(listOf(*links))
        fun entities(entities: List<Embedded>?) = apply { this.entities = entities }
        fun entities(vararg entities: Embedded) = entities(listOf(*entities))
        fun actions(actions: List<Action>?) = apply { this.actions = actions }
        fun actions(vararg actions: Action) = actions(listOf(*actions))

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

    companion object {
        private const val serialVersionUID = 82962202068591847L

        @JvmStatic
        fun newBuilder(rel: List<String>): Builder = Builder(rel)

        @JvmStatic
        fun newBuilder(rel: String): Builder = Builder(listOf(rel))
    }
}
