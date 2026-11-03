package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullJsonSerializer}.
 *
 * <p>The serializer must emit a JSON {@code null} literal for null inputs
 * and remain a singleton. The tests verify both contracts.</p>
 *
 */
class NullJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullJsonSerializer.INSTANCE, NullJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitJsonNullForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("null", writer.toString());
    }
}
