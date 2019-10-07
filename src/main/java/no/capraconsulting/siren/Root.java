package no.capraconsulting.siren;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.capraconsulting.siren.internal.json.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsStringList;
import static no.capraconsulting.siren.internal.util.ListUtil.map;
import static no.capraconsulting.siren.internal.util.MapUtil.notNull;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * An Entity is a URI-addressable resource that has properties and actions associated with it.
 * It may contain sub-entities and navigational links.
 *
 * @see Link
 * @see Field
 * @see Action
 * @see Embedded
 * @see <a href="https://github.com/kevinswiber/siren#entities">Entity specification</a>
 */
public final class Root implements Serializable {
    private static final long serialVersionUID = -6380321936545122329L;

    @NotNull
    private final List<String> clazz;
    @Nullable
    private final String title;
    @NotNull
    private final Map<String, Object> properties;
    @NotNull
    private final List<Link> links;
    @NotNull
    private final List<Embedded> entities;
    @NotNull
    private final List<Action> actions;

    private Root(
        @NotNull final List<String> clazz,
        @Nullable final String title,
        @NotNull final Map<String, Object> properties,
        @NotNull final List<Link> links,
        @NotNull final List<Embedded> entities,
        @NotNull final List<Action> actions
    ) {
        this.clazz = clazz;
        this.title = title;
        this.properties = properties;
        this.links = links;
        this.entities = entities;
        this.actions = actions;
    }

    /**
     * The first class of the entity.
     * <p>
     * Only use this if you have full control over the Siren document as there is no guarantee
     * what will come first when having multiple class values.
     *
     * @return string or null if missing
     * @see #getClazz()
     */
    @Nullable
    public String getFirstClass() {
        return clazz.stream().findFirst().orElse(null);
    }

    /**
     * Entities which are embedded links.
     *
     * @return list
     * @see #getEntities()
     */
    @NotNull
    public List<EmbeddedLink> getEmbeddedLinks() {
        return entities.stream()
            .filter(EmbeddedLink.class::isInstance)
            .map(entity -> (EmbeddedLink) entity)
            .collect(Collectors.toList());
    }

    /**
     * Entities which are embedded representations.
     *
     * @return list
     * @see #getEntities()
     */
    @NotNull
    public List<EmbeddedRepresentation> getEmbeddedRepresentations() {
        return entities.stream()
            .filter(EmbeddedRepresentation.class::isInstance)
            .map(entity -> (EmbeddedRepresentation) entity)
            .collect(Collectors.toList());
    }

    /**
     * A collection of related sub-entities.
     *
     * @return the value of entities attribute or an empty list if it is missing
     */
    @NotNull
    public List<Embedded> getEntities() {
        return entities;
    }

    /**
     * A collection of items that describe navigational links, distinct from entity relationships.
     * Link items should contain a `rel` attribute to describe the relationship and an `href` attribute
     * to point to the target URI. Entities should include a link `rel` to `self`.
     *
     * @return the value of links attribute or an empty list if it is missing
     */
    @NotNull
    public List<Link> getLinks() {
        return links;
    }

    /**
     * A set of key-value pairs that describe the state of an entity.
     *
     * @return the value of properties attribute or an empty map if it is missing
     */
    @NotNull
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Describes the nature of an entity's content based on the current representation.
     * Possible values are implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    @NotNull
    public List<String> getClazz() {
        return clazz;
    }

    /**
     * Descriptive text about the entity.
     *
     * @return the value of title attribute
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * A collection of actions; actions show available behaviors an entity exposes.
     *
     * @return the value of actions attribute or an empty list if it is missing
     */
    @NotNull
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Create a new builder using the current data.
     */
    public Builder toBuilder() {
        return Root
            .newBuilder()
            .clazz(clazz)
            .title(title)
            .properties(properties)
            .links(links)
            .entities(entities)
            .actions(actions);
    }

    /**
     * Generate a JSON string representation of this entity.
     * <p>
     * The value will be contained in a single line.
     * <p>
     * The representation will be idempotent unless some special user data
     * is used as properties that does not guarantee ordering.
     *
     * @return json string
     */
    @NotNull
    public String toJson() {
        return Json.toJson(toRaw());
    }

    /**
     * Generate a representation of this entity by using generic java objects such as Map and List.
     * <p>
     * Attributes in the Siren specific structure that are null is not included as it covers optional data.
     *
     * @return object
     */
    @NotNull
    public Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.CLASS, clazz.isEmpty() ? null : clazz);
        result.put(Siren.TITLE, title);
        result.put(Siren.PROPERTIES, properties.isEmpty() ? null : properties);
        result.put(Siren.ENTITIES, entities.isEmpty() ? null : map(entities, Embedded::toRaw));
        result.put(Siren.ACTIONS, actions.isEmpty() ? null : map(actions, Action::toRaw));
        result.put(Siren.LINKS, links.isEmpty() ? null : map(links, Link::toRaw));
        return skipNulls(result);
    }

    /**
     * Create a Root by generic java objects such as Map and List.
     * <p>
     * This is effectively the inverse of {{@link #toRaw()}}.
     * <p>
     * If extra attributes not specified in the Siren specification is included they will be discarded.
     * <p>
     * Prefer using the builder instead of this to produce more readable code and avoid data loss.
     *
     * @param map object
     * @return a new Root
     */
    @NotNull
    public static Root fromRaw(@NotNull final Map<String, Object> map) {
        return Root
            .newBuilder()
            .clazz(notNull(map, Siren.CLASS) ? objectAsStringList(map.get(Siren.CLASS)) : null)
            .title((String) map.get(Siren.TITLE))
            .properties(notNull(map, Siren.PROPERTIES) ? objectAsMap(map.get(Siren.PROPERTIES)) : null)
            .links(notNull(map, Siren.LINKS) ? map(objectAsList(map.get(Siren.LINKS)), Link::fromRaw) : null)
            .entities(notNull(map, Siren.ENTITIES) ? map(objectAsList(map.get(Siren.ENTITIES)), Embedded::fromRaw) : null)
            .actions(notNull(map, Siren.ACTIONS) ? map(objectAsList(map.get(Siren.ACTIONS)), Action::fromRaw) : null)
            .build();
    }

    /**
     * Create a Root by parsing a JSON value that follows the Siren specification.
     * <p>
     * If extra attributes not specified in the Siren specification is present in the JSON value it will be discarded.
     *
     * @param json valid Siren JSON value
     * @return a new Root
     */
    @NotNull
    public static Root fromJson(@NotNull final String json) {
        return fromRaw(objectAsMap(Json.fromJsonToMap(json)));
    }

    /**
     * Create a new builder. No attributes are required.
     *
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder for Root.
     *
     * @see Root
     */
    public static class Builder {
        @NotNull
        private List<String> clazz = emptyList();
        @Nullable
        private String title;
        @NotNull
        private Map<String, Object> properties = emptyMap();
        @NotNull
        private List<Link> links = emptyList();
        @NotNull
        private List<Embedded> entities = emptyList();
        @NotNull
        private List<Action> actions = emptyList();

        private Builder() {
        }

        /**
         * Set value for class.
         *
         * @param clazz Describes the nature of an entity's content based on the current representation.
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
         * @param clazz Describes the nature of an entity's content based on the current representation.
         *              Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@NotNull String... clazz) {
            return clazz(asList(clazz));
        }

        /**
         * Set value for title.
         *
         * @param title Descriptive text about the entity.
         * @return builder
         */
        @NotNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /**
         * Set value for properties.
         *
         * @param properties A set of key-value pairs that describe the state of an entity.
         * @return builder
         */
        @NotNull
        public Builder properties(@Nullable Map<String, Object> properties) {
            this.properties = properties == null ? emptyMap() : properties;
            return this;
        }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational links, distinct from entity relationships.
         *              Entities should include a link `rel` to `self`.
         * @return builder
         */
        @NotNull
        public Builder links(@Nullable List<Link> links) {
            this.links = links == null ? emptyList() : links;
            return this;
        }

        /**
         * Set value for links.
         *
         * @param links A collection of items that describe navigational links, distinct from entity relationships.
         *              Entities should include a link `rel` to `self`.
         * @return builder
         */
        @NotNull
        public Builder links(@NotNull Link... links) {
            return links(asList(links));
        }

        /**
         * Set value for entities.
         *
         * @param entities A collection of related sub-entities.
         * @return builder
         */
        @NotNull
        public Builder entities(@Nullable List<Embedded> entities) {
            this.entities = entities == null ? emptyList() : entities;
            return this;
        }

        /**
         * Set value for entities.
         *
         * @param entities A collection of related sub-entities.
         * @return builder
         */
        @NotNull
        public Builder entities(@NotNull Embedded... entities) {
            return entities(asList(entities));
        }

        /**
         * Set value for actions.
         *
         * @param actions A collection of actions; actions show available behaviors an entity exposes.
         * @return builder
         */
        @NotNull
        public Builder actions(@Nullable List<Action> actions) {
            this.actions = actions == null ? emptyList() : actions;
            return this;
        }

        /**
         * Set value for actions.
         *
         * @param actions A collection of actions; actions show available behaviors an entity exposes.
         * @return builder
         */
        @NotNull
        public Builder actions(@NotNull Action... actions) {
            return actions(asList(actions));
        }

        /**
         * Build.
         *
         * @return new Root
         */
        @NotNull
        public Root build() {
            // TODO: Ensure immutability
            return new Root(clazz, title, properties, links, entities, actions);
        }
    }
}
