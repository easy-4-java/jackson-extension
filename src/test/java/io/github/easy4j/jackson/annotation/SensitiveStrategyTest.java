package io.github.easy4j.jackson.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link SensitiveStrategy}.
 *
 * <p>Each strategy is exercised through {@link SensitiveStrategy#mask(String)}
 * with both the canonical sample inputs and edge cases (null, empty,
 * short strings that fall under the visible-prefix/suffix threshold).</p>
 *
 * @since 3.0.0
 */
class SensitiveStrategyTest {

    @Test
    void shouldReturnOriginalValueForNoneStrategy() {
        assertEquals("abcdef", SensitiveStrategy.NONE.mask("abcdef"));
    }

    @Test
    void shouldApplyDefaultMasking() {
        assertEquals("a****f", SensitiveStrategy.DEFAULT.mask("abcdef"));
    }

    @Test
    void shouldApplyUsernameMasking() {
        assertEquals("a****f", SensitiveStrategy.USERNAME.mask("abcdef"));
    }

    @Test
    void shouldMaskChineseNameKeepingFirstChar() {
        assertEquals("张**", SensitiveStrategy.CHINESE_NAME.mask("张三丰"));
    }

    @Test
    void shouldMaskIdCardKeepingFirstAndLastSegments() {
        assertEquals("130***********3232", SensitiveStrategy.ID_CARD.mask("130722199102323232"));
    }

    @Test
    void shouldMaskPhoneKeepingPrefixAndSuffix() {
        assertEquals("138****5678", SensitiveStrategy.PHONE.mask("13812345678"));
    }

    @Test
    void shouldMaskFixedPhoneKeepingAreaCodeAndSuffix() {
        assertEquals("01****5678", SensitiveStrategy.FIXED_PHONE.mask("0112345678"));
    }

    @Test
    void shouldMaskAddressKeepingPrefixOnly() {
        assertEquals("北京市海淀区***", SensitiveStrategy.ADDRESS.mask("北京市海淀区中关村"));
    }

    @Test
    void shouldMaskEmailKeepingFirstLocalCharAndDomain() {
        assertEquals("t*****@example.com", SensitiveStrategy.EMAIL.mask("tester@example.com"));
    }

    @Test
    void shouldMaskBankCardKeepingPrefixAndSuffix() {
        assertEquals("6222********7890", SensitiveStrategy.BANK_CARD.mask("6222021234567890"));
    }

    @Test
    void shouldMaskCnapsCodeKeepingPrefix() {
        assertEquals("1234********", SensitiveStrategy.CNAPS_CODE.mask("123456789012"));
    }

    @Test
    void shouldMaskPaySignNoKeepingPrefixAndSuffix() {
        assertEquals("190313******640590", SensitiveStrategy.PAY_SIGN_NO.mask("190313172733640590"));
    }

    @Test
    void shouldReturnNullInputUnchanged() {
        assertNull(SensitiveStrategy.NONE.mask(null));
        assertNull(SensitiveStrategy.PHONE.mask(null));
    }

    @Test
    void shouldReturnEmptyInputUnchanged() {
        assertEquals("", SensitiveStrategy.NONE.mask(""));
        assertEquals("", SensitiveStrategy.PHONE.mask(""));
    }

    @Test
    void shouldReturnShortStringUnchangedWhenMaskingHasNoEffect() {
        // USERNAME masks between(1, 1): if length <= 2 it falls back.
        assertEquals("ab", SensitiveStrategy.USERNAME.mask("ab"));
        // ID_CARD masks between(3, 4): if length <= 7 it falls back.
        assertEquals("123456", SensitiveStrategy.ID_CARD.mask("123456"));
        // CHINESE_NAME masks between(1, 0): if length <= 1 it falls back.
        assertEquals("x", SensitiveStrategy.CHINESE_NAME.mask("x"));
    }

    @Test
    void shouldReturnShortEmailUnchanged() {
        // Local part of size 0 or 1 is preserved untouched.
        assertEquals("a@b.com", SensitiveStrategy.EMAIL.mask("a@b.com"));
    }

    @Test
    void shouldReturnEmailWithoutAtSignUnchanged() {
        assertEquals("no-at-sign", SensitiveStrategy.EMAIL.mask("no-at-sign"));
    }

    @Test
    void shouldExposeEnumConstants() {
        assertSame(SensitiveStrategy.NONE, SensitiveStrategy.valueOf("NONE"));
        assertSame(SensitiveStrategy.PAY_SIGN_NO, SensitiveStrategy.valueOf("PAY_SIGN_NO"));
    }

    @Test
    void shouldEnumerateAllStrategies() {
        // Smoke test ensuring valueOf and values() cover the same constants.
        SensitiveStrategy[] values = SensitiveStrategy.values();
        for (SensitiveStrategy strategy : values) {
            assertSame(strategy, SensitiveStrategy.valueOf(strategy.name()));
        }
    }
}
