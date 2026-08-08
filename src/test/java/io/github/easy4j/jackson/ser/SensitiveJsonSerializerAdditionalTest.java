package io.github.easy4j.jackson.ser;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import io.github.easy4j.jackson.annotation.Sensitive;
import io.github.easy4j.jackson.annotation.SensitiveStrategy;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional unit tests for {@link SensitiveJsonSerializer}.
 *
 * <p>These tests complement
 * {@link SensitiveJsonSerializerTest} by exercising the contextual
 * resolution path directly and covering the edge cases that arise when
 * the annotation is applied to non-string properties or is absent
 * entirely.</p>
 *
 * @since 3.0.0
 */
class SensitiveJsonSerializerAdditionalTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void shouldWriteValueUnchangedWhenStrategyIsNull() throws Exception {
        SensitiveJsonSerializer serializer = new SensitiveJsonSerializer();

        StringWriter writer = new StringWriter();
        JsonGenerator generator = mapper.createGenerator(writer);

        serializer.serialize("plain-text", generator, null);
        generator.flush();

        assertEquals("\"plain-text\"", writer.toString());
    }

    @Test
    void shouldWriteNullThroughSerializerWithoutError() throws Exception {
        SensitiveJsonSerializer serializer = new SensitiveJsonSerializer();

        StringWriter writer = new StringWriter();
        JsonGenerator generator = mapper.createGenerator(writer);

        serializer.serialize(null, generator, null);
        generator.flush();

        assertEquals("null", writer.toString());
    }

    @Test
    void shouldReturnSameInstanceWhenCreateContextualReceivesNullProperty() {
        SensitiveJsonSerializer serializer = new SensitiveJsonSerializer();

        ValueSerializer<?> resolved = serializer.createContextual(null, null);

        assertSame(serializer, resolved);
    }

    @Test
    void shouldProduceDifferentInstancesForDifferentStrategies() {
        PhonePayload phonePayload = new PhonePayload("13812345678");
        EmailPayload emailPayload = new EmailPayload("tester@example.com");

        String phoneJson = serialize(phonePayload);
        String emailJson = serialize(emailPayload);

        assertEquals("{\"value\":\"138****5678\"}", phoneJson);
        assertEquals("{\"value\":\"t*****@example.com\"}", emailJson);
    }

    @Test
    void shouldMaskNullValueThroughAnnotatedField() throws Exception {
        NullPayload payload = new NullPayload();

        String json = mapper.writeValueAsString(payload);

        assertEquals("{\"value\":null}", json);
    }

    @Test
    void shouldNotInvokeAnnotationLogicForNonStringAnnotatedFields() throws Exception {
        NonStringPayload payload = new NonStringPayload();

        // Number fields must be serialised as numbers even when the
        // @Sensitive annotation is present (the contextual resolver
        // delegates back to the primary serializer).
        String json = mapper.writeValueAsString(payload);

        assertEquals("{\"value\":42}", json);
    }

    @Test
    void shouldApplyNoneStrategyIdenticallyToPlainString() throws Exception {
        NonePayload payload = new NonePayload();

        String json = mapper.writeValueAsString(payload);

        assertEquals("{\"value\":\"raw\"}", json);
    }

    private String serialize(Object payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static final class SensitivePayload {
        @Sensitive(strategy = SensitiveStrategy.PHONE)
        public String value;

        SensitivePayload(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final class PhonePayload {
        @Sensitive(strategy = SensitiveStrategy.PHONE)
        public String value;

        PhonePayload(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final class EmailPayload {
        @Sensitive(strategy = SensitiveStrategy.EMAIL)
        public String value;

        EmailPayload(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final class NullPayload {
        @Sensitive(strategy = SensitiveStrategy.PHONE)
        public String value;

        public String getValue() {
            return value;
        }
    }

    private static final class NonStringPayload {
        @Sensitive(strategy = SensitiveStrategy.PHONE)
        public Integer value = 42;

        public Integer getValue() {
            return value;
        }
    }

    private static final class NonePayload {
        @Sensitive(strategy = SensitiveStrategy.NONE)
        public String value = "raw";

        public String getValue() {
            return value;
        }
    }
}
