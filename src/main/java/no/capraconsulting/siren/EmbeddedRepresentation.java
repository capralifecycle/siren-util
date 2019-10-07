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
    @Nullable
    private final Map<String, Object> properties;
    @Nullable
    private final List<Link> links;
    @Nullable
    private final List<Embedded> entities;
    @Nullable
    private final List<Action> actions;

    private EmbeddedRepresentation(
        @Nullable final List<String> clazz,
        @Nullable final String title,
        @NotNull final List<String> rel,
        @Nullable final Map<String, Object> properties,
        @Nullable final List<Link> links,
        @Nullable final List<Embedded> entities,
        @Nullable final List<Action> actions
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
        return entities == null ? emptyList() : entities.stream()
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
        return entities == null ? emptyList() : entities.stream()
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
        return entities == null ? emptyList() : entities;
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
        return links == null ? emptyList() : links;
    }

    /**
     * A set of key-value pairs that describe the state of an entity.
     *
     * @return the value of properties attribute or an empty map if it is missing
     */
    @NotNull
    public Map<String, Object> getProperties() {
        return properties == null ? emptyMap() : properties;
    }

    /**
     * A collection of actions; actions show available behaviors an entity exposes.
     *
     * @return the value of actions attribute or an empty list if it is missing
     */
    @Nullable
    public List<Action> getActions() {
        return actions == null ? emptyList() : actions;
    }

    @Override
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.CLASS, clazz);
        result.put(Siren.REL, rel);
        result.put(Siren.PROPERTIES, properties);
        result.put(Siren.LINKS, links == null ? null : map(links, Link::toRaw));
        result.put(Siren.ENTITIES, entities == null ? null : map(entities, Embedded::toRaw));
        result.put(Siren.ACTIONS, actions == null ? null : map(actions, Action::toRaw));
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
        @Nullable
        private List<String> clazz;
        @Nullable
        private String title;
        @NotNull
        private final List<String> rel;
        @Nullable
        private Map<String, Object> properties;
        @Nullable
        private List<Link> links;
        @Nullable
        private List<Embedded> entities;
        @Nullable
        private List<Action> actions;

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
            this.clazz = clazz;
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
            this.properties = properties;
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
            this.links = links;
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
            this.entities = entities;
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
            this.actions = actions;
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
