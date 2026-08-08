package io.github.easy4j.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import tools.jackson.databind.annotation.JsonSerialize;
import io.github.easy4j.jackson.ser.SensitiveJsonSerializer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link Sensitive}.
 *
 * <p>The annotation is exercised through reflection to verify that the
 * runtime and target meta-annotations, as well as the strategy accessor,
 * behave as documented.</p>
 *
 * @since 3.0.0
 */
class SensitiveTest {

    @Test
    void shouldBeAnnotatedWithRuntimeRetention() {
        Retention retention = Sensitive.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldBeAnnotatedWithFieldTarget() {
        Target target = Sensitive.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.FIELD, target.value()[0]);
    }

    @Test
    void shouldBeAnnotatedWithJacksonAnnotationsInside() {
        assertNotNull(Sensitive.class.getAnnotation(JacksonAnnotationsInside.class));
    }

    @Test
    void shouldCarryJsonSerializeMetaAnnotation() {
        JsonSerialize jsonSerialize = Sensitive.class.getAnnotation(JsonSerialize.class);
        assertNotNull(jsonSerialize);
        assertSame(SensitiveJsonSerializer.class, jsonSerialize.using());
    }

    @Test
    void shouldReturnStrategyFromAnnotation() throws NoSuchFieldException {
        Field field = SampleBean.class.getDeclaredField("name");
        Sensitive annotation = field.getAnnotation(Sensitive.class);
        assertNotNull(annotation);
        assertEquals(SensitiveStrategy.CHINESE_NAME, annotation.strategy());
    }

    @Test
    void shouldReturnStrategyForEveryAnnotatedField() throws NoSuchFieldException {
        Field phone = SampleBean.class.getDeclaredField("phone");
        Field email = SampleBean.class.getDeclaredField("email");
        assertEquals(SensitiveStrategy.PHONE, phone.getAnnotation(Sensitive.class).strategy());
        assertEquals(SensitiveStrategy.EMAIL, email.getAnnotation(Sensitive.class).strategy());
    }

    @Test
    void shouldHonorCustomStrategyOnAnnotationUsage() throws NoSuchFieldException {
        Field bankCard = SampleBean.class.getDeclaredField("bankCard");
        Sensitive annotation = bankCard.getAnnotation(Sensitive.class);
        assertEquals(SensitiveStrategy.BANK_CARD, annotation.strategy());
    }

    @Test
    void shouldDeclareExactlyOneDeclaredMethod() {
        // Sensitive has exactly one abstract accessor: strategy().
        assertEquals(1, Sensitive.class.getDeclaredMethods().length);
        assertEquals("strategy", Sensitive.class.getDeclaredMethods()[0].getName());
    }

    @Test
    void shouldDeclareStrategyMethodReturnType() throws NoSuchMethodException {
        assertEquals(SensitiveStrategy.class,
                Sensitive.class.getDeclaredMethod("strategy").getReturnType());
    }

    /** Sample bean carrying the {@link Sensitive} annotation on multiple fields. */
    private static final class SampleBean {

        @Sensitive(strategy = SensitiveStrategy.CHINESE_NAME)
        private final String name = "张三丰";

        @Sensitive(strategy = SensitiveStrategy.PHONE)
        private final String phone = "13812345678";

        @Sensitive(strategy = SensitiveStrategy.BANK_CARD)
        private final String bankCard = "6222021234567890";

        @Sensitive(strategy = SensitiveStrategy.EMAIL)
        private final String email = "tester@example.com";
    }
}
