package no.capraconsulting.siren;

import org.junit.Test;

import java.net.URI;

import static no.capraconsulting.siren.internal.TestUtil.entry;
import static no.capraconsulting.siren.internal.TestUtil.mapOf;
import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;

public class EmbeddedTest {

    @Test
    public void testEmbeddedRepresentation() {
        Root root = Root
            .newBuilder()
            .entities(
                EmbeddedRepresentation
                    .newBuilder("hasA")
                    .clazz("location")
                    .properties(mapOf(
                        entry("locationId", "location4"),
                        entry("geo", mapOf(
                            entry("latitude", 54.801913),
                            entry("longitude", 12.317822)
                        ))
                    ))
                    .links(
                        Link
                            .newBuilder(
                                "self",
                                URI.create("https://example.com/entity/entity1/location/location4")
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        verifyRoot("EmbeddedTest.EmbeddedRepresentation.siren.json", root);
    }

    @Test
    public void testEmbeddedLink() {
        Root root = Root
            .newBuilder()
            .entities(
                EmbeddedLink
                    .newBuilder(
                        "hasA",
                        URI.create("http://localhost:8080/1234")
                    )
                    .clazz("location")
                    .build()
            )
            .build();

        verifyRoot("EmbeddedTest.EmbeddedLink.siren.json", root);
    }
}
