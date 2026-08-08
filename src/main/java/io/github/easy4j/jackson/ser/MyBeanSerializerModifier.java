package io.github.easy4j.jackson.ser;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * {@link ValueSerializerModifier} that installs the project's family of
 * null serializers on a per-property basis.
 *
 * <p>The modifier is the configuration hub that wires the following
 * transformations on null property values:</p>
 *
 * <ul>
 *     <li>arrays and {@link Collection} subtypes &rarr; {@code []}
 *         (see {@link NullArrayJsonSerializer});</li>
 *     <li>numeric ({@link Number}) subtypes &rarr; {@code 0}
 *         (see {@link NullNumberJsonSerializer});</li>
 *     <li>boolean ({@link Boolean}) subtypes &rarr; {@code false}
 *         (see {@link NullBooleanJsonSerializer});</li>
 *     <li>strings ({@link CharSequence} and {@link Character}) &rarr; {@code ""}
 *         (see {@link NullStringJsonSerializer});</li>
 *     <li>date/time types ({@link Date}, {@link java.sql.Date},
 *         {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime})
 *         &rarr; {@code ""} (see {@link NullDateJsonSerializer});</li>
 *     <li>object-typed properties ({@link Map}, POJOs and enums) &rarr; {@code {}}
 *         (see {@link NullObjectJsonSerializer}).</li>
 * </ul>
 *
 * <p>Each rule can be enabled or disabled individually through the
 * six-argument constructor. The default no-arg constructor enables array,
 * string, date and object rules and disables the number/boolean rules, which
 * matches the historical behaviour of the project.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see ValueSerializerModifier
 * @see NullArrayJsonSerializer
 * @see NullStringJsonSerializer
 * @see NullNumberJsonSerializer
 * @see NullBooleanJsonSerializer
 * @see NullDateJsonSerializer
 * @see NullObjectJsonSerializer
 */
public class MyBeanSerializerModifier extends ValueSerializerModifier {

    /** Whether arrays and {@link Collection} subtypes are rewritten as {@code []}. */
    private final boolean defaultNullArraySerializer;

    /** Whether {@link Number} subtypes are rewritten as {@code 0}. */
    private final boolean defaultNullNumberSerializer;

    /** Whether strings and {@link Character} are rewritten as {@code ""}. */
    private final boolean defaultNullStringSerializer;

    /** Whether date/time types are rewritten as {@code ""}. */
    private final boolean defaultNullDateSerializer;

    /** Whether {@link Boolean} properties are rewritten as {@code false}. */
    private final boolean defaultNullBooleanSerializer;

    /** Whether object-typed properties are rewritten as {@code {}}. */
    private final boolean defaultNullJsonObjectSerializer;

    /**
     * Convenience constructor that enables the same flags as the legacy
     * implementation: array, string, date and object rules on; number and
     * boolean rules off.
     */
    public MyBeanSerializerModifier() {
        this(true, false, true, true, false, true);
    }

    /**
     * Fully specified constructor exposing every individual rule flag.
     *
     * @param defaultNullArraySerializer      enable {@code []} for array/collection nulls.
     * @param defaultNullNumberSerializer     enable {@code 0} for numeric nulls.
     * @param defaultNullStringSerializer     enable {@code ""} for string nulls.
     * @param defaultNullDateSerializer       enable {@code ""} for date/time nulls.
     * @param defaultNullBooleanSerializer    enable {@code false} for boolean nulls.
     * @param defaultNullJsonObjectSerializer enable {@code {}} for object nulls.
     */
    public MyBeanSerializerModifier(boolean defaultNullArraySerializer,
                                    boolean defaultNullNumberSerializer,
                                    boolean defaultNullStringSerializer,
                                    boolean defaultNullDateSerializer,
                                    boolean defaultNullBooleanSerializer,
                                    boolean defaultNullJsonObjectSerializer) {
        this.defaultNullArraySerializer = defaultNullArraySerializer;
        this.defaultNullNumberSerializer = defaultNullNumberSerializer;
        this.defaultNullStringSerializer = defaultNullStringSerializer;
        this.defaultNullDateSerializer = defaultNullDateSerializer;
        this.defaultNullBooleanSerializer = defaultNullBooleanSerializer;
        this.defaultNullJsonObjectSerializer = defaultNullJsonObjectSerializer;
    }

    /**
     * Apply the configured null-serializer rewrites to every property of
     * every serialised bean.
     *
     * <p>The supplied {@code beanProperties} list is mutated in place and
     * also returned to satisfy the {@link ValueSerializerModifier} contract.
     * Properties whose declared Java type does not match any enabled rule
     * are left untouched.</p>
     *
     * @param config        the active serialisation configuration.
     * @param beanDesc      supplier for the bean description; not used
     *                      directly because the modifier operates at the
     *                      property level.
     * @param beanProperties the properties whose null serializers should be
     *                      rewritten.
     * @return the (mutated) {@code beanProperties} list.
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription.Supplier beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // 1、循环所有的beanPropertyWriter
        for (BeanPropertyWriter writer : beanProperties) {
            // 2、当前属性的Java类型
            Class<?> rawClass = writer.getType().getRawClass();
            // 3、判断字段的类型，如果是array，list，set则注册nullSerializer
            if (defaultNullArraySerializer && this.isArrayType(rawClass)) {
                writer.assignNullSerializer(NullArrayJsonSerializer.INSTANCE);
            } else if (defaultNullNumberSerializer && this.isNumberType(rawClass)) {
                writer.assignNullSerializer(NullNumberJsonSerializer.INSTANCE);
            } else if (defaultNullBooleanSerializer && this.isBooleanType(rawClass)) {
                writer.assignNullSerializer(NullBooleanJsonSerializer.INSTANCE);
            } else if (defaultNullStringSerializer && this.isStringType(rawClass)) {
                writer.assignNullSerializer(NullStringJsonSerializer.INSTANCE);
            } else if (defaultNullDateSerializer && this.isDateType(rawClass)) {
                writer.assignNullSerializer(NullDateJsonSerializer.INSTANCE);
            } else if (defaultNullJsonObjectSerializer && this.isJsonObjectType(rawClass)) {
                writer.assignNullSerializer(NullObjectJsonSerializer.INSTANCE);
            }
        }
        return beanProperties;
    }

    /**
     * Determine whether {@code rawClass} represents a Java array or any
     * {@link Collection} subtype.
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as an array-like
     *         property.
     */
    protected boolean isArrayType(Class<?> rawClass) {
        return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);
    }

    /**
     * Determine whether {@code rawClass} represents a string-like type
     * ({@link CharSequence} or {@link Character}).
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as a string-like
     *         property.
     */
    protected boolean isStringType(Class<?> rawClass) {
        return CharSequence.class.isAssignableFrom(rawClass) || Character.class.isAssignableFrom(rawClass);
    }

    /**
     * Determine whether {@code rawClass} represents a date/time type.
     *
     * <p>Recognised types include {@link Date}, {@link java.sql.Date},
     * {@link LocalDate}, {@link LocalDateTime} and {@link LocalTime}.</p>
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as a date/time
     *         property.
     */
    protected boolean isDateType(Class<?> rawClass) {
        return Date.class.isAssignableFrom(rawClass) || java.sql.Date.class.isAssignableFrom(rawClass)
                || LocalDate.class.isAssignableFrom(rawClass)
                || LocalDateTime.class.isAssignableFrom(rawClass)
                || LocalTime.class.isAssignableFrom(rawClass);
    }

    /**
     * Determine whether {@code rawClass} represents any {@link Number}
     * subtype.
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as a numeric
     *         property.
     */
    protected boolean isNumberType(Class<?> rawClass) {
        return Number.class.isAssignableFrom(rawClass);
    }

    /**
     * Determine whether {@code rawClass} is exactly {@link Boolean} (boxed).
     * Primitive {@code boolean} is intentionally excluded.
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as a boolean
     *         property.
     */
    protected boolean isBooleanType(Class<?> rawClass) {
        return rawClass.equals(Boolean.class);
    }

    /**
     * Determine whether {@code rawClass} represents a generic object that
     * should serialise as {@code {}} when null.
     *
     * <p>This includes {@link Map} subtypes, any non-primitive POJO and
     * any enum constant.</p>
     *
     * @param rawClass the candidate Java type to inspect.
     * @return {@code true} when the type should be treated as an object
     *         property.
     */
    private boolean isJsonObjectType(Class<?> rawClass) {
        return Map.class.isAssignableFrom(rawClass) || (!rawClass.isPrimitive() && !rawClass.isEnum());
    }

}
