package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.util.Objects;

/**
 * JSON serializer that emits JSON {@code false} whenever a boolean property
 * is {@code null}.
 *
 * <p>Installed as the {@code nullSerializer} for bean properties whose
 * declared type is exactly {@link Boolean} (see
 * {@link MyBeanSerializerModifier#isBooleanType(Class)}). Producing
 * {@code false} keeps three-valued logic out of client code paths that
 * expect a binary decision.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @see JsonSerializer
 * @see NullNumberJsonSerializer
 */
public class NullBooleanJsonSerializer extends JsonSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullBooleanJsonSerializer INSTANCE = new NullBooleanJsonSerializer();

    /**
     * Serialize the supplied value, writing {@code false} when the value is
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
     * @throws IOException propagated from the underlying generator.
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeBoolean(Boolean.FALSE);
        }
    }

}
