package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.emptyList
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList

abstract class Embedded protected constructor(
    protected val _clazz: List<String>?,
    val rel: List<String>
) : Serializable {

    val firstRel: String get() = rel[0]
    val firstClass: String? get() = _clazz?.firstOrNull()
    val clazz: List<String> get() = _clazz ?: emptyList()

    internal abstract fun toRaw(): Map<String, Any>

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
            val clazz = map[Siren.CLASS]?.asNonNullStringList()
            val rel = map.getValue(Siren.REL)!!.asNonNullStringList()

            if (map[Siren.HREF] != null) {
                val href = parseHref(map[Siren.HREF].toString())
                return EmbeddedLink
                    .newBuilder(rel, href)
                    .clazz(clazz)
                    .type(map[Siren.TYPE] as String?)
                    .title(map[Siren.TITLE] as String?)
                    .build()
            }

            return EmbeddedRepresentation
                .newBuilder(rel)
                .clazz(clazz)
                .title(map[Siren.TITLE] as String?)
                .properties(map[Siren.PROPERTIES]?.asMap())
                .links(map[Siren.LINKS]?.asList()?.map { Link.fromRaw(it) })
                .entities(map[Siren.ENTITIES]?.asList()?.map { fromRaw(it) })
                .actions(map[Siren.ACTIONS]?.asList()?.map { Action.fromRaw(it) })
                .build()
        }
    }
}
