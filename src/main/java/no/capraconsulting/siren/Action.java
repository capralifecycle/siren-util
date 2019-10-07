package no.capraconsulting.siren;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsStringList;
import static no.capraconsulting.siren.internal.util.ListUtil.map;
import static no.capraconsulting.siren.internal.util.MapUtil.notNull;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * Actions show available behaviors an entity exposes and are used for executing state transitions.
 * Represented in JSON Siren as an array such as { "actions": [{ ... }] }
 *
 * @see <a href="https://github.com/kevinswiber/siren#actions-1">Action specification</a>
 */
public final class Action implements Serializable {
    private static final long serialVersionUID = -8092791402843123679L;

    @NotNull
    private final String name;

    /**
     * class
     */
    @NotNull
    private final List<String> clazz;

    /**
     * As per specification this list can be extended.
     */
    @Nullable
    private final String method;
    @NotNull
    private final URI href;
    @Nullable
    private final String title;
    @Nullable
    private final String type;
    @NotNull
    private final List<Field> fields;

    private Action(
        @NotNull final String name,
        @NotNull final List<String> clazz,
        @Nullable final String method,
        @NotNull final URI href,
        @Nullable final String title,
        @Nullable final String type,
        @NotNull final List<Field> fields
    ) {
        this.name = name;
        this.clazz = clazz;
        this.method = method;
        this.href = href;
        this.title = title;
        this.type = type;
        this.fields = fields;
    }

    /**
     * A string that identifies the action to be performed. Action names MUST be unique within the set of
     * actions for an entity. The behaviour of clients when parsing a Siren document that violates this
     * constraint is undefined. Required.
     *
     * @return the value of name attribute
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Describes the nature of an action based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    @NotNull
    public List<String> getClazz() {
        return clazz;
    }

    /**
     * An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET, PUT, POST,
     * DELETE, or PATCH. As new methods are introduced, this list can be extended. If this attribute is
     * omitted, GET should be assumed.
     *
     * @return the value of method attribute
     */
    @Nullable
    public String getMethod() {
        return method;
    }

    /**
     * The URI of the action. Required.
     *
     * @return the value of href attribute
     */
    @NotNull
    public URI getHref() {
        return href;
    }

    /**
     * Descriptive text about the action.
     *
     * @return the value of title attribute
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * The encoding type for the request. When omitted and the fields attribute exists, the default value
     * is `application/x-www-form-urlencoded`.
     *
     * @return the value of type attribute
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * A collection of fields.
     *
     * @return the value of fields attribute or an empty list if it is missing
     */
    @NotNull
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Create a new builder using the current data.
     */
    public Builder toBuilder() {
        return Action
            .newBuilder(name, href)
            .clazz(clazz)
            .method(method)
            .title(title)
            .type(type)
            .fields(fields);
    }

    @NotNull
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.NAME, name);
        result.put(Siren.TITLE, title);
        result.put(Siren.CLASS, clazz.isEmpty() ? null : clazz);
        result.put(Siren.METHOD, method);
        result.put(Siren.HREF, href);
        result.put(Siren.TYPE, type);
        result.put(Siren.FIELDS, fields.isEmpty() ? null : map(fields, Field::toRaw));
        return skipNulls(result);
    }

    @NotNull
    static Action fromRaw(@NotNull final Object map) {
        return fromRaw(objectAsMap(map));
    }

    @NotNull
    private static Action fromRaw(@NotNull final Map<String, Object> map) {
        return Action
            .newBuilder(
                (String) map.get(Siren.NAME),
                URI.create(map.get(Siren.HREF).toString())
            )
            .clazz(notNull(map, Siren.CLASS) ? objectAsStringList(map.get(Siren.CLASS)) : null)
            .method((String) map.get(Siren.METHOD))
            .title((String) map.get(Siren.TITLE))
            .type((String) map.get(Siren.TYPE))
            .fields(
                notNull(map, Siren.FIELDS)
                    ? map(objectAsList(map.get(Siren.FIELDS)), Field::fromRaw)
                    : null
            )
            .build();
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param name A string that identifies the action to be performed. Action names MUST be unique
     *             within the set of actions for an entity. The behaviour of clients when parsing a
     *             Siren document that violates this constraint is undefined.
     * @param href The URI of the action.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull final String name, @NotNull final URI href) {
        return new Builder(name, href);
    }

    /**
     * Builder for Action.
     *
     * @see Action
     */
    public static class Builder {
        @NotNull
        private String name;

        /**
         * class
         */
        @NotNull
        private List<String> clazz = emptyList();

        @Nullable
        private String method;
        @NotNull
        private URI href;
        @Nullable
        private String title;
        @Nullable
        private String type;
        @NotNull
        private List<Field> fields = emptyList();

        private Builder(@NotNull final String name, @NotNull final URI href) {
            this.name = name;
            this.href = href;
        }

        /**
         * Set value for name.
         *
         * @param name A string that identifies the action to be performed. Action names MUST be unique
         *             within the set of actions for an entity. The behaviour of clients when parsing a
         *             Siren document that violates this constraint is undefined. Required.
         * @return builder
         */
        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        /**
         * Set value for href.
         *
         * @param href The URI of the action. Required.
         */
        @NotNull
        public Builder href(@NotNull URI href) {
            this.href = href;
            return this;
        }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an action based on the current representation.
         *              Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@Nullable List<String> clazz) {
            this.clazz = clazz == null ? emptyList() : clazz;
            return this;
        }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an action based on the current representation.
         *              Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@NotNull String... clazz) {
            return clazz(asList(clazz));
        }

        /**
         * Set value for method.
         *
         * @param method An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET,
         *               PUT, POST, DELETE, or PATCH. As new methods are introduced, this list can be extended.
         *               If this attribute is omitted, GET should be assumed.
         * @return builder
         */
        @NotNull
        public Builder method(@Nullable String method) {
            this.method = method;
            return this;
        }

        /**
         * Set value for method.
         *
         * @param method An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET,
         *               PUT, POST, DELETE, or PATCH. As new methods are introduced, this list can be extended.
         *               If this attribute is omitted, GET should be assumed.
         * @return builder
         */
        @NotNull
        public Builder method(@Nullable Method method) {
            this.method = method == null ? null : method.name();
            return this;
        }

        /**
         * Set value for title.
         *
         * @param title Descriptive text about the action.
         * @return builder
         */
        @NotNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /**
         * Set value for type.
         *
         * @param type The encoding type for the request. When omitted and the fields attribute exists, the
         *             default value is `application/x-www-form-urlencoded`.
         * @return builder
         */
        @NotNull
        public Builder type(@Nullable String type) {
            this.type = type;
            return this;
        }

        /**
         * Set value for fields.
         *
         * @param fields A collection of fields.
         * @return builder
         */
        @NotNull
        public Builder fields(@Nullable List<Field> fields) {
            this.fields = fields == null ? emptyList() : fields;
            return this;
        }

        /**
         * Set value for fields.
         *
         * @param fields A collection of fields.
         * @return builder
         */
        @NotNull
        public Builder fields(@NotNull Field... fields) {
            return fields(asList(fields));
        }

        /**
         * Build.
         *
         * @return new Action
         */
        @NotNull
        public Action build() {
            // TODO: Ensure immutability
            return new Action(name, clazz, method, href, title, type, fields);
        }
    }

    /**
     * An enumerated attribute mapping to a protocol method. For HTTP, these values may be GET, PUT, POST,
     * DELETE, or PATCH. As new methods are introduced, this list can be extended.
     */
    public enum Method {
        HEAD,
        GET,
        PUT,
        POST,
        OPTIONS,
        DELETE
    }
}
