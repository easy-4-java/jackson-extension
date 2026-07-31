package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 处理日期类型的null值
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class NullDateJsonSerializer extends ValueSerializer<Object> {

    public static final NullDateJsonSerializer INSTANCE = new NullDateJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeString(StringUtils.EMPTY);
        }
    }

}
