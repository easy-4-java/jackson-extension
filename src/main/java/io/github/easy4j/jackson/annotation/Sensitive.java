package io.github.easy4j.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import tools.jackson.databind.annotation.JsonSerialize;
import io.github.easy4j.jackson.ser.SensitiveJsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field-level marker annotation that triggers value masking at JSON
 * serialization time.
 *
 * <p>Apply {@code @Sensitive} to any string-typed field that should never be
 * emitted in plain text &mdash; typically PII such as names, phone numbers,
 * identity cards, e-mail addresses and bank cards. The annotation is
 * meta-annotated with {@link JacksonAnnotationsInside} so that Jackson treats
 * it as a container for the embedded {@link JsonSerialize} annotation that
 * installs {@link SensitiveJsonSerializer} as the field serializer.</p>
 *
 * <p>The exact masking rules are supplied through the {@link #strategy()}
 * attribute; see {@link SensitiveStrategy} for the catalogue of supported
 * strategies.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SensitiveStrategy
 * @see SensitiveJsonSerializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive {

    /**
     * The masking strategy to apply to the annotated field. The strategy is
     * read at serialization time by {@link SensitiveJsonSerializer} via
     * {@link SensitiveJsonSerializer#createContextual}.
     *
     * @return the configured {@link SensitiveStrategy}; must not be {@code null}.
     */
    SensitiveStrategy strategy();
}
