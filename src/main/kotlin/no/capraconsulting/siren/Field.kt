package no.capraconsulting.siren

import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.util.asNonNullStringList
import no.capraconsulting.siren.internal.util.skipNulls
import java.io.Serializable
import java.util.Arrays.asList
import java.util.Collections.emptyList
import java.util.LinkedHashMap

/**
 * Fields represent controls inside of [actions][Action].
 *
 * @see [Field specification](https://github.com/kevinswiber/siren.fields-1)
 */
class Field private constructor(
    /**
     * A name describing the control. Field names MUST be unique within the set of fields for an action.
     * The behaviour of clients when parsing a Siren document that violates this constraint is undefined.
     *
     * @return the value of name attribute
     */
    val name: String,
    /**
     * class
     */
    private val _clazz: List<String>?,
    /**
     * The input type of the field. This is a subset of the input types specified by HTML5.
     *
     * @return the value of type attribute
     */
    val type: String?,
    /**
     * Textual annotation of a field. Clients may use this as a label.
     *
     * @return the value of title attribute
     */
    val title: String?,
    /**
     * See spec for special values.
     */
    /**
     * A value assigned to the field. May be a scalar value or a list of value objects.
     *
     * @return the value of value attribute
     */
    val value: Any?
) : Serializable {

    /**
     * Describes aspects of the field based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    val clazz: List<String> get() = _clazz ?: emptyList()

    internal fun toRaw(): Map<String, Any> =
        LinkedHashMap<String, Any?>().apply {
            this[Siren.NAME] = name
            this[Siren.CLASS] = _clazz
            this[Siren.TYPE] = type
            this[Siren.TITLE] = title
            this[Siren.VALUE] = value
        }.skipNulls()

    /**
     * Builder for Field.
     *
     * @see Field
     */
    class Builder internal constructor(private val name: String) {

        /**
         * class
         */
        private var clazz: List<String>? = null

        private var type: String? = null
        private var title: String? = null
        private var value: Any? = null

        /**
         * Set value for class.
         *
         * @param clazz Describes aspects of the field based on the current representation.
         * Possible values areimplementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(clazz: List<String>?) = apply { this.clazz = clazz }

        /**
         * Set value for class.
         *
         * @param clazz Describes aspects of the field based on the current representation.
         * Possible values areimplementation-dependent and should be documented.
         * @return builder
         */
        fun clazz(vararg clazz: String): Builder = clazz(listOf(*clazz))

        /**
         * Set value for type.
         *
         * @param type The input type of the field. This is a subset of the input types specified by HTML5.
         * @return builder
         */
        fun type(type: String?) = apply { this.type = type }

        /**
         * Set value for type.
         *
         * @param type The input type of the field. This is a subset of the input types specified by HTML5.
         * @return builder
         */
        fun type(type: Type?) = apply { this.type = type?.value }

        /**
         * Set value for title.
         *
         * @param title Textual annotation of a field. Clients may use this as a label.
         * @return builder
         */
        fun title(title: String?) = apply { this.title = title }

        /**
         * Set value for value.
         * @param value A value assigned to the field. May be a scalar value or a list of value objects.
         * @return builder
         */
        fun value(value: Any?) = apply { this.value = value }

        /**
         * Build.
         *
         * @return new Field
         */
        // TODO: Ensure immutability
        fun build(): Field = Field(name, clazz, type, title, value)
    }

    enum class Type  constructor(
        /**
         * The textual value as defined in Siren specification.
         */
        val value: String
    ) {
        HIDDEN("hidden"),
        TEXT("text"),
        SEARCH("search"),
        TEL("tel"),
        URL("url"),
        EMAIL("email"),
        PASSWORD("password"),
        DATETIME("datetime"),
        DATE("date"),
        MONTH("month"),
        WEEK("week"),
        TIME("time"),
        DATETIME_LOCAL("datetime-local"),
        NUMBER("number"),
        RANGE("range"),
        COLOR("color"),
        CHECKBOX("checkbox"),
        RADIO("radio"),
        FILE("file")
    }

    companion object {
        private const val serialVersionUID = -4600180928453411445L

        internal fun fromRaw(map: Any?): Field = fromRaw(map!!.asMap())

        private fun fromRaw(map: Map<String, Any?>): Field = Field
            .newBuilder(map[Siren.NAME] as String)
            .clazz(map[Siren.CLASS]?.asNonNullStringList())
            .type(map[Siren.TYPE] as String?)
            .title(map[Siren.TITLE] as String?)
            .value(map[Siren.VALUE])
            .build()

        /**
         * Create a new builder using the required attributes.
         *
         * @param name A name describing the control. Field names MUST be unique within the set of fields for an action.
         * @return a new builder
         */
        @JvmStatic
        fun newBuilder(name: String): Builder = Builder(name)
    }
}
