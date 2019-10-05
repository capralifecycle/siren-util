package no.capraconsulting.siren.internal;

import no.capraconsulting.siren.Root;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public final class TestUtil {
    public static String getResource(final String name) {
        try (InputStream is = Objects.requireNonNull(TestUtil.class.getClassLoader().getResourceAsStream(name))) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int length;
            while ((length = is.read(buf)) != -1) {
                result.write(buf, 0, length);
            }
            return new String(result.toByteArray(), UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Create a sorted map of entries.
     *
     * Using a LinkedHashMap to ensure deterministic result.
     */
    @SafeVarargs
    public static Map<String, Object> mapOf(SimpleEntry<String, Object>... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (SimpleEntry<String, Object> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Create SimpleEntry with minimal code so the test can focus on its contents.
     */
    public static SimpleEntry<String, Object> entry(String key, Object value) {
        return new SimpleEntry<>(key, value);
    }

    /**
     * Verify Root object against snapshot and that it can be serialized to JSON, deserialized back
     * and that another round of serialization produces exactly the same JSON. This ensures we have
     * no data loss in serialization/deserialization.
     */
    public static void verifyRoot(String snapshotName, Root root) {
        String json = root.toJson();
        SnapshotTests.verifySnapshot(snapshotName, json);

        parseAndVerifyRootStrict(json);
    }

    /**
     * Verify a JSON value can be parsed to a Root object and serialized again without any
     * data loss. Return the parsed Root object.
     *
     * This performs strict check so that the input JSON must be formatted exactly as
     * the Root object will produce.
     */
    public static Root parseAndVerifyRootStrict(String json) {
        Root root = Root.fromJson(json);

        // We verify JSON exactly as we want to avoid any changes in the JSON structure
        // when reading into the model and writing back.
        assertEquals("No change when deserialize and serialize", json, root.toJson());

        return root;
    }

    /**
     * Verify a JSON value can be parsed to a Root object and serialized again without any
     * data loss. Return the parsed Root object.
     *
     * This performs relaxed check so that the JSON format only has to have the same data,
     * but can have different whitespace, map ordering and some data formatting.
     */
    public static Root parseAndVerifyRootRelaxed(String json) {
        Root root = Root.fromJson(json);

        try {
            JSONAssert.assertEquals(
                "Root object produces similar JSON",
                json,
                root.toJson(),
                true
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return root;
    }
}
