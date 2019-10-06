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

class Root private constructor(
    private val _clazz: List<String>?,
    val title: String?,
    private val _properties: Map<String, Any?>?,
    private val _links: List<Link>?,
    private val _entities: List<Embedded>?,
    private val _actions: List<Action>?
) : Serializable {

    val firstClass: String? get() = _clazz?.firstOrNull()

    val embeddedLinks: List<EmbeddedLink>
        get() = _entities?.filterIsInstance<EmbeddedLink>() ?: emptyList()

    val embeddedRepresentations: List<EmbeddedRepresentation>
        get() = _entities?.filterIsInstance<EmbeddedRepresentation>() ?: emptyList()

    val entities: List<Embedded> get() = _entities ?: emptyList()
    val links: List<Link> get() = _links ?: emptyList()
    val properties: Map<String, Any?> get() = _properties ?: emptyMap()
    val clazz: List<String> get() = _clazz ?: emptyList()
    val actions: List<Action> get() = _actions ?: emptyList()

    fun toJson(): String = toRaw().toJson()

    fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.CLASS] = _clazz
            this[Siren.TITLE] = title
            this[Siren.PROPERTIES] = _properties
            this[Siren.ENTITIES] = _entities?.map(Embedded::toRaw)
            this[Siren.ACTIONS] = _actions?.map(Action::toRaw)
            this[Siren.LINKS] = _links?.map(Link::toRaw)
        }.skipNulls()

    class Builder internal constructor() {
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
        fun build() = Root(clazz, title, properties, links, entities, actions)
    }

    companion object {
        private const val serialVersionUID = -6380321936545122329L

        @JvmStatic
        fun fromRaw(map: Map<String, Any?>): Root = Root
            .newBuilder()
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .title(map[Siren.TITLE] as String?)
            .properties(map[Siren.PROPERTIES]?.asMap())
            .links(map[Siren.LINKS]?.asList()?.map { Link.fromRaw(it) })
            .entities(map[Siren.ENTITIES]?.asList()?.map { Embedded.fromRaw(it) })
            .actions(map[Siren.ACTIONS]?.asList()?.map { Action.fromRaw(it) })
            .build()

        @JvmStatic
        fun fromJson(json: String): Root = fromRaw(json.parseJsonToMap().asMap())

        @JvmStatic
        fun newBuilder(): Builder = Builder()
    }
}
