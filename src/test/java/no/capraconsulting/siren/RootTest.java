package no.capraconsulting.siren;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.capraconsulting.siren.internal.TestUtil.entry;
import static no.capraconsulting.siren.internal.TestUtil.getResource;
import static no.capraconsulting.siren.internal.TestUtil.mapOf;
import static no.capraconsulting.siren.internal.TestUtil.parseAndVerifyRootStrict;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsList;
import static no.capraconsulting.siren.internal.util.GenericsUtil.objectAsMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RootTest {

    @Test
    public void testFromRawWithNullValues() {
        Map<String, Object> rootMap = mapOf(
            entry(Siren.CLASS, singletonList("class")),
            entry(Siren.ENTITIES, null),
            entry(Siren.LINKS, null),
            entry(Siren.PROPERTIES, mapOf(
                entry("prop1", "val1"),
                entry("prop2", "val2")
            ))
        );

        Root rootEntity = Root.fromRaw(rootMap);

        assertTrue(rootEntity.getLinks().isEmpty());
        assertTrue(rootEntity.getEntities().isEmpty());
        assertEquals(1, rootEntity.getClazz().size());
        assertEquals("val1", rootEntity.getProperties().get("prop1"));
        assertEquals("val2", rootEntity.getProperties().get("prop2"));
    }

    @Test
    public void testFromRawWithoutEntitiesAndLinks() {
        Map<String, Object> rootMap = mapOf(
            entry(Siren.CLASS, singletonList("class")),
            entry(Siren.PROPERTIES, mapOf(
                entry("prop1", "val1"),
                entry("prop2", "val2")
            ))
        );

        Root root = Root.fromRaw(rootMap);

        assertTrue(root.getLinks().isEmpty());
        assertTrue(root.getEntities().isEmpty());

        assertNotNull(root.getClazz());
        assertEquals(1, root.getClazz().size());

        assertNotNull(root.getProperties());
        assertEquals("val1", root.getProperties().get("prop1"));
        assertEquals("val2", root.getProperties().get("prop2"));
    }

    @Test
    public void testGetEntitiesWithEntities() {
        Root root = Root
            .newBuilder()
            .entities(
                EmbeddedRepresentation
                    .newBuilder("parent")
                    .properties(mapOf(
                        entry("prop1", "val1"),
                        entry("prop2", "val2")
                    ))
                    .build()
            )
            .build();

        assertNotNull(root.getEntities());
        assertEquals(1, root.getEntities().size());
        EmbeddedRepresentation subEntity = root.getEmbeddedRepresentations().get(0);

        assertNotNull(subEntity.getProperties());
        assertEquals("val1", subEntity.getProperties().get("prop1"));
        assertEquals("val2", subEntity.getProperties().get("prop2"));
    }

    @Test
    public void testShouldReturnEmptyListsAndMapsWhereNoData() {
        Root root = Root
            .newBuilder()
            .build();

        // Fields.
        assertTrue(root.getLinks().isEmpty());
        assertTrue(root.getProperties().isEmpty());
        assertTrue(root.getClazz().isEmpty());
        assertTrue(root.getEntities().isEmpty());
        assertTrue(root.getActions().isEmpty());
        assertNull(root.getTitle()); // not a list/map

        // Other getters.
        assertNull(root.getFirstClass());

        // Expect for the special getters.
        assertTrue(root.getEmbeddedRepresentations().isEmpty());
        assertTrue(root.getEmbeddedLinks().isEmpty());
    }

    @Test
    public void testReturnEmptyListWhenBuiltWithIt() {
        // As far as I can see an empty list is still valid in the Siren specification,
        // except where explicitly noted a list must contain items.

        Root root = Root
            .newBuilder()
            .links(emptyList())
            .build();

        assertNotNull(root.getLinks());
        assertEquals(0, root.getLinks().size());
    }

    @Test
    public void testGetLinks() {
        Root root = Root
            .newBuilder()
            .links(
                Link
                    .newBuilder("self", URI.create("http://localhost:80"))
                    .clazz("dummytype")
                    .build()
            )
            .build();

        assertNotNull(root.getLinks());
        assertEquals(1, root.getLinks().size());

        Link firstLink = root.getLinks().get(0);
        assertEquals(URI.create("http://localhost:80"), firstLink.getHref());

        assertEquals(1, firstLink.getRel().size());
        assertEquals("self", firstLink.getRel().get(0));
        assertEquals(firstLink.getFirstRel(), firstLink.getRel().get(0));

        assertNotNull(firstLink.getClazz());
        assertEquals("dummytype", firstLink.getClazz().get(0));
        assertEquals(firstLink.getFirstClass(), firstLink.getClazz().get(0));
    }

    @Test
    public void testGetProperties() {
        Root root = Root.newBuilder()
            .properties(mapOf(
                entry("prop1", "val1"),
                entry("prop2", "val2")
            ))
            .build();

        assertNotNull(root.getProperties());
        assertEquals(2, root.getProperties().size());
        assertEquals("val1", root.getProperties().get("prop1"));
    }

    @Test
    public void testGetClass() {
        Root root = Root
            .newBuilder()
            .clazz("City")
            .build();

        assertNotNull(root.getClazz());
        assertEquals(1, root.getClazz().size());
        assertEquals("City", root.getClazz().get(0));
    }

    @Test
    public void testToRaw() {
        Root root = Root.newBuilder()
            .entities(
                EmbeddedRepresentation
                    .newBuilder("parent")
                    .properties(mapOf(
                        entry("prop1", "val1"),
                        entry("prop2", "val2")
                    ))
                    .build()
            )
            .build();

        Map<String, Object> raw = root.toRaw();

        assertFalse(raw.containsKey(Siren.PROPERTIES));
        assertFalse(raw.containsKey(Siren.CLASS));
        assertTrue(raw.containsKey(Siren.ENTITIES));
        assertFalse(raw.containsKey(Siren.LINKS));

        List<Object> entities = objectAsList(raw.get(Siren.ENTITIES));
        assertEquals(1, entities.size());

        Map<String, Object> firstEntity = objectAsMap(entities.get(0));
        assertEquals(2, firstEntity.size());
        assertTrue(firstEntity.containsKey(Siren.PROPERTIES));
    }

    @Test
    public void testExample1() throws Exception {
        String inputJson = getResource("RootTest.Example1.siren.json");
        Root root = Root.fromJson(inputJson);

        String outputJson = root.toJson();

        assertNotEquals(
            "toJson will not be equal to formatted input json",
            inputJson,
            outputJson
        );

        // But if we retry on the generated output it should be valid.
        parseAndVerifyRootStrict(outputJson);

        JSONAssert.assertEquals(
            "by ignoring formatting input will be equal to output",
            inputJson,
            outputJson,
            true
        );
    }

    @Test
    public void testInvalidDoc() {
        // Currently 'rel' is required for an embedded entity. We might
        // want to relax on this to allow parsing invalid documents but
        // try to enforce it when constructing new documents.

        String valid = getResource("RootTest.InvalidDoc1.valid.siren.json");
        String invalid = getResource("RootTest.InvalidDoc1.invalid.siren.json");

        Root rootValid = Root.fromJson(valid);
        assertEquals(1, rootValid.getEmbeddedRepresentations().size());

        try {
            Root.fromJson(invalid);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            // TODO: This message is not very helpful for the user.
            //  Either don't fail or improve the message.
            assertEquals("Casting to List failed. Found type null", e.getMessage());
        }
    }
}
