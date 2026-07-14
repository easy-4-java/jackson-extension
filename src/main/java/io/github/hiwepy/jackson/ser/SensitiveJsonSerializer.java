package io.github.hiwepy.jackson.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import io.github.hiwepy.jackson.annotation.Sensitive;
import io.github.hiwepy.jackson.annotation.SensitiveStrategy;

import java.util.Objects;

/**
 * 数据脱敏 JSON 序列化器
 *
 * <p>根据 {@link Sensitive} 注解中指定的脱敏策略，对字符串字段进行脱敏序列化。
 * 支持上下文感知，可在运行时动态获取字段上的注解信息。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 1.0.8.RELEASE
 */
public class SensitiveJsonSerializer extends ValueSerializer<String> {
    /**
     * 脱敏策略，由注解 {@link Sensitive#strategy()} 动态设置
     */
    private final SensitiveStrategy strategy;

    public SensitiveJsonSerializer() {
        this(null);
    }

    private SensitiveJsonSerializer(SensitiveStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        if (Objects.isNull(strategy)) {
            gen.writeString(value);
            return;
        }
        gen.writeString(strategy.mask(value));
    }

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
