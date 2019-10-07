package no.capraconsulting.siren;

import java.net.URI;
import org.junit.Test;

import static java.util.Collections.singletonMap;
import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;

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
}
