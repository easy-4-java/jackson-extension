package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
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
 * @since 3.0.0
 */
class NullJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullJsonSerializer.INSTANCE, NullJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitJsonNullForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("null", writer.toString());
    }
}
