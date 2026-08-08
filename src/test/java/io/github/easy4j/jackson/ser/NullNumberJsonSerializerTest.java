package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullNumberJsonSerializer}.
 *
 * <p>The serializer must emit the numeric literal {@code 0} for null
 * inputs and remain a singleton. The tests verify both contracts.</p>
 *
 * @since 3.0.0
 */
class NullNumberJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullNumberJsonSerializer.INSTANCE, NullNumberJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullNumberJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitZeroForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullNumberJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("0", writer.toString());
    }
}
