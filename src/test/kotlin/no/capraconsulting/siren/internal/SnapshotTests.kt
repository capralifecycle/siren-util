package no.capraconsulting.siren.internal

import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import org.junit.Assert.assertEquals
import org.junit.ComparisonFailure

private const val REGENERATE_SNAPSHOTS = "REGENERATE_SNAPSHOTS"

fun verifySnapshot(name: String, value: String) =
    try {
        verifySnapshotInternal(name, value)
    } catch (e: IOException) {
        throw UncheckedIOException(e)
    }

private fun verifySnapshotInternal(name: String, value: String) {
    val resource = File("src/test/resources", "$name.snapshot")
    val snapshotExists = resource.exists()
    val explicitRegenerate = System.getProperty(REGENERATE_SNAPSHOTS)?.toBoolean() ?: false

    if (!snapshotExists || explicitRegenerate) {
        if (snapshotExists) {
            val existingValue = resource.readText()
            if (existingValue == value) {
                // Existing snapshot OK.
                return
            }

            println("Snapshot for [$name] not matching - regenerating")
        } else {
            println("Initial snapshot for [$name] created")
        }

        resource.writeText(value)
        return
    }

    val existingValue = resource.readText()
    try {
        assertEquals("Snapshot [$name] should match", existingValue, value)
    } catch (e: ComparisonFailure) {
        // Make this verbose so the user more likely finds it in the log.
        println("#####################################################################")
        println()
        println()
        println("    Snapshot for [$name] failed - recreate by setting system property $REGENERATE_SNAPSHOTS to true")
        println("    Example: mvn test -DREGENERATE_SNAPSHOTS=true")
        println()
        println()
        println("#####################################################################")
        throw e
    }
}
