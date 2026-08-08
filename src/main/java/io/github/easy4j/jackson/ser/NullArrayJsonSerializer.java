package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * JSON serializer that emits an empty array whenever the input value is
 * {@code null}.
 *
 * <p>Installed as the {@code nullSerializer} for bean properties whose
 * declared type is an array or a {@link java.util.Collection} subtype (see
 * {@link MyBeanSerializerModifier#isArrayType(Class)}). Producing {@code []}
 * instead of {@code null} avoids null-pointer exceptions in client code that
 * blindly iterates over the received value.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see ValueSerializer
 * @see NullObjectJsonSerializer
 */
public class NullArrayJsonSerializer extends ValueSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullArrayJsonSerializer INSTANCE = new NullArrayJsonSerializer();

    /**
     * Serialize the supplied value, writing {@code []} when the value is
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
            jsonGenerator.writeStartArray();
            jsonGenerator.writeEndArray();
        }
    }

}
