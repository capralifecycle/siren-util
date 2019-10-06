package no.capraconsulting.siren

import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.emptyList
import java.util.LinkedHashMap
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls

/**
 * Actions show available behaviors an entity exposes and are used for executing state transitions.
 * Represented in JSON Siren as an array such as `{ "actions": [{ ... }] }`.
 *
 * **See also:** [Action specification](https://github.com/kevinswiber/siren.actions-1)
 */
class Action private constructor(
    /**
     * A string that identifies the action to be performed. Action names MUST be unique within the set of
     * actions for an entity. The behaviour of clients when parsing a Siren document that violates this
     * constraint is undefined.
     *
     * @return the value of name attribute
     */
    val name: String,
    /**
     * class
     */
    private val _clazz: List<String>?,
    /**
     * An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET, PUT, POST,
     * DELETE, or PATCH. As new methods are introduced, this list can be extended. If this attribute is
     * omitted, GET should be assumed.
     *
     * @return the value of method attribute
     */
    val method: String?,
    /**
     * The URI of the action.
     *
     * @return the value of href attribute
     */
    val href: URI,
    /**
     * Descriptive text about the action.
     *
     * @return the value of title attribute
     */
    val title: String?,
    /**
     * The encoding type for the request. When omitted and the fields attribute exists, the default value
     * is `application/x-www-form-urlencoded`.
     *
     * @return the value of type attribute
     */
    val type: String?,
    private val _fields: List<Field>?
) : Serializable {

    /**
     * Describes the nature of an action based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    val clazz: List<String> get() = _clazz ?: emptyList()

    /**
     * A collection of fields.
     *
     * @return the value of fields attribute or an empty list if it is missing
     */
    val fields: List<Field> get() = _fields ?: emptyList()

    internal fun toRaw(): Map<String, Any?> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.NAME] = name
            this[Siren.TITLE] = title
            this[Siren.CLASS] = _clazz
            this[Siren.METHOD] = method
            this[Siren.HREF] = href
            this[Siren.TYPE] = type
            this[Siren.FIELDS] = _fields?.map(Field::toRaw)
        }.skipNulls()

    /**
     * Builder for [Action].
     */
    class Builder internal constructor(private val name: String, private val href: URI) {

        /**
         * class
         */
        private var clazz: List<String>? = null

        private var method: String? = null
        private var title: String? = null
        private var type: String? = null
        private var fields: List<Field>? = null

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an action based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an action based on the current representation.
         * Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(vararg clazz: String) = clazz(listOf(*clazz))

        /**
         * Set value for method.
         *
         * @param method An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET,
         * PUT, POST, DELETE, or PATCH. As new methods are introduced, this list can be extended.
         * If this attribute is omitted, GET should be assumed.
         * @return builder
         */
        fun method(method: String?) = apply { this.method = method }

        /**
         * Set value for method.
         *
         * @param method An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET,
         * PUT, POST, DELETE, or PATCH. As new methods are introduced, this list can be extended.
         * If this attribute is omitted, GET should be assumed.
         * @return builder
         */
        fun method(method: Method?) = apply { this.method = method?.name }

        /**
         * Set value for title.
         *
         * @param title Descriptive text about the action.
         * @return builder
         */
        fun title(title: String?) = apply { this.title = title }

        /**
         * Set value for type.
         *
         * @param type The encoding type for the request. When omitted and the fields attribute exists, the
         * default value is `application/x-www-form-urlencoded`.
         * @return builder
         */
        fun type(type: String?) = apply { this.type = type }

        /**
         * Set value for fields.
         *
         * @param fields A collection of fields.
         * @return builder
         */
        fun fields(fields: List<Field>?) = apply { this.fields = fields }

        /**
         * Set value for fields.
         *
         * @param fields A collection of fields.
         * @return builder
         */
        fun fields(vararg fields: Field) = apply { fields(listOf(*fields)) }

        /**
         * Build the [Action].
         */
        // TODO: Ensure immutability
        fun build() = Action(name, clazz, method, href, title, type, fields)
    }

    /**
     * An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET, PUT, POST,
     * DELETE, or PATCH. As new methods are introduced, this list can be extended.
     */
    enum class Method {
        HEAD,
        GET,
        PUT,
        POST,
        OPTIONS,
        DELETE
    }

    /** @suppress */
    companion object {
        private const val serialVersionUID = -8092791402843123679L

        internal fun fromRaw(map: Any?): Action = fromRaw(map!!.asMap())

        private fun parseHref(value: String): URI =
            try {
                URI(value)
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(String.format("Invalid %s in Action", Siren.HREF), e)
            }

        private fun fromRaw(map: Map<String, Any?>): Action = Action
            .newBuilder(
                map[Siren.NAME] as String,
                parseHref(map[Siren.HREF].toString())
            )
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .method(map[Siren.METHOD] as String?)
            .title(map[Siren.TITLE] as String?)
            .type(map[Siren.TYPE] as String?)
            .fields(map[Siren.FIELDS]?.asList()?.map { Field.fromRaw(it) })
            .build()

        /**
         * Create a new builder using the required attributes.
         *
         * @param name A string that identifies the action to be performed. Action names MUST be unique
         * within the set of actions for an entity. The behaviour of clients when parsing a
         * Siren document that violates this constraint is undefined.
         * @param href The URI of the action.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(name: String, href: URI): Builder = Builder(name, href)
    }
}
