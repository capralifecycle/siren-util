package no.capraconsulting.siren

import no.capraconsulting.siren.internal.verifyRoot
import org.junit.Test
import java.net.URI

class ActionTest {

    @Test
    fun testAction() {
        val root = Root
            .newBuilder()
            .clazz("type")
            .actions(
                Action
                    .newBuilder(
                        "action-name",
                        URI.create("http://example.com:8080/")
                    )
                    .method(Action.Method.POST)
                    .fields(
                        Field.newBuilder("date").type(Field.Type.DATETIME).build(),
                        Field.newBuilder("url").type(Field.Type.URL).build()
                    )
                    .build()
            )
            .build()

        verifyRoot("ActionTest1.siren.json", root)
    }
}
