package io.github.easy4j.jackson.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Lenient {@link ValueDeserializer} that converts string-encoded numeric tokens
 * into {@link Long} values.
 *
 * <p>The deserializer is intentionally tolerant: blank or whitespace-only
 * inputs return {@code null} instead of throwing, and any
 * {@link NumberFormatException} raised while parsing is logged at the
 * {@code ERROR} level and swallowed, again producing {@code null}. This makes
 * it suitable for inbound JSON where clients occasionally send numeric values
 * that have been quoted (a common JavaScript convention) but never produce
 * an exception visible to the caller.</p>
 *
 * <p>Non-blank values are parsed through {@link BigDecimal#longValue()} which
 * silently truncates fractional input. For example {@code "123.99"} becomes
 * {@code 123L}. Use a different deserializer when strict parsing is required.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ValueDeserializer
 * @see BigDecimal
 */
@Slf4j
public class ToLongDeserializer extends ValueDeserializer<Long> {

    /**
     * Deserialize a JSON token into a {@link Long}.
     *
     * <p>Reads the textual representation of the current token, returns
     * {@code null} for blank input and returns {@code null} (after logging)
     * for tokens that cannot be parsed as a number.</p>
     *
     * @param jsonParser            the active JSON parser positioned on a textual token.
     * @param deserializationContext contextual information provided by Jackson;
     *                               not used by this implementation.
     * @return the parsed {@link Long} value, or {@code null} when the token is
     *         blank or unparsable.
     * @throws JacksonException propagated from the underlying parser when an
     *                          unrecoverable streaming error occurs.
     */
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws JacksonException {
        String value = jsonParser.getText();
        try {
            return StringUtils.isNotBlank(value) ? new BigDecimal(value).longValue() : null;
        } catch (NumberFormatException e) {
            log.error("Failed to parse long value from token '{}'", value, e);
            return null;
        }
    }

}
