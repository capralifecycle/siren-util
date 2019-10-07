package no.capraconsulting.siren;

import java.net.URI;
import org.junit.Test;

import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;

public class FieldTest {

    @Test
    public void testToBuilder() {
        Field field = Field
            .newBuilder("name")
            .clazz("class")
            .type(Field.Type.NUMBER)
            .title("title")
            .value("some value")
            .build()
            .toBuilder()
            .name("other")
            .build();

        verifyRoot(
            "FieldTest.ToBuilder.siren.json",
            Root.newBuilder()
                .actions(Action.newBuilder("name", URI.create("uri")).fields(field).build())
                .build()
        );
    }
}
