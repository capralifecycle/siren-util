package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.jupiter.api.Test

class FieldTest {

  @Test
  fun testToBuilder() {
    val field =
        Field.newBuilder("name")
            .clazz("class")
            .type(Field.Type.NUMBER)
            .title("title")
            .value("some value")
            .build()
            .toBuilder()
            .name("other")
            .build()

    verifyRoot(
        "FieldTest.ToBuilder.siren.json",
        Root.newBuilder()
            .actions(Action.newBuilder("name", URI.create("uri")).fields(field).build())
            .build())
  }
}
