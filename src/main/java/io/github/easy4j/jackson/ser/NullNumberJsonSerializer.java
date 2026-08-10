package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * JSON serializer that emits numeric zero ({@code 0}) whenever a numeric
 * property is {@code null}.
 *
 * <p>Installed as the {@code nullSerializer} for bean properties whose
 * declared type is a {@link Number} subtype (see
 * {@link MyBeanSerializerModifier#isNumberType(Class)}). Producing a numeric
 * literal avoids {@link NullPointerException}s in client arithmetic.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ValueSerializer
 * @see NullBooleanJsonSerializer
 */
public class NullNumberJsonSerializer extends ValueSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullNumberJsonSerializer INSTANCE = new NullNumberJsonSerializer();

    /**
     * Serialize the supplied value, writing {@code 0} when the value is
     * {@code null}.
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
            jsonGenerator.writeNumber(0);
        }
    }

}
