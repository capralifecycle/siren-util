package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.getResource
import no.capraconsulting.siren.internal.parseAndVerifyRootRelaxed
import no.capraconsulting.siren.internal.verifyRoot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LinkTest {

  @Test
  fun testParse() {
    val json = getResource("LinkTest.siren.json")
    val root = parseAndVerifyRootRelaxed(json)

    assertThat(root.links.size).isEqualTo(1)
    assertThat(root.links[0].firstClass).isEqualTo("city")
  }

  @Test
  fun testLinkWithoutType() {
    val link = Link.newBuilder("containsChild", URI.create("http://localhost:8080")).build()
    assertThat(link.clazz).isEmpty()
    assertThat(link.firstClass).isNull()
    assertThat(link.toRaw()).doesNotContainKey("class")
  }

  @Test
  fun testLinkWithType() {
    val link =
        Link.newBuilder("containsChild", URI.create("http://localhost:8080"))
            .clazz("dummyclass")
            .build()

    assertThat(link.clazz.size).isEqualTo(1)
    assertThat(link.firstClass).isEqualTo("dummyclass")
    assertThat(link.toRaw()).containsKey("class")
  }

  @Test
  fun testToBuilder() {
    val link =
        Link.newBuilder("rel", URI.create("uri"))
            .clazz("class")
            .title("title")
            .type("type")
            .build()
            .toBuilder()
            .rel("other")
            .build()

    verifyRoot("LinkTest.ToBuilder.siren.json", Root.newBuilder().links(link).build())
  }
}
