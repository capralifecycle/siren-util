package no.capraconsulting.siren;

import org.junit.Test;

import java.net.URI;

import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;

public class ActionTest {

    @Test
    public void testAction() {
        Root root = Root
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
            .build();

        verifyRoot("ActionTest1.siren.json", root);
    }
}
