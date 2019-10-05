package no.capraconsulting.siren.internal

import no.capraconsulting.siren.Root
import org.junit.Assert.assertEquals
import org.skyscreamer.jsonassert.JSONAssert

fun getResource(name: String): String =
    object : Any() {}.javaClass.classLoader.getResource(name)!!.readText()

/**
 * Verify Root object against snapshot and that it can be serialized to JSON, deserialized back
 * and that another round of serialization produces exactly the same JSON. This ensures we have
 * no data loss in serialization/deserialization.
 */
fun verifyRoot(snapshotName: String, root: Root) {
    val json = root.toJson()
    verifySnapshot(snapshotName, json)

    parseAndVerifyRootStrict(json)
}

/**
 * Verify a JSON value can be parsed to a Root object and serialized again without any
 * data loss. Return the parsed Root object.
 *
 * This performs strict check so that the input JSON must be formatted exactly as
 * the Root object will produce.
 */
fun parseAndVerifyRootStrict(json: String): Root {
    val root = Root.fromJson(json)

    // We verify JSON exactly as we want to avoid any changes in the JSON structure
    // when reading into the model and writing back.
    assertEquals("No change when deserialize and serialize", json, root.toJson())

    return root
}

/**
 * Verify a JSON value can be parsed to a Root object and serialized again without any
 * data loss. Return the parsed Root object.
 *
 * This performs relaxed check so that the JSON format only has to have the same data,
 * but can have different whitespace, map ordering and some data formatting.
 */
fun parseAndVerifyRootRelaxed(json: String): Root {
    val root = Root.fromJson(json)

    JSONAssert.assertEquals(
        "Root object produces similar JSON",
        json,
        root.toJson(),
        true
    )

    return root
}
