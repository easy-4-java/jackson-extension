package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.util.Objects;

/**
 * JSON serializer that emits an explicit JSON {@code null} token for null
 * inputs.
 *
 * <p>This is the default no-op null serializer used by Jackson. It is
 * provided as a bean so callers can install it explicitly (e.g. inside a
 * custom {@code BeanSerializerModifier}) and so the project's null-handling
 * toolkit offers a complete, symmetrical set of null serializers.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @see JsonSerializer
 * @see NullStringJsonSerializer
 * @see NullArrayJsonSerializer
 */
public class NullJsonSerializer extends JsonSerializer<Object> {

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
     * @throws IOException propagated from the underlying generator.
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeNull();
        }
    }

}
