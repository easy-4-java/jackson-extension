package io.github.easy4j.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import io.github.easy4j.jackson.annotation.SensitiveStrategy;
import io.github.easy4j.jackson.ser.MyBeanSerializerModifier;
import io.github.easy4j.jackson.ser.NullJsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JacksonExtensionBoundaryTest {

    @Test
    void shouldSerializeEveryEnabledNullDefaultAndPreserveDisabledDefaults() throws Exception {
        ObjectMapper enabled = mapper(new MyBeanSerializerModifier(true, true, true, true, true, true));
        String json = enabled.writeValueAsString(new NullPayload());
        assertEquals("{\"array\":[],\"number\":0,\"flag\":false,\"date\":\"\",\"map\":{},\"text\":\"\",\"other\":{}}", json);

        ObjectMapper disabled = mapper(new MyBeanSerializerModifier(false, false, false, false, false, false));
        assertEquals("{\"array\":null,\"number\":null,\"flag\":null,\"date\":null,\"map\":null,\"text\":null,\"other\":null}",
                disabled.writeValueAsString(new NullPayload()));
    }

    @Test
    void shouldHandleInvalidLongAndMaskingEdgeCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertNull(mapper.readValue("{\"total\":\"invalid\"}", JacksonExtensionContractTest.Order.class).total);
        assertEquals("", SensitiveStrategy.EMAIL.mask(""));
        assertEquals("a@b", SensitiveStrategy.EMAIL.mask("a@b"));
        assertEquals("ab", SensitiveStrategy.DEFAULT.mask("ab"));
    }

    @Test
    void shouldWriteLiteralNullWithDedicatedSerializer() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(output);
        NullJsonSerializer.INSTANCE.serialize(null, generator, null);
        generator.close();
        assertEquals("null", output.toString());
    }

    private static ObjectMapper mapper(MyBeanSerializerModifier modifier) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(BeanSerializerFactory.instance.withSerializerModifier(modifier));
        return mapper;
    }

    public static class NullPayload {
        public String[] array;
        public Integer number;
        public Boolean flag;
        public LocalDate date;
        public Map<String, String> map;
        public String text;
        public Object other;
    }
}
