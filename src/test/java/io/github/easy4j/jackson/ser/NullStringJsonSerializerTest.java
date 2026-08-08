package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;
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
 * @since 3.0.0
 */
class NullStringJsonSerializerTest {

    @Test
    void shouldExposeSingletonInstance() {
        assertSame(NullStringJsonSerializer.INSTANCE, NullStringJsonSerializer.INSTANCE);
    }

    @Test
    void shouldExtendValueSerializer() {
        NullStringJsonSerializer.INSTANCE.getClass().asSubclass(
                tools.jackson.databind.ValueSerializer.class);
    }

    @Test
    void shouldEmitEmptyStringForNullInput() throws JacksonException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JsonMapper.builder().build().createGenerator(writer);

        NullStringJsonSerializer.INSTANCE.serialize(null, generator, null);

        generator.flush();
        assertEquals("\"\"", writer.toString());
    }
}
