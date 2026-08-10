package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * JSON serializer that emits an empty string whenever a date/time property
 * is {@code null}.
 *
 * <p>Installed as the {@code nullSerializer} for bean properties whose
 * declared type is {@link java.util.Date}, {@link java.sql.Date},
 * {@link java.time.LocalDate}, {@link java.time.LocalDateTime} or
 * {@link java.time.LocalTime} (see {@link MyBeanSerializerModifier#isDateType(Class)}).
 * Producing {@code ""} instead of {@code null} keeps downstream parsers from
 * special-casing missing dates.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ValueSerializer
 * @see NullStringJsonSerializer
 */
public class NullDateJsonSerializer extends ValueSerializer<Object> {

    /** Reusable singleton instance. */
    public static final NullDateJsonSerializer INSTANCE = new NullDateJsonSerializer();

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
     * @throws JacksonException propagated from the underlying generator.
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializerProvider)
            throws JacksonException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeString(StringUtils.EMPTY);
        }
    }

}
