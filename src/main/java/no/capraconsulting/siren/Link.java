package no.capraconsulting.siren;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsStringList;
import static no.capraconsulting.siren.internal.util.MapUtil.notNull;
import static no.capraconsulting.siren.internal.util.MapUtil.skipNulls;

/**
 * Links represent navigational transitions in the Siren specification.
 * In JSON Siren, links are represented as an array inside the entity,
 * such as { "links": [{ "rel": [ "self" ], "href": "http://api.x.io/orders/42"}] }
 *
 * @see <a href="https://github.com/kevinswiber/siren#links-1">Link specification</a>
 */
public final class Link implements Serializable {
    private static final long serialVersionUID = -5250035724727313356L;

    @Nullable
    private final List<String> clazz;
    @Nullable
    private final String title;
    @NotNull
    private final List<String> rel;
    @NotNull
    private final URI href;
    @Nullable
    private final String type;

    private Link(
        @Nullable final List<String> clazz,
        @Nullable final String title,
        @NotNull final List<String> rel,
        @NotNull final URI href,
        @Nullable final String type
    ) {
        this.clazz = clazz;
        this.title = title;
        this.rel = rel;
        this.href = href;
        this.type = type;
    }

    /**
     * The URI of the linked resource.
     *
     * @return the value of href attribute
     */
    @NotNull
    public URI getHref() {
        return href;
    }

    /**
     * Text describing the nature of a link.
     *
     * @return the value of title attribute
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * The first rel of the link.
     *
     * Per specification there should always be at least one element in the rel attribute.
     *
     * Only use this method if you have full control over the Siren document as there is no guarantee
     * what will come first when having multiple rel values.
     *
     * @return string or null if missing
     * @see #getRel()
     */
    @NotNull
    public String getFirstRel() {
        return rel.get(0);
    }

    /**
     * The first class of the link.
     *
     * Only use this if you have full control over the Siren document as there is no guarantee
     * what will come first when having multiple class values.
     *
     * @return string or null if missing
     * @see #getClazz()
     */
    @Nullable
    public String getFirstClass() {
        return clazz == null ? null : clazz.stream().findFirst().orElse(null);
    }

    /**
     * Defines the relationship of the link to its entity, per Web Linking (RFC5988).
     *
     * @return the value of rel attribute
     */
    @NotNull
    public List<String> getRel() {
        return rel;
    }

    /**
     * Describes aspects of the link based on the current representation. Possible values are
     * implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    @NotNull
    public List<String> getClazz() {
        return clazz == null ? emptyList() : clazz;
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

    @NotNull
    Map<String, Object> toRaw() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Siren.CLASS, clazz);
        result.put(Siren.TITLE, title);
        result.put(Siren.REL, rel);
        result.put(Siren.HREF, href);
        result.put(Siren.TYPE, type);
        return skipNulls(result);
    }

    @NotNull
    static Link fromRaw(@NotNull Object map) {
        return fromRaw(objectAsMap(map));
    }

    @NotNull
    private static URI parseHref(@NotNull String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Invalid %s in Link", Siren.HREF), e);
        }
    }

    @NotNull
    private static Link fromRaw(@NotNull final Map<String, Object> map) {
        return Link
            .newBuilder(
                objectAsStringList(map.get(Siren.REL)),
                parseHref(map.get(Siren.HREF).toString())
            )
            .title((String) map.get(Siren.TITLE))
            .clazz(notNull(map, Siren.CLASS) ? objectAsStringList(map.get(Siren.CLASS)) : null)
            .type((String) map.get(Siren.TYPE))
            .build();
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the link to its entity, per Web Linking (RFC5988).
     * @param href The URI of the linked resource.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull List<String> rel, @NotNull URI href) {
        return new Builder(rel, href);
    }

    /**
     * Create a new builder using the required attributes.
     *
     * @param rel Defines the relationship of the link to its entity, per Web Linking (RFC5988).
     * @param href The URI of the linked resource.
     * @return a new builder
     */
    @NotNull
    public static Builder newBuilder(@NotNull String rel, @NotNull URI href) {
        return new Builder(singletonList(rel), href);
    }

    /**
     * Builder for Link.
     *
     * @see Link
     */
    public static class Builder {
        @Nullable
        private List<String> clazz;
        @Nullable
        private String title;
        @NotNull
        private final List<String> rel;
        @NotNull
        private final URI href;
        @Nullable
        private String type;

        private Builder(@NotNull List<String> rel, @NotNull URI href) {
            this.rel = rel;
            this.href = href;
        }

        /**
         * Add value for title.
         *
         * @param title Text describing the nature of a link.
         * @return builder
         */
        @NotNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /**
         * Add value for class.
         *
         * @param clazz Describes aspects of the link based on the current representation.
         *              Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@Nullable List<String> clazz) {
            this.clazz = clazz;
            return this;
        }

        /**
         * Add value for class.
         *
         * @param clazz Describes aspects of the link based on the current representation.
         *              Possible values are implementation-dependent and should be documented.
         * @return builder
         */
        @NotNull
        public Builder clazz(@NotNull String... clazz) {
            return clazz(asList(clazz));
        }

        /**
         * Add value for type.
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
         * Build.
         *
         * @return new Link
         */
        @NotNull
        public Link build() {
            // TODO: Ensure immutability
            return new Link(clazz, title, rel, href, type);
        }
    }
}
