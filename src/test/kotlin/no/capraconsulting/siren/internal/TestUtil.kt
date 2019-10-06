package no.capraconsulting.siren.internal

import no.capraconsulting.siren.Root
import org.junit.Assert.assertEquals
import org.skyscreamer.jsonassert.JSONAssert

fun getResource(name: String): String =
    object : Any() {}.javaClass.classLoader.getResource(name)!!.readText()

fun verifyRoot(snapshotName: String, root: Root) {
    val json = root.toJson()
    verifySnapshot(snapshotName, json)

    parseAndVerifyRootStrict(json)
}

fun parseAndVerifyRootStrict(json: String): Root {
    val root = Root.fromJson(json)

    // We verify JSON exactly as we want to avoid any changes in the JSON structure
    // when reading into the model and writing back.
    assertEquals("No change when deserialize and serialize", json, root.toJson())

    return root
}

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
