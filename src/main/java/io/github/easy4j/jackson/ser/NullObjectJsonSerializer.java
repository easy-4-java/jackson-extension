package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JSON serializer that emits an empty JSON object ({@code {}}) whenever an
 * object-typed property is {@code null}.
 *
 * <p>Installed as the {@code nullSerializer} for bean properties whose
 * declared type is a {@link Map}, a non-primitive POJO or an enum (see
 * {@link MyBeanSerializerModifier#isJsonObjectType(Class)}). Producing an
 * empty object keeps client code that always expects an object literal
 * simple.</p>
 *
 * <p>The unused {@link #EMPTY_MAP} field is retained as a placeholder for
 * future implementations that may want to inspect the streaming context to
 * write richer default values.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see ValueSerializer
 * @see NullArrayJsonSerializer
 */
public class NullObjectJsonSerializer extends ValueSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullObjectJsonSerializer INSTANCE = new NullObjectJsonSerializer();

    /**
     * Placeholder empty map retained for potential future use; never
     * serialised directly because this class writes an empty JSON object
     * via the streaming API.
     */
    @SuppressWarnings("unused")
    private static final Map<String, Object> EMPTY_MAP = new HashMap<>(1);

    /**
     * Serialize the supplied value, writing an empty JSON object when the
     * value is {@code null}.
     *
     * <p>Non-null values are intentionally ignored: this serializer is only
     * ever invoked by Jackson for null property values because it is wired
     * through {@code BeanPropertyWriter.assignNullSerializer}.</p>
     *
     * @param value             the value being serialized; expected to be
     *                          {@code null} when this serializer is invoked.
     * @param jsonGenerator     the active JSON generator.
     * @param serializerProvider contextual access to the surrounding
     *                          serialization state; not used.
     * @throws JacksonException propagated from the underlying generator.
     */
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
