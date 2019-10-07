package no.capraconsulting.siren;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;
import static org.junit.Assert.assertEquals;

public class EmbeddedRepresentationTest {

    @Test
    public void testToBuilder() {
        EmbeddedRepresentation repr = EmbeddedRepresentation
            .newBuilder("rel")
            .clazz("class")
            .properties(singletonMap("key", "value"))
            .links(Link.newBuilder("rel", URI.create("uri")).build())
            .entities(EmbeddedLink.newBuilder("rel", URI.create("uri")).build())
            .actions(Action.newBuilder("name", URI.create("uri")).build())
            .title("title")
            .build()
            .toBuilder()
            .rel("other")
            .build();

        verifyRoot(
            "EmbeddedRepresentationTest.ToBuilder.siren.json",
            Root.newBuilder().entities(repr).build()
        );
    }

    @Test
    public void testNullArgToBuilder() {
        EmbeddedRepresentation repr = EmbeddedRepresentation
            .newBuilder("rel")
            .clazz("class")
            .properties(singletonMap("key", "value"))
            .build()
            .toBuilder()
            .clazz((List<String>) null)
            .properties((Map<String, Object>) null)
            .build();

        assertEquals(0, repr.getClazz().size());
        assertEquals(0, repr.getProperties().size());
    }

    @Test
    public void testGetEntities() {
        EmbeddedRepresentation subembr = EmbeddedRepresentation.newBuilder("rel").build();
        EmbeddedLink sublink = EmbeddedLink.newBuilder("rel", URI.create("uri")).build();

        EmbeddedRepresentation repr = EmbeddedRepresentation
            .newBuilder("rel")
            .entities(subembr, sublink)
            .build();

        assertEquals(singletonList(sublink), repr.getEmbeddedLinks());
        assertEquals(singletonList(subembr), repr.getEmbeddedRepresentations());
    }
}
