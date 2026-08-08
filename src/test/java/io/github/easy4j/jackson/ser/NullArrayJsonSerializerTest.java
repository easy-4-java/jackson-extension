package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullArrayJsonSerializer}.
 *
 * <p>The serializer must emit an empty JSON array for null inputs and
 * remain a singleton. The tests verify both contracts.</p>
 *
 * @since 3.0.0
 */
class NullArrayJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullArrayJsonSerializer.INSTANCE, NullArrayJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullArrayJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitEmptyArrayForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullArrayJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("[]", writer.toString());
    }
}
