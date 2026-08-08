package io.github.easy4j.jackson;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link JavaTimeModule}.
 *
 * <p>The module is registered against an {@code ObjectMapper} and the
 * resulting mapper is exercised through serialisation and deserialisation
 * round-trips for the three supported Java time types.</p>
 *
 * @since 3.0.0
 */
class JavaTimeModuleTest {

    @Test
    void shouldRegisterModuleWithoutError() {
        assertDoesNotThrow(() -> JsonMapper.builder().addModule(new JavaTimeModule()).build());
    }

    @Test
    void shouldFormatLocalDateTimeUsingCanonicalPattern() throws Exception {
        JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        LocalDateTime value = LocalDateTime.of(2024, 5, 17, 12, 34, 56);

        String json = mapper.writeValueAsString(value);
        assertEquals("\"2024-05-17 12:34:56\"", json);

        LocalDateTime parsed = mapper.readValue(json, LocalDateTime.class);
        assertEquals(value, parsed);
    }

    @Test
    void shouldFormatLocalDateUsingCanonicalPattern() throws Exception {
        JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        LocalDate value = LocalDate.of(2024, 5, 17);

        String json = mapper.writeValueAsString(value);
        assertEquals("\"2024-05-17\"", json);

        LocalDate parsed = mapper.readValue(json, LocalDate.class);
        assertEquals(value, parsed);
    }

    @Test
    void shouldFormatLocalTimeUsingCanonicalPattern() throws Exception {
        JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        LocalTime value = LocalTime.of(12, 34, 56);

        String json = mapper.writeValueAsString(value);
        assertEquals("\"12:34:56\"", json);

        LocalTime parsed = mapper.readValue(json, LocalTime.class);
        assertEquals(value, parsed);
    }
}
