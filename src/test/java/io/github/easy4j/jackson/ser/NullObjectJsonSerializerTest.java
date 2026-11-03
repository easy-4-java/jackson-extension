package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullObjectJsonSerializer}.
 *
 * <p>The serializer must emit an empty JSON object ({@code {}}) for null
 * inputs and remain a singleton. The tests verify both contracts.</p>
 *
 */
class NullObjectJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullObjectJsonSerializer.INSTANCE, NullObjectJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullObjectJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitEmptyObjectForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullObjectJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("{}", writer.toString());
    }
}
