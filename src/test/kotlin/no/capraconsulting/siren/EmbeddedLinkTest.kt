package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.Test

class EmbeddedLinkTest {

    @Test
    fun testToBuilder() {
        val link = EmbeddedLink
            .newBuilder("rel", URI.create("uri"))
            .clazz("class")
            .type("type")
            .title("title")
            .build()
            .toBuilder()
            .rel("other")
            .build()

        verifyRoot(
            "EmbeddedLinkTest.ToBuilder.siren.json",
            Root.newBuilder().entities(link).build()
        )
    }
}
