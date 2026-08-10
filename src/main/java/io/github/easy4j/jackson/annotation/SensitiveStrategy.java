package io.github.easy4j.jackson.annotation;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * Catalogue of built-in data masking strategies used by {@link Sensitive}.
 *
 * <p>Each constant carries a {@link Function} that transforms an input
 * string into its masked representation. The {@link #mask(String)} entry
 * point applies the function while short-circuiting empty inputs.</p>
 *
 * <p>Most strategies are implemented through {@link #maskBetween(String, int, int)}
 * which keeps a fixed number of characters at the prefix and suffix of the
 * value while replacing everything in between with {@code '*'}. The
 * {@link #EMAIL} strategy uses {@link #maskEmail(String)} which preserves
 * the domain part of the address.</p>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see Sensitive
 */
public enum SensitiveStrategy {

    /**
     * Sentinel strategy that performs no masking; the input is returned
     * unchanged. Useful when the default masking logic must be bypassed.
     */
    NONE(Function.identity()),

    /**
     * Generic masking strategy that keeps the first and last character of the
     * value visible. Falls back to the original value when the visible
     * prefix/suffix span the entire input.
     */
    DEFAULT(value -> maskBetween(value, 1, 1)),

    /**
     * Username-style masking. Equivalent to {@link #DEFAULT} but semantically
     * named to make the intent explicit when used on user identifiers.
     */
    USERNAME(value -> maskBetween(value, 1, 1)),

    /**
     * Chinese-name masking: keeps the first character visible (surname) and
     * masks the remainder. Suitable for two- and three-character Chinese names.
     */
    CHINESE_NAME(value -> maskBetween(value, 1, 0)),

    /**
     * Mainland China resident identity-card masking: keeps the first three and
     * last four digits visible, masking the eight-digit birth-date segment.
     */
    ID_CARD(value -> maskBetween(value, 3, 4)),

    /**
     * Mainland China mobile phone number masking: keeps the first three and
     * last four digits visible (e.g. {@code 185****1653}).
     */
    PHONE(value -> maskBetween(value, 3, 4)),

    /**
     * Landline telephone masking: keeps the area code (two digits) and last
     * four digits visible.
     */
    FIXED_PHONE(value -> maskBetween(value, 2, 4)),

    /**
     * Postal address masking: keeps the first six characters visible and masks
     * the remainder. Suitable for province/city-level granularity.
     */
    ADDRESS(value -> maskBetween(value, 6, 0)),

    /**
     * E-mail address masking. Preserves the first local-part character and the
     * entire domain part (e.g. {@code r*****@qq.com}).
     */
    EMAIL(SensitiveStrategy::maskEmail),

    /**
     * Bank card masking: keeps the first four and last four digits visible.
     */
    BANK_CARD(value -> maskBetween(value, 4, 4)),

    /**
     * Bank of China CNAPS code masking: keeps the first four characters visible.
     */
    CNAPS_CODE(value -> maskBetween(value, 4, 0)),

    /**
     * Payment signing agreement number masking: keeps the first six and last
     * six characters visible.
     */
    PAY_SIGN_NO(value -> maskBetween(value, 6, 6)),

    ;

    /**
     * Concrete masking function that applies the strategy to a non-empty
     * value. Stored per-enum-constant and used by {@link #mask(String)}.
     */
    private final Function<String, String> desensitizer;

    /**
     * Construct a strategy with the supplied masking function.
     *
     * @param desensitizer the masking function to apply; never {@code null}.
     */
    SensitiveStrategy(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    /**
     * Apply this strategy to the supplied value.
     *
     * <p>Empty inputs are returned unchanged to preserve the round-trip
     * contract (a missing value should not be silently turned into
     * asterisks). Non-empty inputs are forwarded to the underlying
     * {@link Function}.</p>
     *
     * @param value the original string to mask; may be {@code null} or empty.
     * @return the masked value, or the original {@code value} when empty.
     */
    public String mask(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return desensitizer.apply(value);
    }

    /**
     * Mask an e-mail address while preserving the first local-part character
     * and the full domain.
     *
     * @param value a non-null e-mail address.
     * @return the masked e-mail address, or {@code value} unchanged when the
     *         local part has fewer than two characters.
     */
    private static String maskEmail(String value) {
        int atIndex = StringUtils.indexOf(value, '@');
        if (atIndex <= 1) {
            return value;
        }
        return StringUtils.left(value, 1)
                + StringUtils.repeat('*', atIndex - 1)
                + StringUtils.substring(value, atIndex);
    }

    /**
     * Mask the middle portion of a string while keeping a fixed prefix and
     * suffix visible.
     *
     * @param value         the original non-null string to mask.
     * @param visiblePrefix length of the unmasked prefix; must be non-negative.
     * @param visibleSuffix length of the unmasked suffix; must be non-negative.
     * @return the masked string, or {@code value} unchanged when the input
     *         length is shorter than or equal to the visible prefix plus
     *         visible suffix.
     */
    private static String maskBetween(String value, int visiblePrefix, int visibleSuffix) {
        int length = StringUtils.length(value);
        if (length <= visiblePrefix + visibleSuffix) {
            return value;
        }
        return StringUtils.left(value, visiblePrefix)
                + StringUtils.repeat('*', length - visiblePrefix - visibleSuffix)
                + StringUtils.right(value, visibleSuffix);
    }
}
