package no.capraconsulting.siren.internal

import no.capraconsulting.siren.Root
import no.liflig.snapshot.verifyJsonSnapshot
import org.skyscreamer.jsonassert.JSONAssert

fun getResource(name: String): String =
    object : Any() {}.javaClass.classLoader.getResource(name)!!.readText()

/**
 * Verify Root object against snapshot and that it can be serialized to JSON, deserialized back and
 * that another round of serialization produces exactly the same JSON. This ensures we have no data
 * loss in serialization/deserialization.
 */
fun verifyRoot(snapshotName: String, root: Root) {
  val json = root.toJson()
  verifyJsonSnapshot(snapshotName, json)
}

/**
 * Verify a JSON value can be parsed to a Root object and serialized again without any data loss.
 * Return the parsed Root object.
 *
 * This performs relaxed check so that the JSON format only has to have the same data, but can have
 * different whitespace, map ordering and some data formatting.
 */
fun parseAndVerifyRootRelaxed(json: String): Root {
  val root = Root.fromJson(json)

  JSONAssert.assertEquals("Root object produces similar JSON", json, root.toJson(), true)

  return root
}
