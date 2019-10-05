package no.capraconsulting.siren;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsStringList;
import static no.capraconsulting.siren.internal.util.MapUtil.notNull;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * Fields represent controls inside of {@link Action actions}.
 *
 * @see <a href="https://github.com/kevinswiber/siren#fields-1">Field specification</a>
 */
public final class Field implements Serializable {
    private static final long serialVersionUID = -4600180928453411445L;

    @NotNull
    private final String name;

    /**
     * class
     */
    @Nullable
    private final List<String> clazz;

    @Nullable
    private final String type;

    @Nullable
    private final String title;

    /**
     * See spec for special values.
     */
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    @Nullable
    private final Object value;

    private Field(
        @NotNull final String name,
        @Nullable final List<String> clazz,
        @Nullable final String type,
        @Nullable final String title,
        @Nullable final Object value
    ) {
        this.name = name;
        this.clazz = clazz;
        this.type = type;
        this.title = title;
        this.value = value;
    }

    /**
     * A name describing the control. Field names MUST be unique within the set of fields for an action.
     * The behaviour of clients when parsing a Siren document that violates this constraint is undefined.
     *
     * @return the value of name attribute
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Describes aspects of the field based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    @NotNull
    public List<String> getClazz() {
        return clazz == null ? emptyList() : clazz;
    }

    /**
     * The input type of the field. This is a subset of the input types specified by HTML5.
     *
     * @return the value of type attribute
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * Textual annotation of a field. Clients may use this as a label.
     *
     * @return the value of title attribute
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * A value assigned to the field. May be a scalar value or a list of value objects.
     *
     * @return the value of value attribute
     */
    @Nullable
    public Object getValue() {
        return value;
    }

    @NotNull
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.NAME, name);
        result.put(Siren.CLASS, clazz);
        result.put(Siren.TYPE, type);
        result.put(Siren.TITLE, title);
        result.put(Siren.VALUE, value);
        return skipNulls(result);
    }

    @NotNull
    static Field fromRaw(@NotNull final Object map) {
        return fromRaw(objectAsMap(map));
    }

    @NotNull
    private static Field fromRaw(@NotNull final Map<String, Object> map) {
        return Field
            .newBuilder((String) map.get(Siren.NAME))
            .clazz(notNull(map, Siren.CLASS) ? objectAsStringList(map.get(Siren.CLASS)) : null)
            .type((String) map.get(Siren.TYPE))
            .title((String) map.get(Siren.TITLE))
            .value(map.get(Siren.VALUE))
            .build();
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param name A name describing the control. Field names MUST be unique within the set of fields for an action.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull final String name) {
        return new Builder(name);
    }

    /**
     * Builder for Field.
     *
     * @see Field
     */
    public static class Builder {
        @NotNull
        private final String name;

        /**
         * class
         */
        @Nullable
        private List<String> clazz;

        @Nullable
        private String type;
        @Nullable
        private String title;
        @Nullable
        private Object value;

        private Builder(@NotNull final String name) {
            this.name = name;
        }

        /**
         * Set value for class.
         *
         * @param clazz Describes aspects of the field based on the current representation.
         *              Possible values areimplementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@Nullable List<String> clazz) {
            this.clazz = clazz;
            return this;
        }

        /**
         * Set value for class.
         *
         * @param clazz Describes aspects of the field based on the current representation.
         *              Possible values areimplementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@NotNull String... clazz) {
            return clazz(asList(clazz));
        }

        /**
         * Set value for type.
         *
         * @param type The input type of the field. This is a subset of the input types specified by HTML5.
         * @return builder
         */
        @NotNull
        public Builder type(@Nullable String type) {
            this.type = type;
            return this;
        }

        /**
         * Set value for type.
         *
         * @param type The input type of the field. This is a subset of the input types specified by HTML5.
         * @return builder
         */
        @NotNull
        public Builder type(@Nullable Type type) {
            this.type = type == null ? null : type.getValue();
            return this;
        }

        /**
         * Set value for title.
         *
         * @param title Textual annotation of a field. Clients may use this as a label.
         * @return builder
         */
        @NotNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /**
         * Set value for value.
         * @param value A value assigned to the field. May be a scalar value or a list of value objects.
         * @return builder
         */
        @NotNull
        public Builder value(@Nullable Object value) {
            this.value = value;
            return this;
        }

        /**
         * Build.
         *
         * @return new Field
         */
        @NotNull
        public Field build() {
            // TODO: Ensure immutability
            return new Field(name, clazz, type, title, value);
        }
    }

    public enum Type {
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
        FILE("file");

        /**
         * The textual value as defined in Siren specification.
         */
        @NotNull
        private final String value;

        // For some reason Kotlin crashes if having @NotNull on the parameter.
        @SuppressWarnings("NullableProblems")
        Type(final String className) {
            this.value = className;
        }

        @NotNull
        public String getValue() {
            return value;
        }
    }
}
