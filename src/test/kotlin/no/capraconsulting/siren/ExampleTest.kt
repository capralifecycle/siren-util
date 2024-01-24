package no.capraconsulting.siren

import java.net.URI
import no.capraconsulting.siren.internal.verifyRoot
import org.junit.jupiter.api.Test

class ExampleTest {

  /**
   * Creates the Siren example structure and tests it against the provided JSON.
   *
   * **See also:** [Siren example](https://github.com/kevinswiber/siren.example)
   */
  @Test
  fun testSirenOfficialExample() {
    val rootEntity =
        Root.newBuilder()
            .clazz("order")
            .properties(mapOf("orderNumber" to 42, "itemCount" to 3, "status" to "pending"))
            .entities(
                EmbeddedLink.newBuilder(
                        "http://x.io/rels/order-items",
                        URI.create("http://api.x.io/orders/42/items"))
                    .clazz("items", "collection")
                    .build(),
                EmbeddedRepresentation.newBuilder("http://x.io/rels/customer")
                    .clazz("info", "customer")
                    .properties(mapOf("customerId" to "pj123", "name" to "Peter Joseph"))
                    .links(
                        Link.newBuilder("self", URI.create("http://api.x.io/customers/pj123"))
                            .build())
                    .build())
            .actions(
                Action.newBuilder("add-item", URI.create("http://api.x.io/orders/42/items"))
                    .title("Add Item")
                    .method(Action.Method.POST)
                    .type("application/x-www-form-urlencoded")
                    .fields(
                        Field.newBuilder("orderNumber").type(Field.Type.HIDDEN).value("42").build(),
                        Field.newBuilder("productCode").type(Field.Type.TEXT).build(),
                        Field.newBuilder("quantity").type(Field.Type.NUMBER).build())
                    .build())
            .links(
                Link.newBuilder("self", URI.create("http://api.x.io/orders/42")).build(),
                Link.newBuilder("previous", URI.create("http://api.x.io/orders/41")).build(),
                Link.newBuilder("next", URI.create("http://api.x.io/orders/43")).build())
            .build()

    verifyRoot("SirenOfficialExample.siren.json", rootEntity)
  }

  /**
   * Creates the fizzbuzzaas example structure and tests it against the provided JSON.
   *
   * **See also:** [FizzBuzzAAS example](http://fizzbuzzaas.herokuapp.com/)
   */
  @Test
  fun testFizzbuzzaasExample() {
    val root =
        Root.newBuilder()
            .links(
                Link.newBuilder("home", URI.create("/")).build(),
                Link.newBuilder("first", URI.create("/fizzbuzz?number=1")).build(),
                Link.newBuilder("last", URI.create("/fizzbuzz?number=100")).build())
            .actions(
                Action.newBuilder("custom-fizzbuzz", URI.create("/fizzbuzz"))
                    .title("Custom FizzBuzz")
                    .method(Action.Method.GET)
                    .type("application/x-www-form-urlencoded")
                    .fields(
                        Field.newBuilder("add").type(Field.Type.NUMBER).value("1").build(),
                        Field.newBuilder("startsAt").type(Field.Type.NUMBER).value("1").build(),
                        Field.newBuilder("endsAt").type(Field.Type.NUMBER).value("100").build(),
                        Field.newBuilder("firstNumber").type(Field.Type.NUMBER).value("3").build(),
                        Field.newBuilder("secondNumber").type(Field.Type.NUMBER).value("5").build(),
                        Field.newBuilder("embed").type(Field.Type.NUMBER).build())
                    .build(),
                Action.newBuilder("get-fizzbuzz-value", URI.create("/fizzbuzz"))
                    .title("Get FizzBuzz Value")
                    .method(Action.Method.GET)
                    .type("application/x-www-form-urlencoded")
                    .fields(Field.newBuilder("number").type(Field.Type.NUMBER).build())
                    .build())
            .build()

    verifyRoot("FizzbuzzaasExample.siren.json", root)
  }
}
