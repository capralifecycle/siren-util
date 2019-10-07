package no.capraconsulting.siren;

import org.junit.Test;

import java.net.URI;

import static no.capraconsulting.siren.internal.TestUtil.getResource;
import static no.capraconsulting.siren.internal.TestUtil.parseAndVerifyRootRelaxed;
import static no.capraconsulting.siren.internal.TestUtil.verifyRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LinkTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testParse() {
        String json = getResource("LinkTest.siren.json");

        Root root = parseAndVerifyRootRelaxed(json);

        assertEquals(1, root.getLinks().size());
        assertEquals("city", root.getLinks().get(0).getFirstClass());
    }

    @Test
    public void testLinkWithoutType() {
        Link link = Link.newBuilder("containsChild", URI.create("http://localhost:8080")).build();
        assertTrue(link.getClazz().isEmpty());
        assertNull(link.getFirstClass());
        assertFalse(link.toRaw().containsKey("class"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testLinkWithType() {
        Link link = Link
            .newBuilder("containsChild", URI.create("http://localhost:8080"))
            .clazz("dummyclass")
            .build();

        assertEquals(1, link.getClazz().size());
        assertEquals("dummyclass", link.getFirstClass());
        assertTrue(link.toRaw().containsKey("class"));
    }

    @Test
    public void testToBuilder() {
        Link link = Link
            .newBuilder("rel", URI.create("uri"))
            .clazz("class")
            .title("title")
            .type("type")
            .build()
            .toBuilder()
            .rel("other")
            .build();

        verifyRoot(
            "LinkTest.ToBuilder.siren.json",
            Root.newBuilder().links(link).build()
        );
    }
}
