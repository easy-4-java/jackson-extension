package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link NullStringJsonSerializer}.
 *
 * <p>The serializer is exposed as a singleton and never invoked for
 * non-null values by Jackson; the tests therefore focus on the singleton
 * contract, the type hierarchy and that wrapping a {@link JsonGenerator}
 * does not throw for null input.</p>
 *
 */
class NullStringJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullStringJsonSerializer.INSTANCE, NullStringJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendJsonSerializer() {
        NullStringJsonSerializer.INSTANCE.getClass().asSubclass(
                com.fasterxml.jackson.databind.JsonSerializer.class);
    }

    @Test
    void shouldEmitEmptyStringForNullInput() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().getFactory().createGenerator(writer);

        NullStringJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("\"\"", writer.toString());
    }
}
