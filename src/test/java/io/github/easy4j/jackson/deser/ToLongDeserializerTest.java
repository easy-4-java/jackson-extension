package io.github.easy4j.jackson.deser;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link ToLongDeserializer}.
 *
 * <p>The deserializer is exercised both through the standard
 * {@code ObjectMapper} machinery (round-trip style) and through the
 * lower-level {@link JsonParser} API so that the lenient blank-token
 * and number-format-exception branches are covered.</p>
 *
 * @since 3.0.0
 */
class ToLongDeserializerTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    private final JsonMapper mapperWithDeserializer;

    {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Long.class, new ToLongDeserializer());
        mapperWithDeserializer = JsonMapper.builder().addModule(module).build();
    }

    @Test
    void shouldDeserializeQuotedNumericString() throws Exception {
        Long result = mapper.readValue("\"42\"", Long.class);
        assertEquals(42L, result);
    }

    @Test
    void shouldDeserializePlainNumericToken() throws Exception {
        Long result = mapper.readValue("42", Long.class);
        assertEquals(42L, result);
    }

    @Test
    void shouldDeserializeLargeQuotedValue() throws Exception {
        Long result = mapper.readValue("\"9999999999\"", Long.class);
        assertEquals(9_999_999_999L, result);
    }

    @Test
    void shouldDeserializeNegativeQuotedValue() throws Exception {
        Long result = mapper.readValue("\"-17\"", Long.class);
        assertEquals(-17L, result);
    }

    @Test
    void shouldReturnNullForBlankString() throws Exception {
        Long result = mapperWithDeserializer.readValue("\"   \"", Long.class);
        assertNull(result);
    }

    @Test
    void shouldReturnNullForEmptyString() throws Exception {
        Long result = mapperWithDeserializer.readValue("\"\"", Long.class);
        assertNull(result);
    }

    @Test
    void shouldReturnNullForNonNumericInput() throws Exception {
        Long result = mapperWithDeserializer.readValue("\"not-a-number\"", Long.class);
        assertNull(result);
    }

    @Test
    void shouldReturnNullForWhitespaceOnlyInput() throws Exception {
        ToLongDeserializer deserializer = new ToLongDeserializer();

        JsonParser parser = mapper.createParser(" \" \" ");
        parser.nextToken();

        Long result = deserializer.deserialize(parser, null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullForMalformedToken() throws Exception {
        ToLongDeserializer deserializer = new ToLongDeserializer();

        JsonParser parser = mapper.createParser("\"abc\"");
        parser.nextToken();

        Long result = deserializer.deserialize(parser, null);
        assertNull(result);
    }
}
