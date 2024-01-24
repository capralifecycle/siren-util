package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.jupiter.api.Test

class EmbeddedTest {

  @Test
  fun testEmbeddedRepresentation() {
    val root =
        Root.newBuilder()
            .entities(
                EmbeddedRepresentation.newBuilder("hasA")
                    .clazz("location")
                    .properties(
                        mapOf(
                            "locationId" to "location4",
                            "geo" to mapOf("latitude" to 54.801913, "longitude" to 12.317822)))
                    .links(
                        Link.newBuilder(
                                "self",
                                URI.create("https://example.com/entity/entity1/location/location4"))
                            .build())
                    .build())
            .build()

    verifyRoot("EmbeddedTest.EmbeddedRepresentation.siren.json", root)
  }

  @Test
  fun testEmbeddedLink() {
    val root =
        Root.newBuilder()
            .entities(
                EmbeddedLink.newBuilder("hasA", URI.create("http://localhost:8080/1234"))
                    .clazz("location")
                    .build())
            .build()

    verifyRoot("EmbeddedTest.EmbeddedLink.siren.json", root)
  }
}
