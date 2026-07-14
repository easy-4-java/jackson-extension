package io.github.hiwepy.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * 处理boolean类型的null值
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class NullBooleanJsonSerializer extends ValueSerializer<Object> {

    public static final NullBooleanJsonSerializer INSTANCE = new NullBooleanJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeBoolean(Boolean.FALSE);
        }
    }

}