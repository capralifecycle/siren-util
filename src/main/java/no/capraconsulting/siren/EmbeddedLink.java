package no.capraconsulting.siren;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * Represents an embedded sub-entity that contains a URI link.
 *
 * @see Embedded
 * @see EmbeddedRepresentation
 * @see <a href="https://github.com/kevinswiber/siren#embedded-link">Embedded Link specification</a>
 */
public final class EmbeddedLink extends Embedded implements Serializable {
    private static final long serialVersionUID = 7663303509287365613L;

    @NotNull
    private final URI href;
    @Nullable
    private final String type;
    @Nullable
    private final String title;

    private EmbeddedLink(
        @NotNull final List<String> clazz,
        @NotNull final List<String> rel,
        @NotNull final URI href,
        @Nullable final String type,
        @Nullable final String title
    ) {
        super(clazz, rel);
        this.href = href;
        this.type = type;
        this.title = title;
    }

    /**
     * The URI of the linked sub-entity. Required.
     *
     * @return the value of href attribute
     */
    @NotNull
    public URI getHref() {
        return href;
    }

    /**
     * Defines media type of the linked resource, per Web Linking (RFC5988). For the syntax, see
     * RFC2045 (section 5.1), RFC4288 (section 4.2), RFC6838 (section 4.2)
     *
     * @return the value of type attribute
     */
    @Nullable
    public String getType() {
        return type;
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

    @Override
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.CLASS, clazz.isEmpty() ? null : clazz);
        result.put(Siren.REL, rel.isEmpty() ? null : rel);
        result.put(Siren.HREF, href);
        result.put(Siren.TYPE, type);
        result.put(Siren.TITLE, title);
        return skipNulls(result);
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
     * @param href The URI of the linked sub-entity.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull List<String> rel, @NotNull URI href) {
        return new Builder(rel, href);
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
     * @param href The URI of the linked sub-entity.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull String rel, @NotNull URI href) {
        return new Builder(singletonList(rel), href);
    }

    /**
     * Builder for EmbeddedLink.
     *
     * @see EmbeddedLink
     */
    public static class Builder {
        @NotNull
        private List<String> clazz = emptyList();
        @NotNull
        private final List<String> rel;
        @NotNull
        private final URI href;
        @Nullable
        private String type;
        @Nullable
        private String title;

        private Builder(@NotNull List<String> rel, @NotNull URI href) {
            this.rel = rel;
            this.href = href;
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
         * Set value for type.
         *
         * @param type Defines media type of the linked resource, per Web Linking (RFC5988). For the syntax,
         *             see RFC2045 (section 5.1), RFC4288 (section 4.2), RFC6838 (section 4.2)
         * @return builder
         */
        @NotNull
        public Builder type(@Nullable String type) {
            this.type = type;
            return this;
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
         * Build.
         *
         * @return new EmbeddedLink
         */
        @NotNull
        public EmbeddedLink build() {
            // TODO: Ensure immutability
            return new EmbeddedLink(clazz, rel, href, type, title);
        }
    }
}
