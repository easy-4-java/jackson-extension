package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullBooleanJsonSerializer}.
 *
 * <p>The serializer must emit the boolean literal {@code false} for null
 * inputs and remain a singleton. The tests verify both contracts.</p>
 *
 * @since 3.0.0
 */
class NullBooleanJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullBooleanJsonSerializer.INSTANCE, NullBooleanJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullBooleanJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitFalseForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullBooleanJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("false", writer.toString());
    }
}
