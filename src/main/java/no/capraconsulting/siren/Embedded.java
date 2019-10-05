package no.capraconsulting.siren;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import no.capraconsulting.siren.internal.util.GenericsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static java.util.Collections.emptyList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsStringList;
import static no.capraconsulting.siren.internal.util.ListUtil.map;
import static no.capraconsulting.siren.internal.util.MapUtil.notNull;

/**
 * Represents a sub-entity in the Siren specification. Sub-entities can be expressed as either an
 * {@link EmbeddedLink embedded link} or an {@link EmbeddedRepresentation embedded representation}.
 * In JSON Siren, sub-entities are represented by an entities array, such as { "entities": [{ ... }] }.
 *
 * @see EmbeddedLink
 * @see EmbeddedRepresentation
 * @see <a href="https://github.com/kevinswiber/siren#sub-entities">Sub-entity specification</a>
 */
public abstract class Embedded implements Serializable {
    private static final long serialVersionUID = 8856776314875482332L;

    /**
     * class
     */
    @Nullable
    protected final List<String> clazz;

    @NotNull
    protected final List<String> rel;

    Embedded(@Nullable List<String> clazz, @NotNull List<String> rel) {
        this.clazz = clazz;
        this.rel = rel;
    }

    /**
     * The first rel of the entity.
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
     * The first class of the entity.
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
     * Defines the relationship of the sub-entity to its parent, per Web Linking (RFC5899).
     *
     * @return the value of rel attribute
     */
    @NotNull
    public List<String> getRel() {
        return rel;
    }

    /**
     * Describes the nature of an entity's content based on the current representation. Possible values
     * are implementation-dependent and should be documented.
     *
     * @return the value of class attribute or an empty list if it is missing
     */
    @NotNull
    public List<String> getClazz() {
        return clazz == null ? emptyList() : clazz;
    }

    abstract Map<String, Object> toRaw();

    @NotNull
    static Embedded fromRaw(@NotNull final Object map) {
        return fromRaw(objectAsMap(map));
    }

    @NotNull
    private static URI parseHref(@NotNull String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Invalid %s in Embedded", Siren.HREF), e);
        }
    }

    @NotNull
    static Embedded fromRaw(@NotNull final Map<String, Object> map) {
        List<String> clazz = notNull(map, Siren.CLASS) ? objectAsStringList(map.get(Siren.CLASS)) : null;
        List<String> rel = objectAsStringList(map.get(Siren.REL));

        if (notNull(map, Siren.HREF)) {
            URI href = parseHref(map.get(Siren.HREF).toString());
            return EmbeddedLink
                .newBuilder(rel, href)
                .clazz(clazz)
                .type((String) map.get(Siren.TYPE))
                .title((String) map.get(Siren.TITLE))
                .build();
        }

        return EmbeddedRepresentation
            .newBuilder(rel)
            .clazz(clazz)
            .title((String) map.get(Siren.TITLE))
            .properties(notNull(map, Siren.PROPERTIES) ? objectAsMap(map.get(Siren.PROPERTIES)) : null)
            .links(notNull(map, Siren.LINKS) ? map(objectAsList(map.get(Siren.LINKS)), Link::fromRaw) : null)
            .entities(notNull(map, Siren.ENTITIES) ? map(objectAsList(map.get(Siren.ENTITIES)), Embedded::fromRaw) : null)
            .actions(notNull(map, Siren.ACTIONS) ? map(objectAsList(map.get(Siren.ACTIONS)), Action::fromRaw) : null)
            .build();
    }
}
