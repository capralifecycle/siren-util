package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class ActionTest {

  @Test
  fun testAction() {
    val root =
        Root.newBuilder()
            .clazz("type")
            .actions(
                Action.newBuilder("action-name", URI.create("http://example.com:8080/"))
                    .method(Action.Method.POST)
                    .fields(
                        Field.newBuilder("date").type(Field.Type.DATETIME).build(),
                        Field.newBuilder("url").type(Field.Type.URL).build())
                    .build())
            .build()

    verifyRoot("ActionTest1.siren.json", root)
  }

  @Test
  fun testInvalidHref() {
    val json =
        Root.newBuilder()
            .actions(
                Action.newBuilder("action-name", URI.create("http://example.com:8080/")).build())
            .build()
            .toJson()
            .replace("http://example.com:8080/", "::")

    try {
      Root.fromJson(json)
      fail("Exception expected")
    } catch (e: IllegalArgumentException) {
      assertEquals("Expected scheme name at index 0: ::", e.message)
    }
  }

  @Test
  fun testToBuilder() {
    val action =
        Action.newBuilder("name", URI.create("uri"))
            .title("title")
            .clazz("class")
            .method(Action.Method.GET)
            .href(URI.create("uri"))
            .type("type")
            .fields(Field.newBuilder("field").build())
            .build()
            .toBuilder()
            .name("new name")
            .build()

    verifyRoot("ActionTest.ToBuilder.siren.json", Root.newBuilder().actions(action).build())
  }
}
