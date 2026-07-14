package io.github.hiwepy.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * 处理数组集合类型的null值
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class NullArrayJsonSerializer extends ValueSerializer<Object> {

    public static final NullArrayJsonSerializer INSTANCE = new NullArrayJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeEndArray();
        }
    }

}