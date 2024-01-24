package no.capraconsulting.siren

import java.net.URI
import java.util.Collections.emptyList
import no.capraconsulting.siren.internal.getResource
import no.capraconsulting.siren.internal.util.asList
import no.capraconsulting.siren.internal.util.asMap
import no.capraconsulting.siren.internal.verifyRoot
import no.liflig.snapshot.verifyJsonSnapshot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class RootTest {

  @Test
  fun testFromRawWithNullValues() {
    val rootMap =
        mapOf(
            Siren.CLASS to listOf("class"),
            Siren.ENTITIES to null,
            Siren.LINKS to null,
            Siren.PROPERTIES to mapOf("prop1" to "val1", "prop2" to "val2"))

    val rootEntity = Root.fromRaw(rootMap)

    assertThat(rootEntity.links).isEmpty()
    assertThat(rootEntity.entities).isEmpty()
    assertThat(rootEntity.clazz.size).isEqualTo(1)
    assertThat(rootEntity.properties["prop1"]).isEqualTo("val1")
    assertThat(rootEntity.properties["prop2"]).isEqualTo("val2")
  }

  @Test
  fun testFromRawWithoutEntitiesAndLinks() {
    val rootMap =
        mapOf(
            Siren.CLASS to listOf("class"),
            Siren.PROPERTIES to mapOf("prop1" to "val1", "prop2" to "val2"))

    val root = Root.fromRaw(rootMap)

    assertThat(root.links).isEmpty()
    assertThat(root.entities).isEmpty()

    assertThat(root.clazz).isNotNull.hasSize(1)

    assertThat(root.properties).isNotNull
    assertThat(root.properties["prop1"]).isEqualTo("val1")
    assertThat(root.properties["prop2"]).isEqualTo("val2")
  }

  @Test
  fun `Siren object should contain correct embedded representations`() {
    val root =
        Root.newBuilder()
            .entities(
                EmbeddedRepresentation.newBuilder("parent")
                    .properties(mapOf("prop1" to "val1", "prop2" to "val2"))
                    .build())
            .build()

    assertThat(root.entities).isNotNull().hasSize(1)

    val subEntity = root.embeddedRepresentations[0]

    assertThat(subEntity.properties).isNotNull()
    assertThat(subEntity.properties["prop1"]).isEqualTo("val1")
    assertThat(subEntity.properties["prop2"]).isEqualTo("val2")
  }

  @Test
  fun `Should return empty lists and maps where no data`() {
    val root = Root.newBuilder().build()

    // Fields.
    assertThat(root.links).isEmpty()
    assertThat(root.properties).isEmpty()
    assertThat(root.clazz).isEmpty()
    assertThat(root.entities).isEmpty()
    assertThat(root.actions).isEmpty()
    assertThat(root.title).isNull() // not a list/map

    // Other getters.
    assertThat(root.firstClass).isNull()

    // Expect for the special getters.
    assertThat(root.embeddedRepresentations).isEmpty()
    assertThat(root.embeddedLinks).isEmpty()
  }

  @Test
  fun `Builder should return root with empty list when built with it`() {
    // As far as I can see an empty list is still valid in the Siren specification,
    // except where explicitly noted a list must contain items.

    val root = Root.newBuilder().links(emptyList()).build()

    assertThat(root.links).isNotNull().hasSize(0)
  }

  @Test
  fun `Siren object should encapsulate links`() {
    val root =
        Root.newBuilder()
            .links(
                Link.newBuilder("self", URI.create("http://localhost:80"))
                    .clazz("dummytype")
                    .build())
            .build()

    assertThat(root.links).isNotNull.hasSize(1)

    val firstLink = root.links[0]
    assertThat(firstLink.href).isEqualTo(URI.create("http://localhost:80"))

    assertThat(firstLink.rel).hasSize(1)
    assertThat(firstLink.rel[0]).isEqualTo("self").isEqualTo(firstLink.firstRel)

    assertThat(firstLink.clazz).isNotNull()
    assertThat(firstLink.clazz[0]).isEqualTo("dummytype").isEqualTo(firstLink.firstClass)
  }

  @Test
  fun `A constructed siren object should contain the correct properties`() {
    val root = Root.newBuilder().properties(mapOf("prop1" to "val1", "prop2" to "val2")).build()

    assertThat(root.properties).isNotNull().hasSize(2)
    assertThat(root.properties["prop1"]).isEqualTo("val1")
  }

  @Test
  fun `A constructed siren object should contain the correct class`() {
    val root = Root.newBuilder().clazz("City").build()

    assertThat(root.clazz).isNotNull().hasSize(1)
    assertThat(root.clazz[0]).isEqualTo("City")
  }

  @Test
  fun `Converting a siren object to a raw list and hash map representation should not loose information`() {
    val root =
        Root.newBuilder()
            .entities(
                EmbeddedRepresentation.newBuilder("parent")
                    .properties(mapOf("prop1" to "val1", "prop2" to "val2"))
                    .build())
            .build()

    val raw = root.toRaw()

    assertThat(raw.entries.find { it.key == Siren.PROPERTIES }).isNull()
    assertThat(raw.entries.find { it.key == Siren.CLASS }).isNull()
    assertThat(raw.entries.find { it.key == Siren.LINKS }).isNull()
    assertThat(raw.entries.find { it.key == Siren.ENTITIES }).isNotNull()

    val entities = raw.getValue(Siren.ENTITIES).asList()
    assertThat(entities).hasSize(1)

    val firstEntity = entities[0]!!.asMap()
    assertThat(firstEntity).hasSize(2).containsKey(Siren.PROPERTIES)
  }

  @Test
  fun testExample1() {
    val inputJson = getResource("RootTest.Example1.siren.json")
    val root = Root.fromJson(inputJson)

    val outputJson = root.toJson()

    assertThat(outputJson)
        .`as` { "toJson will not be equal to formatted input json" }
        .isNotEqualTo(inputJson)

    verifyJsonSnapshot("/roottest/example1.json", outputJson)
    verifyJsonSnapshot("/roottest/example1.json", inputJson)
  }

  @Test
  fun testEmptyListExcluded() {
    val inputJson = getResource("RootTest.WithEmptyElements.siren.json")
    val root = Root.fromJson(inputJson)

    verifyRoot("RootTest.WithEmptyElements.out.siren.json", root)
  }

  @Test
  fun `Siren objects should be equal when their data is equal`() {
    val doc = getResource("RootTest.Example1.siren.json")
    val other = getResource("LinkTest.siren.json")

    val root1 = Root.fromJson(doc)
    val root2 = Root.fromJson(doc) // Read again to force other instances.
    val root3 = Root.fromJson(other) // Other doc should never equal.

    // Modify a field.
    val root4 = root1.copy(title = "Some other title")

    // Modify contents of properties so it is not the same.
    val root5 =
        root1.copy(properties = LinkedHashMap(root1.properties).apply { set("custom", "value") })

    // Remove field again so it moves forward to be the same as before.
    val root6 = root5.copy(properties = LinkedHashMap(root5.properties).apply { remove("custom") })

    assertThat(root1).isEqualTo(root2)
    assertThat(root1).isNotEqualTo(root3)
    assertThat(root1).isNotEqualTo(root4)
    assertThat(root1).isNotEqualTo(root5)
    assertThat(root1).isEqualTo(root6)
  }

  @Test
  fun testCompleteData() {
    val root =
        Root.newBuilder()
            .clazz("order")
            .title("Complete data")
            .properties(mapOf("orderNumber" to 42, "status" to "pending"))
            .entities(
                EmbeddedLink.newBuilder(
                        "http://x.io/rels/order-items",
                        URI.create("http://api.x.io/orders/42/items"))
                    .clazz("embeddedlinkclass")
                    .title("Embedded link")
                    .type("application/json")
                    .build(),
                EmbeddedRepresentation.newBuilder("http://x.io/rels/customer")
                    .clazz("info", "customer")
                    .title("Customer Peter Joseph")
                    .properties(mapOf("customerId" to "pj123", "name" to "Peter Joseph"))
                    .entities(
                        EmbeddedLink.newBuilder(
                                "http://x.io/rels/order-items",
                                URI.create("http://api.x.io/orders/44/items"))
                            .clazz("embeddedlinkclass")
                            .title("Another embedded link")
                            .type("application/json")
                            .build())
                    .links(
                        Link.newBuilder("self", URI.create("http://api.x.io/customers/pj123"))
                            .title("Customer pj123")
                            .clazz("info", "customer")
                            .type("application/json")
                            .build())
                    .actions(
                        Action.newBuilder(
                                "update-address",
                                URI.create("http://api.x.io/customers/pj123/address"))
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
                                    .build())
                            .build())
                    .build())
            .actions(
                Action.newBuilder("add-item", URI.create("http://api.x.io/orders/42/items"))
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
                            .build())
                    .build())
            .links(
                Link.newBuilder("self", URI.create("http://api.x.io/orders/42"))
                    .clazz("linkclass")
                    .title("A link")
                    .type("application/json")
                    .build())
            .build()

    verifyRoot("RootTest.CompleteData.siren.json", root)

    // Verify all getters.

    assertThat(root.clazz).isEqualTo(listOf("order"))
    assertThat(root.firstClass).isEqualTo("order")
    assertThat(root.title).isEqualTo("Complete data")
    assertThat(root.properties).isEqualTo(mapOf("orderNumber" to 42, "status" to "pending"))

    assertThat(root.entities).hasSize(2)
    assertThat(listOf(root.entities[0])).isEqualTo(root.embeddedLinks)
    assertThat(listOf(root.entities[1])).isEqualTo(root.embeddedRepresentations)

    val emblink = root.embeddedLinks[0]
    assertThat(emblink.rel).isEqualTo(listOf("http://x.io/rels/order-items"))
    assertThat(emblink.firstRel).isEqualTo(("http://x.io/rels/order-items"))
    assertThat(emblink.href).isEqualTo(URI.create("http://api.x.io/orders/42/items"))
    assertThat(emblink.clazz).isEqualTo(listOf("embeddedlinkclass"))
    assertThat(emblink.firstClass).isEqualTo("embeddedlinkclass")
    assertThat(emblink.title).isEqualTo("Embedded link")
    assertThat(emblink.type).isEqualTo("application/json")

    val embrepr = root.embeddedRepresentations[0]
    assertThat(embrepr.rel).isEqualTo(listOf("http://x.io/rels/customer"))
    assertThat(embrepr.firstRel).isEqualTo("http://x.io/rels/customer")
    assertThat(embrepr.clazz).isEqualTo(listOf("info", "customer"))
    assertThat(embrepr.firstClass).isEqualTo("info")
    assertThat(embrepr.title).isEqualTo("Customer Peter Joseph")

    assertThat(embrepr.properties)
        .isEqualTo(mapOf("customerId" to "pj123", "name" to "Peter Joseph"))

    assertThat(embrepr.entities).hasSize(1)
    assertThat(embrepr.embeddedLinks).hasSize(1)
    assertThat(embrepr.embeddedRepresentations).hasSize(0)

    // Skipping verify of second EmbeddedLink.
    assertThat(embrepr.links).hasSize(1)

    val link = embrepr.links[0]
    assertThat(link.rel).isEqualTo(listOf("self"))
    assertThat(link.firstRel).isEqualTo("self")
    assertThat(link.href).isEqualTo(URI.create("http://api.x.io/customers/pj123"))
    assertThat(link.clazz).isEqualTo(listOf("info", "customer"))
    assertThat(link.firstClass).isEqualTo("info")
    assertThat(link.title).isEqualTo("Customer pj123")
    assertThat(link.type).isEqualTo("application/json")

    assertThat(embrepr.actions).hasSize(1)

    val action = embrepr.actions[0]
    assertThat(action.name).isEqualTo("update-address")
    assertThat(action.href).isEqualTo(URI.create("http://api.x.io/customers/pj123/address"))
    assertThat(action.method).isEqualTo("PUT")
    assertThat(action.title).isEqualTo("Update Address")
    assertThat(action.clazz).isEqualTo(listOf("update", "address"))
    assertThat(action.type).isEqualTo("application/json")
    assertThat(action.fields).hasSize(2)

    // Skipping verify of second Field.
    val field = action.fields[0]
    assertThat(field.name).isEqualTo("customerId")
    assertThat(field.clazz).isEqualTo(listOf("some class"))
    assertThat(field.type).isEqualTo("hidden")
    assertThat(field.title).isEqualTo("Customer ID")
    assertThat(field.value).isEqualTo("pj123")

    // Action already verified above. Skipping here.

    // Link already verified above. Skipping here.
    assertThat(root.links).hasSize(1)
  }

  @Test
  fun `Should fail when constructing siren from invalid data`() {
    // Currently 'rel' is required for an embedded entity. We might
    // want to relax on this to allow parsing invalid documents but
    // try to enforce it when constructing new documents.

    val valid = getResource("RootTest.InvalidDoc1.valid.siren.json")
    val invalid = getResource("RootTest.InvalidDoc1.invalid.siren.json")

    val rootValid = Root.fromJson(valid)
    assertThat(rootValid.embeddedRepresentations).hasSize(1)

    assertThatExceptionOfType(java.util.NoSuchElementException::class.java)
        .isThrownBy { Root.fromJson(invalid) }
        .withMessage("Key rel is missing in the map.")
  }

  @Test
  fun testToBuilder() {
    val root =
        Root.newBuilder()
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
