package io.github.easy4j.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import io.github.easy4j.jackson.deser.ToLongDeserializer;
import io.github.easy4j.jackson.ser.MyBeanSerializerModifier;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JacksonExtensionContractTest {

    @Test
    void shouldRoundTripFixedJavaTimeFormats() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        TimePayload input = new TimePayload();
        input.dateTime = LocalDateTime.of(2026, 8, 5, 10, 30, 7);
        input.date = LocalDate.of(2026, 8, 5);
        input.time = LocalTime.of(10, 30, 7);

        String json = mapper.writeValueAsString(input);
        TimePayload restored = mapper.readValue(json, TimePayload.class);

        assertEquals("{\"dateTime\":\"2026-08-05 10:30:07\",\"date\":\"2026-08-05\",\"time\":\"10:30:07\"}", json);
        assertEquals(input.dateTime, restored.dateTime);
        assertEquals(input.date, restored.date);
        assertEquals(input.time, restored.time);
    }

    @Test
    void shouldSerializeConfiguredNullDefaults() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(BeanSerializerFactory.instance
                .withSerializerModifier(new MyBeanSerializerModifier()));

        assertEquals("{\"remark\":\"\",\"items\":[]}",
                mapper.writeValueAsString(new NullablePayload()));
    }

    @Test
    void shouldConvertDecimalStringsAndAcceptBlanksAsNullableLong() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals(123L, mapper.readValue("{\"total\":\"123.9\"}", Order.class).total);
        assertNull(mapper.readValue("{\"total\":\"  \"}", Order.class).total);
    }

    public static class TimePayload {
        public LocalDateTime dateTime;
        public LocalDate date;
        public LocalTime time;
    }

    public static class NullablePayload {
        public String remark;
        public List<String> items;
    }

    public static class Order {
        @JsonDeserialize(using = ToLongDeserializer.class)
        public Long total;
    }
}
