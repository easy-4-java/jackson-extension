package io.github.hiwepy.jackson;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import io.github.hiwepy.jackson.ser.MyBeanSerializerModifier;

import java.text.SimpleDateFormat;

/**
 * Jackson ObjectMapper 默认配置（纯 Java，无 Spring 依赖）。
 * <p>
 * 提供一个工厂方法构建主 {@link ObjectMapper}，行为与原 Spring
 * {@code Jackson2ObjectMapperBuilder} 配置等价。配置项的默认值通过构造参数传入。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class DefaultJacksonAutoConfiguration {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final boolean defaultNullArraySerializer;
    private final boolean defaultNullNumberSerializer;
    private final boolean defaultNullStringSerializer;
    private final boolean defaultNullDateSerializer;
    private final boolean defaultNullBooleanSerializer;
    private final boolean defaultNullJsonObjectSerializer;

    /**
     * 使用默认配置构造（与原 {@code @Value} 默认值一致）。
     */
    public DefaultJacksonAutoConfiguration() {
        this(true, false, true, true, false, true);
    }

    /**
     * 使用指定配置项构造。
     *
     * @param defaultNullArraySerializer      数组/集合 null 序列化为 {@code []}
     * @param defaultNullNumberSerializer     数值 null 序列化为 {@code 0}
     * @param defaultNullStringSerializer     字符串 null 序列化为 {@code ""}
     * @param defaultNullDateSerializer       日期 null 序列化为 {@code ""}
     * @param defaultNullBooleanSerializer    布尔 null 序列化为 {@code false}
     * @param defaultNullJsonObjectSerializer 对象/Map null 序列化为 {@code {}}
     */
    public DefaultJacksonAutoConfiguration(boolean defaultNullArraySerializer,
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
     * 构建并返回主 ObjectMapper。
     * <p>
     * 等价于原 Spring {@code Jackson2ObjectMapperBuilder} 的配置：
     * <ul>
     *   <li>统一日期格式 {@code yyyy-MM-dd HH:mm:ss}（{@code simpleDateFormat}）</li>
     *   <li>{@code failOnEmptyBeans = false}</li>
     *   <li>{@code failOnUnknownProperties = false}</li>
     *   <li>启用 {@code USE_GETTERS_AS_SETTERS} 与 {@code ALLOW_FINAL_FIELDS_AS_MUTATORS}</li>
     *   <li>非 XML 映射（{@code createXmlMapper = false}，即默认行为）</li>
     * </ul>
     * 此外注册了 {@link JavaTimeModule}（jdk8 时间序列化）与 {@link MyBeanSerializerModifier}（null 值兜底）。
     *
     * @return 配置好的 ObjectMapper 实例
     */
    public ObjectMapper jacksonObjectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        MyBeanSerializerModifier serializerModifier = new MyBeanSerializerModifier(defaultNullArraySerializer,
                defaultNullNumberSerializer, defaultNullStringSerializer,
                defaultNullDateSerializer, defaultNullBooleanSerializer, defaultNullJsonObjectSerializer);
        module.setSerializerModifier(serializerModifier);

        return JsonMapper.builder()
                .defaultDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN))
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .addModule(module)
                .build();
    }

}
