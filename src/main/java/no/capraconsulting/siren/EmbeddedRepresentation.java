package no.capraconsulting.siren;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static no.capraconsulting.siren.internal.util.ListUtil.map;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * Embedded sub-entity representations retain all the characteristics of a {@link Root standard entity},
 * but MUST also contain a rel attribute describing the relationship of the sub-entity to its parent.
 *
 * @see Embedded
 * @see EmbeddedLink
 * @see <a href="https://github.com/kevinswiber/siren#embedded-representation">Embedded Representation specification</a>
 */
public final class EmbeddedRepresentation extends Embedded implements Serializable {
    private static final long serialVersionUID = 82962202068591847L;

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

    private EmbeddedRepresentation(
        @NotNull final List<String> clazz,
        @Nullable final String title,
        @NotNull final List<String> rel,
        @NotNull final Map<String, Object> properties,
        @NotNull final List<Link> links,
        @NotNull final List<Embedded> entities,
        @NotNull final List<Action> actions
    ) {
        super(clazz, rel);
        this.title = title;
        this.properties = properties;
        this.links = links;
        this.entities = entities;
        this.actions = actions;
    }

    /**
     * Entities which are embedded links.
     *
     * @return list of embedded links
     * @see #getEntities()
     */
    @NotNull
    public List<EmbeddedLink> getEmbeddedLinks() {
        return entities.stream()
            .filter(EmbeddedLink.class::isInstance)
            .map(item -> (EmbeddedLink) item)
            .collect(Collectors.toList());
    }

    /**
     * Entities which are embedded representations.
     *
     * @return list of embedded representations
     * @see #getEntities()
     */
    @NotNull
    public List<EmbeddedRepresentation> getEmbeddedRepresentations() {
        return entities.stream()
            .filter(EmbeddedRepresentation.class::isInstance)
            .map(item -> (EmbeddedRepresentation) item)
            .collect(Collectors.toList());
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
        return EmbeddedRepresentation
            .newBuilder(rel)
            .clazz(clazz)
            .title(title)
            .properties(properties)
            .links(links)
            .entities(entities)
            .actions(actions);
    }

    @Override
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.CLASS, clazz.isEmpty() ? null : clazz);
        result.put(Siren.REL, rel.isEmpty() ? null : rel);
        result.put(Siren.PROPERTIES, properties.isEmpty() ? null : properties);
        result.put(Siren.LINKS, links.isEmpty() ? null : map(links, Link::toRaw));
        result.put(Siren.ENTITIES, entities.isEmpty() ? null : map(entities, Embedded::toRaw));
        result.put(Siren.ACTIONS, actions.isEmpty() ? null : map(actions, Action::toRaw));
        result.put(Siren.TITLE, title);
        return skipNulls(result);
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull List<String> rel) {
        return new Builder(rel);
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull String rel) {
        return new Builder(singletonList(rel));
    }

    /**
     * Builder for EmbeddedRepresentation.
     *
     * @see EmbeddedRepresentation
     */
    public static class Builder {
        @NotNull
        private List<String> clazz = emptyList();
        @Nullable
        private String title;
        @NotNull
        private List<String> rel;
        @NotNull
        private Map<String, Object> properties = emptyMap();
        @NotNull
        private List<Link> links = emptyList();
        @NotNull
        private List<Embedded> entities = emptyList();
        @NotNull
        private List<Action> actions = emptyList();

        private Builder(@NotNull List<String> rel) {
            this.rel = rel;
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
         * Set value for rel.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899). Required.
         * @return builder
         */
        @NotNull
        public Builder rel(@NotNull List<String> rel) {
            this.rel = rel;
            return this;
        }

        /**
         * Set value for rel.
         *
         * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899). Required.
         * @return builder
         */
        @NotNull
        public Builder rel(@NotNull String rel) {
            return rel(singletonList(rel));
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
         * @return new EmbeddedRepresentation
         */
        @NotNull
        public EmbeddedRepresentation build() {
            // TODO: Ensure immutability
            return new EmbeddedRepresentation(clazz, title, rel, properties, links, entities, actions);
        }
    }
}
