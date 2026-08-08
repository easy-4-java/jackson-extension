package io.github.easy4j.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import io.github.easy4j.jackson.annotation.Sensitive;
import io.github.easy4j.jackson.annotation.SensitiveStrategy;

import java.util.Objects;

/**
 * Context-aware Jackson serializer that applies a {@link SensitiveStrategy}
 * to the values of fields annotated with {@link Sensitive}.
 *
 * <p>The serializer is registered on every property that carries the
 * {@link Sensitive} annotation through Jackson's
 * {@code @JsonSerialize(using = ...)} machinery. At runtime the actual
 * strategy is resolved lazily by
 * {@link #createContextual(SerializationContext, BeanProperty)} so that
 * serializers can be shared across annotations without leaking per-property
 * state.</p>
 *
 * <p>Behavioural rules:</p>
 * <ul>
 *     <li>No strategy is configured &rarr; the value is emitted unchanged.</li>
 *     <li>Strategy is configured &rarr; the value is forwarded to
 *         {@link SensitiveStrategy#mask(String)} and the result is emitted.</li>
 *     <li>Property is missing or not annotated &rarr; the serializer
 *         returns {@code this} so existing customisations are preserved.</li>
 *     <li>Annotated property is not a {@link String} &rarr; the primary
 *         serializer for the property type is returned to avoid masking
 *         non-string values.</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see Sensitive
 * @see SensitiveStrategy
 */
public class SensitiveJsonSerializer extends ValueSerializer<String> {
    /**
     * Strategy resolved from the {@link Sensitive} annotation. May be
     * {@code null} when this serializer is used outside an annotated
     * property context, in which case values are emitted unchanged.
     */
    private final SensitiveStrategy strategy;

    /**
     * Default constructor used by Jackson's reflection-based serializer
     * instantiation. The resulting serializer has no strategy; the actual
     * strategy is bound later by
     * {@link #createContextual(SerializationContext, BeanProperty)}.
     */
    public SensitiveJsonSerializer() {
        this(null);
    }

    /**
     * Private strategy-binding constructor used by
     * {@link #createContextual(SerializationContext, BeanProperty)} to
     * produce a per-property serializer carrying the resolved strategy.
     *
     * @param strategy the masking strategy bound to the current property.
     */
    private SensitiveJsonSerializer(SensitiveStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Write the masked value (or the unmodified value when no strategy has
     * been resolved) to the supplied generator.
     *
     * @param value       the value to mask; may be {@code null}.
     * @param gen         the active JSON generator.
     * @param serializers contextual information from Jackson; not used
     *                    directly.
     * @throws JacksonException propagated from the underlying generator.
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        if (Objects.isNull(strategy)) {
            gen.writeString(value);
            return;
        }
        gen.writeString(strategy.mask(value));
    }

    /**
     * Resolve the contextual {@link Sensitive} annotation and produce a
     * serializer that carries the configured {@link SensitiveStrategy}.
     *
     * <p>If the property is {@code null} or carries no {@link Sensitive}
     * annotation the current instance is returned unchanged. If the
     * annotation is present but the property is not a {@link String}, the
     * primary serializer for the property type is returned so non-string
     * fields remain untouched.</p>
     *
     * @param prov     the active serialisation context.
     * @param property the bean property being customised; may be
     *                 {@code null}.
     * @return a {@link SensitiveJsonSerializer} carrying the resolved
     *         strategy, {@code this} when no annotation is present, or the
     *         type's primary serializer when the annotation is applied to
     *         a non-string property.
     */
    @Override
    public ValueSerializer<?> createContextual(SerializationContext prov, BeanProperty property) {

        if (Objects.isNull(property)) {
            return this;
        }
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            return new SensitiveJsonSerializer(annotation.strategy());
        }
        return prov.findPrimaryPropertySerializer(property.getType(), property);

    }
}
