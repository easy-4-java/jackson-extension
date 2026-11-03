package io.github.easy4j.jackson.ser;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * JSON serializer that emits an empty string whenever the input value is
 * {@code null}.
 *
 * <p>Designed to be installed as the {@code nullSerializer} of a Jackson
 * {@link com.fasterxml.jackson.databind.ser.BeanPropertyWriter} whose declared type
 * is {@link String} (or {@link CharSequence} more generally). Instead of
 * producing a JSON {@code null} token, this serializer writes {@code ""},
 * which simplifies downstream consumers that prefer uniform string fields.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @see JsonSerializer
 * @see NullObjectJsonSerializer
 */
public class NullStringJsonSerializer extends JsonSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullStringJsonSerializer INSTANCE = new NullStringJsonSerializer();

    /**
     * Serialize the supplied value, writing {@code ""} when the value is
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
            jsonGenerator.writeString(StringUtils.EMPTY);
        }
    }

}
