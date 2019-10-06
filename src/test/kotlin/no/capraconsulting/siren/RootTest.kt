package no.capraconsulting.siren

import java.net.URI
import java.util.Collections.emptyList
import no.capraconsulting.siren.internal.getResource
import no.capraconsulting.siren.internal.parseAndVerifyRootStrict
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

class RootTest {

    @Test
    fun testFromRawWithNullValues() {
        val rootMap = mapOf(
            Siren.CLASS to listOf("class"),
            Siren.ENTITIES to null,
            Siren.LINKS to null,
            Siren.PROPERTIES to mapOf(
                "prop1" to "val1",
                "prop2" to "val2"
            )
        )

        val rootEntity = Root.fromRaw(rootMap)

        assertTrue(rootEntity.links.isEmpty())
        assertTrue(rootEntity.entities.isEmpty())
        assertEquals(1, rootEntity.clazz.size.toLong())
        assertEquals("val1", rootEntity.properties["prop1"])
        assertEquals("val2", rootEntity.properties["prop2"])
    }

    @Test
    fun testFromRawWithoutEntitiesAndLinks() {
        val rootMap = mapOf(
            Siren.CLASS to listOf("class"),
            Siren.PROPERTIES to mapOf(
                "prop1" to "val1",
                "prop2" to "val2"
            )
        )

        val root = Root.fromRaw(rootMap)

        assertTrue(root.links.isEmpty())
        assertTrue(root.entities.isEmpty())

        assertNotNull(root.clazz)
        assertEquals(1, root.clazz.size.toLong())

        assertNotNull(root.properties)
        assertEquals("val1", root.properties["prop1"])
        assertEquals("val2", root.properties["prop2"])
    }

    @Test
    fun testGetEntitiesWithEntities() {
        val root = Root
            .newBuilder()
            .entities(
                EmbeddedRepresentation
                    .newBuilder("parent")
                    .properties(
                        mapOf(
                            "prop1" to "val1",
                            "prop2" to "val2"
                        )
                    )
                    .build()
            )
            .build()

        assertNotNull(root.entities)
        assertEquals(1, root.entities.size.toLong())
        val subEntity = root.embeddedRepresentations[0]

        assertNotNull(subEntity.properties)
        assertEquals("val1", subEntity.properties["prop1"])
        assertEquals("val2", subEntity.properties["prop2"])
    }

    @Test
    fun testShouldReturnEmptyListsAndMapsWhereNoData() {
        val root = Root
            .newBuilder()
            .build()

        // Fields.
        assertTrue(root.links.isEmpty())
        assertTrue(root.properties.isEmpty())
        assertTrue(root.clazz.isEmpty())
        assertTrue(root.entities.isEmpty())
        assertTrue(root.actions.isEmpty())
        assertNull(root.title) // not a list/map

        // Other getters.
        assertNull(root.firstClass)

        // Expect for the special getters.
        assertTrue(root.embeddedRepresentations.isEmpty())
        assertTrue(root.embeddedLinks.isEmpty())
    }

    @Test
    fun testReturnEmptyListWhenBuiltWithIt() {
        // As far as I can see an empty list is still valid in the Siren specification,
        // except where explicitly noted a list must contain items.

        val root = Root
            .newBuilder()
            .links(emptyList())
            .build()

        assertNotNull(root.links)
        assertEquals(0, root.links.size.toLong())
    }

    @Test
    fun testGetLinks() {
        val root = Root
            .newBuilder()
            .links(
                Link
                    .newBuilder("self", URI.create("http://localhost:80"))
                    .clazz("dummytype")
                    .build()
            )
            .build()

        assertNotNull(root.links)
        assertEquals(1, root.links.size.toLong())

        val firstLink = root.links[0]
        assertEquals(URI.create("http://localhost:80"), firstLink.href)

        assertEquals(1, firstLink.rel.size.toLong())
        assertEquals("self", firstLink.rel[0])
        assertEquals(firstLink.firstRel, firstLink.rel[0])

        assertNotNull(firstLink.clazz)
        assertEquals("dummytype", firstLink.clazz[0])
        assertEquals(firstLink.firstClass, firstLink.clazz[0])
    }

    @Test
    fun testGetProperties() {
        val root = Root.newBuilder()
            .properties(
                mapOf(
                    "prop1" to "val1",
                    "prop2" to "val2"
                )
            )
            .build()

        assertNotNull(root.properties)
        assertEquals(2, root.properties.size.toLong())
        assertEquals("val1", root.properties["prop1"])
    }

    @Test
    fun testGetClass() {
        val root = Root
            .newBuilder()
            .clazz("City")
            .build()

        assertNotNull(root.clazz)
        assertEquals(1, root.clazz.size.toLong())
        assertEquals("City", root.clazz[0])
    }

    @Test
    fun testToRaw() {
        val root = Root.newBuilder()
            .entities(
                EmbeddedRepresentation
                    .newBuilder("parent")
                    .properties(
                        mapOf(
                            "prop1" to "val1",
                            "prop2" to "val2"
                        )
                    )
                    .build()
            )
            .build()

        val raw = root.toRaw()

        assertFalse(raw.containsKey(Siren.PROPERTIES))
        assertFalse(raw.containsKey(Siren.CLASS))
        assertTrue(raw.containsKey(Siren.ENTITIES))
        assertFalse(raw.containsKey(Siren.LINKS))

        val entities = raw.getValue(Siren.ENTITIES).asList()
        assertEquals(1, entities.size.toLong())

        val firstEntity = entities[0]!!.asMap()
        assertEquals(2, firstEntity.size.toLong())
        assertTrue(firstEntity.containsKey(Siren.PROPERTIES))
    }

    @Test
    fun testExample1() {
        val inputJson = getResource("RootTest.Example1.siren.json")
        val root = Root.fromJson(inputJson)

        val outputJson = root.toJson()

        assertNotEquals(
            "toJson will not be equal to formatted input json",
            inputJson,
            outputJson
        )

        // But if we retry on the generated output it should be valid.
        parseAndVerifyRootStrict(outputJson)

        JSONAssert.assertEquals(
            "by ignoring formatting input will be equal to output",
            inputJson,
            outputJson,
            true
        )
    }

    @Test
    fun testEmptyListExcluded() {
        val inputJson = getResource("RootTest.WithEmptyElements.siren.json")
        val root = Root.fromJson(inputJson)

        verifyRoot("RootTest.WithEmptyElements.out.siren.json", root)
    }

    @Test
    fun testEquality() {
        val doc = getResource("RootTest.Example1.siren.json")
        val other = getResource("LinkTest.siren.json")

        val root1 = Root.fromJson(doc)
        val root2 = Root.fromJson(doc) // Read again to force other instances.
        val root3 = Root.fromJson(other) // Other doc should never equal.

        // Modify a field.
        val root4 = root1.copy(
            title = "Some other title"
        )

        // Modify contents of properties so it is not the same.
        val root5 = root1.copy(
            properties = LinkedHashMap(root1.properties).apply {
                set("custom", "value")
            }
        )

        // Remove field again so it moves forward to be the same as before.
        val root6 = root5.copy(
            properties = LinkedHashMap(root5.properties).apply {
                remove("custom")
            }
        )

        assertEquals(root1, root2)
        assertNotEquals(root1, root3)
        assertNotEquals(root1, root4)
        assertNotEquals(root1, root5)
        assertEquals(root1, root6)
    }
}
