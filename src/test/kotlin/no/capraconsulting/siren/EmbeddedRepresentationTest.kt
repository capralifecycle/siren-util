package no.capraconsulting.siren

import java.net.URI
import java.util.Collections.singletonMap
import no.capraconsulting.siren.internal.verifyRoot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmbeddedRepresentationTest {

  @Test
  fun testToBuilder() {
    val repr =
        EmbeddedRepresentation.newBuilder("rel")
            .clazz("class")
            .properties(singletonMap("key", "value"))
            .links(Link.newBuilder("rel", URI.create("uri")).build())
            .entities(EmbeddedLink.newBuilder("rel", URI.create("uri")).build())
            .actions(Action.newBuilder("name", URI.create("uri")).build())
            .title("title")
            .build()
            .toBuilder()
            .rel("other")
            .build()

    verifyRoot(
        "EmbeddedRepresentationTest.ToBuilder.siren.json",
        Root.newBuilder().entities(repr).build(),
    )
  }

  @Test
  fun testNullArgToBuilder() {
    val repr =
        EmbeddedRepresentation.newBuilder("rel")
            .clazz("class")
            .properties(mapOf("key" to "value"))
            .build()
            .toBuilder()
            .clazz(null)
            .properties(null)
            .build()

    assertThat(repr.clazz.size).isEqualTo(0)
    assertThat(repr.properties.size).isEqualTo(0)
  }

  @Test
  fun testGetEntities() {
    val subembr = EmbeddedRepresentation.newBuilder("rel").build()
    val sublink = EmbeddedLink.newBuilder("rel", URI.create("uri")).build()

    val repr = EmbeddedRepresentation.newBuilder("rel").entities(subembr, sublink).build()

    assertThat(repr.embeddedLinks).isEqualTo(listOf(sublink))
    assertThat(repr.embeddedRepresentations).isEqualTo(listOf(subembr))
  }
}
