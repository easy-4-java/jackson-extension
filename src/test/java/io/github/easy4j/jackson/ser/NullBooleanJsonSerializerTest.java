package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
 */
class NullBooleanJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullBooleanJsonSerializer.INSTANCE, NullBooleanJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullBooleanJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitFalseForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullBooleanJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("false", writer.toString());
    }
}
