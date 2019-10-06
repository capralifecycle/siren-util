package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.emptyList
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList

/**
 * Represents a sub-entity in the Siren specification. Sub-entities can be
 * expressed as either an [EmbeddedLink] or an [EmbeddedRepresentation].
 * In JSON Siren, sub-entities are represented by an entities array, such
 * as `{ "entities": [{ ... }] }`.
 *
 * **See also:** [Sub-entity specification](https://github.com/kevinswiber/siren.sub-entities)
 */
abstract class Embedded : Serializable {
    abstract val clazz: List<String>
    abstract val rel: List<String>

    /**
     * The first rel of the entity.
     *
     * Per specification there should always be at least one element in the
     * rel attribute.
     *
     * Only use this method if you have full control over the Siren document as
     * there is no guarantee what will come first when having multiple rel
     * values.
     */
    val firstRel: String get() = rel[0]

    /**
     * The first class of the entity.
     *
     * Only use this if you have full control over the Siren document as there
     * is no guarantee what will come first when having multiple class values.
     */
    val firstClass: String? get() = clazz.firstOrNull()

    internal abstract fun toRaw(): Map<String, Any>

    /** @suppress */
    companion object {
        private const val serialVersionUID = 8856776314875482332L

        internal fun fromRaw(map: Any?): Embedded = fromRaw(map!!.asMap())

        private fun parseHref(value: String): URI =
            try {
                URI(value)
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(
                    String.format("Invalid %s in Embedded", Siren.HREF),
                    e
                )
            }

        private fun fromRaw(map: Map<String, Any?>): Embedded {
            val clazz = map[Siren.CLASS]?.asNonNullStringList() ?: emptyList()
            val rel = map.getValue(Siren.REL)!!.asNonNullStringList()

            return if (map[Siren.HREF] != null) {
                EmbeddedLink(
                    clazz = clazz,
                    rel = rel,
                    href = parseHref(map[Siren.HREF].toString()),
                    type = map[Siren.TYPE] as String?,
                    title = map[Siren.TITLE] as String?
                )
            } else {
                EmbeddedRepresentation(
                    clazz = clazz,
                    title = map[Siren.TITLE] as String?,
                    rel = rel,
                    properties = map[Siren.PROPERTIES]?.asMap() ?: emptyMap(),
                    links = map[Siren.LINKS]?.asList()?.map { Link.fromRaw(it) } ?: emptyList(),
                    entities = map[Siren.ENTITIES]?.asList()?.map { fromRaw(it) } ?: emptyList(),
                    actions = map[Siren.ACTIONS]?.asList()?.map { Action.fromRaw(it) }
                        ?: emptyList()
                )
            }
        }
    }
}
