package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * JSON null 值序列化器
 *
 * <p>当序列化的值为 null 时，输出 null 值。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class NullJsonSerializer extends ValueSerializer<Object> {

    public static final NullJsonSerializer INSTANCE = new NullJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeNull();
        }
    }

}
