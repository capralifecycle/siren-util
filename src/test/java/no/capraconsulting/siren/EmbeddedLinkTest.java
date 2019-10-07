package no.capraconsulting.siren;

import java.net.URI;
import org.junit.Test;

import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;

public class EmbeddedLinkTest {

    @Test
    public void testToBuilder() {
        EmbeddedLink link = EmbeddedLink
            .newBuilder("rel", URI.create("uri"))
            .clazz("class")
            .type("type")
            .title("title")
            .build()
            .toBuilder()
            .rel("other")
            .build();

        verifyRoot(
            "EmbeddedLinkTest.ToBuilder.siren.json",
            Root.newBuilder().entities(link).build()
        );
    }
}
