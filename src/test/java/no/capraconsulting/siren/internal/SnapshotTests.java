package no.capraconsulting.siren.internal;

import org.junit.ComparisonFailure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public final class SnapshotTests {

    private final static String REGENERATE_SNAPSHOTS = "REGENERATE_SNAPSHOTS";

    public static void verifySnapshot(String name, String value) {
        try {
            verifySnapshotInternal(name, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void verifySnapshotInternal(String name, String value) throws IOException {
        Path resource = getResourcePath(name);
        boolean snapshotExists = Files.exists(resource);
        boolean explicitRegenerate = Boolean.parseBoolean(System.getProperty(REGENERATE_SNAPSHOTS));

        if (!snapshotExists || explicitRegenerate) {
            if (snapshotExists) {
                String existingValue = new String(Files.readAllBytes(resource), UTF_8);
                if (existingValue.equals(value)) {
                    // Existing snapshot OK.
                    return;
                }

                System.out.println("Snapshot for [" + name + "] not matching - regenerating");
            } else {
                System.out.println("Initial snapshot for [" + name + "] created");
            }

            Files.write(resource, value.getBytes(UTF_8));
            return;
        }

        String existingValue = new String(Files.readAllBytes(resource), UTF_8);
        try {
            assertEquals("Snapshot [" + name + "] should match", existingValue, value);
        } catch (ComparisonFailure e) {
            // Make this verbose so the user more likely finds it in the log.
            System.err.println("#####################################################################");
            System.err.println();
            System.err.println();
            System.err.println("    Snapshot for [" + name + "] failed - recreate by setting system property " + REGENERATE_SNAPSHOTS + " to true");
            System.err.println("    Example: mvn test -DREGENERATE_SNAPSHOTS=true");
            System.err.println();
            System.err.println();
            System.err.println("#####################################################################");
            throw e;
        }
    }

    private static Path getResourcePath(String name) {
        return Paths.get("src/test/resources").resolve(name + ".snapshot");
    }
}
