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
import org.junit.Assert.fail
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

    @Test
    fun testCompleteData() {
        val root = Root.newBuilder()
            .clazz("order")
            .title("Complete data")
            .properties(
                mapOf(
                    "orderNumber" to 42,
                    "status" to "pending"
                )
            )
            .entities(
                EmbeddedLink
                    .newBuilder(
                        "http://x.io/rels/order-items",
                        URI.create("http://api.x.io/orders/42/items")
                    )
                    .clazz("embeddedlinkclass")
                    .title("Embedded link")
                    .type("application/json")
                    .build(),
                EmbeddedRepresentation
                    .newBuilder("http://x.io/rels/customer")
                    .clazz("info", "customer")
                    .title("Customer Peter Joseph")
                    .properties(
                        mapOf(
                            "customerId" to "pj123",
                            "name" to "Peter Joseph"
                        )

                    )
                    .entities(
                        EmbeddedLink
                            .newBuilder(
                                "http://x.io/rels/order-items",
                                URI.create("http://api.x.io/orders/44/items")
                            )
                            .clazz("embeddedlinkclass")
                            .title("Another embedded link")
                            .type("application/json")
                            .build()
                    )
                    .links(
                        Link
                            .newBuilder("self", URI.create("http://api.x.io/customers/pj123"))
                            .title("Customer pj123")
                            .clazz("info", "customer")
                            .type("application/json")
                            .build()
                    )
                    .actions(
                        Action
                            .newBuilder(
                                "update-address",
                                URI.create("http://api.x.io/customers/pj123/address")
                            )
                            .method(Action.Method.PUT)
                            .title("Update Address")
                            .clazz("update", "address")
                            .type("application/json")
                            .fields(
                                Field.newBuilder("customerId")
                                    .clazz("some class")
                                    .type(Field.Type.HIDDEN)
                                    .title("Customer ID")
                                    .value("pj123")
                                    .build(),
                                Field.newBuilder("address")
                                    .clazz("some class")
                                    .type(Field.Type.TEXT)
                                    .title("Address")
                                    .value(null)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .actions(
                Action
                    .newBuilder(
                        "add-item",
                        URI.create("http://api.x.io/orders/42/items")
                    )
                    .method(Action.Method.POST)
                    .title("Add Item")
                    .clazz("add-item")
                    .type("application/json")
                    .fields(
                        Field.newBuilder("orderNumber")
                            .clazz("some class")
                            .type(Field.Type.HIDDEN)
                            .title("Order Number")
                            .value("42")
                            .build(),
                        Field.newBuilder("productCode")
                            .clazz("some class")
                            .type(Field.Type.TEXT)
                            .title("Product Code")
                            .value(null)
                            .build()
                    )
                    .build()
            )
            .links(
                Link.newBuilder("self", URI.create("http://api.x.io/orders/42"))
                    .clazz("linkclass")
                    .title("A link")
                    .type("application/json")
                    .build()
            )
            .build()

        verifyRoot("RootTest.CompleteData.siren.json", root)

        // Verify all getters.

        assertEquals(listOf("order"), root.clazz)
        assertEquals("order", root.firstClass)
        assertEquals("Complete data", root.title)
        assertEquals(
            mapOf(
                "orderNumber" to 42,
                "status" to "pending"
            ),
            root.properties
        )

        assertEquals(2, root.entities.size)
        assertEquals(listOf(root.entities[0]), root.embeddedLinks)
        assertEquals(listOf(root.entities[1]), root.embeddedRepresentations)

        val emblink = root.embeddedLinks[0]
        assertEquals(listOf("http://x.io/rels/order-items"), emblink.rel)
        assertEquals("http://x.io/rels/order-items", emblink.firstRel)
        assertEquals(URI.create("http://api.x.io/orders/42/items"), emblink.href)
        assertEquals(listOf("embeddedlinkclass"), emblink.clazz)
        assertEquals("embeddedlinkclass", emblink.firstClass)
        assertEquals("Embedded link", emblink.title)
        assertEquals("application/json", emblink.type)

        val embrepr = root.embeddedRepresentations[0]
        assertEquals(listOf("http://x.io/rels/customer"), embrepr.rel)
        assertEquals("http://x.io/rels/customer", embrepr.firstRel)
        assertEquals(listOf("info", "customer"), embrepr.clazz)
        assertEquals("info", embrepr.firstClass)
        assertEquals("Customer Peter Joseph", embrepr.title)
        assertEquals(
            mapOf(
                "customerId" to "pj123",
                "name" to "Peter Joseph"
            ),
            embrepr.properties
        )
        assertEquals(1, embrepr.entities.size)
        assertEquals(1, embrepr.embeddedLinks.size)
        assertEquals(0, embrepr.embeddedRepresentations.size)

        // Skipping verify of second EmbeddedLink.

        assertEquals(1, embrepr.links.size)

        val link = embrepr.links[0]
        assertEquals(listOf("self"), link.rel)
        assertEquals("self", link.firstRel)
        assertEquals(URI.create("http://api.x.io/customers/pj123"), link.href)
        assertEquals(listOf("info", "customer"), link.clazz)
        assertEquals("info", link.firstClass)
        assertEquals("Customer pj123", link.title)
        assertEquals("application/json", link.type)

        assertEquals(1, embrepr.actions.size)

        val action = embrepr.actions[0]
        assertEquals("update-address", action.name)
        assertEquals(URI.create("http://api.x.io/customers/pj123/address"), action.href)
        assertEquals("PUT", action.method)
        assertEquals("Update Address", action.title)
        assertEquals(listOf("update", "address"), action.clazz)
        assertEquals("application/json", action.type)
        assertEquals(2, action.fields.size)

        // Skipping verify of second Field.
        val field = action.fields[0]
        assertEquals("customerId", field.name)
        assertEquals(listOf("some class"), field.clazz)
        assertEquals("hidden", field.type)
        assertEquals("Customer ID", field.title)
        assertEquals("pj123", field.value)

        // Action already verified above. Skipping here.

        // Link already verified above. Skipping here.
        assertEquals(1, root.links.size)
    }

    @Test
    fun testInvalidDoc() {
        // Currently 'rel' is required for an embedded entity. We might
        // want to relax on this to allow parsing invalid documents but
        // try to enforce it when constructing new documents.

        val valid = getResource("RootTest.InvalidDoc1.valid.siren.json")
        val invalid = getResource("RootTest.InvalidDoc1.invalid.siren.json")

        val rootValid = Root.fromJson(valid)
        assertEquals(1, rootValid.embeddedRepresentations.size)

        try {
            Root.fromJson(invalid)
            fail("Exception not thrown")
        } catch (e: NoSuchElementException) {
            assertEquals("Key rel is missing in the map.", e.message)
        }
    }

    @Test
    fun testToBuilder() {
        val root = Root.newBuilder()
            .clazz("class")
            .title("title")
            .properties(mapOf("some key" to "value"))
            .entities(EmbeddedLink.newBuilder("emblink", URI.create("uri")).build())
            .actions(Action.newBuilder("link", URI.create("uri")).build())
            .links(Link.newBuilder("linkrel", URI.create("uri")).build())
            .build()
            .toBuilder()
            .title("other")
            .build()

        verifyRoot("RootTest.ToBuilder.siren.json", root)
    }
}
