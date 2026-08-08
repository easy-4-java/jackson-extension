package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
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
 * @since 3.0.0
 */
class NullObjectJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullObjectJsonSerializer.INSTANCE, NullObjectJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullObjectJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitEmptyObjectForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullObjectJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("{}", writer.toString());
    }
}
