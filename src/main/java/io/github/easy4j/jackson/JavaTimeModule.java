package io.github.easy4j.jackson;

import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Default serialization module for the Java 8 date/time API.
 *
 * <p>This {@link SimpleModule} registers serializers and deserializers for
 * {@link LocalDate}, {@link LocalTime} and {@link LocalDateTime} using the
 * project-wide canonical patterns:
 *
 * <ul>
 *     <li>{@code yyyy-MM-dd HH:mm:ss} for {@link LocalDateTime}</li>
 *     <li>{@code yyyy-MM-dd} for {@link LocalDate}</li>
 *     <li>{@code HH:mm:ss} for {@link LocalTime}</li>
 * </ul>
 *
 * <p>Registering this module on an {@code ObjectMapper} (or using it indirectly
 * through {@code JsonMapper.builder().addModule(new JavaTimeModule()).build()})
 * ensures that all Java time values are formatted consistently and parsed back
 * to the same canonical representation regardless of the runtime locale.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SimpleModule
 * @see LocalDateTimeSerializer
 * @see LocalDateSerializer
 * @see LocalTimeSerializer
 */
public class JavaTimeModule extends SimpleModule {

    /** Canonical {@link DateTimeFormatter} pattern for {@link LocalDateTime} values. */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** Canonical {@link DateTimeFormatter} pattern for {@link LocalDate} values. */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /** Canonical {@link DateTimeFormatter} pattern for {@link LocalTime} values. */
    private static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * Construct a new module instance and register the canonical
     * serializers/deserializers for the supported Java time types.
     *
     * <p>The instance is fully populated by the time the constructor returns;
     * callers only need to register it with their {@code ObjectMapper}.</p>
     */
    public JavaTimeModule() {
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }
}
