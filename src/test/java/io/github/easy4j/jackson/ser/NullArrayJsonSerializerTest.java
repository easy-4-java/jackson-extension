package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
 */
class NullArrayJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullArrayJsonSerializer.INSTANCE, NullArrayJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullArrayJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitEmptyArrayForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullArrayJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("[]", writer.toString());
    }
}
