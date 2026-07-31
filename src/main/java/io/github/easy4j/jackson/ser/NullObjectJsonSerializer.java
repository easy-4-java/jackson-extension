package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 处理实体对象类型的null值
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class NullObjectJsonSerializer extends ValueSerializer<Object> {

    public static final NullObjectJsonSerializer INSTANCE = new NullObjectJsonSerializer();
    private static final Map<String, Object> EMPTY_MAP = new HashMap<>(1);

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeEndObject();
        }
        //JsonStreamContext outputContext = jsonGenerator.getOutputContext();
        //Object currentValue = outputContext.getCurrentValue();// 这里可以获取到被序列化的对象
        //String currentName = outputContext.getCurrentName(); // 这里获取了序列化的属性
        // 一个被序列化的对象找到了，这个当前序列化的属性也找到了，所以如果借用反射方式可以获取当前的类型
        //if (Objects.nonNull(currentValue)) {
        //	Field findField = ReflectionUtils.findField(currentValue.getClass(), currentName);
        //	Class<?> filedType = findField.getType(); // 获取字段的类型 // 这里开始写入数据
        //jsonGenerator.writeObject("");
        //}
        //jsonGenerator.writeObject(EMPTY_MAP);
    }

}
