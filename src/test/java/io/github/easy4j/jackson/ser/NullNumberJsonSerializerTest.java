package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
 */
class NullNumberJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullNumberJsonSerializer.INSTANCE, NullNumberJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullNumberJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitZeroForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullNumberJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("0", writer.toString());
    }
}
