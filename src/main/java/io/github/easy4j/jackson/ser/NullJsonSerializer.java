package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.util.Objects;

/**
 * JSON serializer that emits an explicit JSON {@code null} token for null
 * inputs.
 *
 * <p>This is the default no-op null serializer used by Jackson. It is
 * provided as a bean so callers can install it explicitly (e.g. inside a
 * custom {@code ValueSerializerModifier}) and so the project's null-handling
 * toolkit offers a complete, symmetrical set of null serializers.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ValueSerializer
 * @see NullStringJsonSerializer
 * @see NullArrayJsonSerializer
 */
public class NullJsonSerializer extends ValueSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullJsonSerializer INSTANCE = new NullJsonSerializer();

    /**
     * Serialize the supplied value, writing a JSON {@code null} literal
     * whenever the value is {@code null}.
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
            jsonGenerator.writeNull();
        }
    }

}
