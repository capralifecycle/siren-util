package no.capraconsulting.siren;

import org.junit.Test;

import java.net.URI;

import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test
    public void testInvalidHref() {
        String json = Root
            .newBuilder()
            .actions(
                Action
                    .newBuilder(
                        "action-name",
                        URI.create("http://example.com:8080/")
                    )
                    .build()
            )
            .build()
            .toJson()
            .replace("http://example.com:8080/", "::");

        try {
            Root.fromJson(json);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
            assertEquals("Expected scheme name at index 0: ::", e.getMessage());
        }
    }
}
