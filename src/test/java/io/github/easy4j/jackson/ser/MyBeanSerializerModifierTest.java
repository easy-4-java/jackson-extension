package io.github.easy4j.jackson.ser;

import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link MyBeanSerializerModifier}.
 *
 * <p>The modifier is installed on a {@link SimpleModule} and the resulting
 * {@code JsonMapper} is used to exercise the various null-rewriting rules
 * both with the default flags and with every individual flag toggled on
 * or off.</p>
 *
 * @since 3.0.0
 */
class MyBeanSerializerModifierTest {

    @Test
    void shouldApplyDefaultConstructor() {
        // The no-arg constructor must produce a fully wired modifier.
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier();
        assertEquals(true, isField(modifier, "defaultNullArraySerializer"));
        assertEquals(false, isField(modifier, "defaultNullNumberSerializer"));
        assertEquals(true, isField(modifier, "defaultNullStringSerializer"));
        assertEquals(true, isField(modifier, "defaultNullDateSerializer"));
        assertEquals(false, isField(modifier, "defaultNullBooleanSerializer"));
        assertEquals(true, isField(modifier, "defaultNullJsonObjectSerializer"));
    }

    @Test
    void shouldApplyAllFlagsConstructor() {
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier(
                false, false, false, false, false, false);
        for (String field : new String[]{
                "defaultNullArraySerializer",
                "defaultNullNumberSerializer",
                "defaultNullStringSerializer",
                "defaultNullDateSerializer",
                "defaultNullBooleanSerializer",
                "defaultNullJsonObjectSerializer"}) {
            assertEquals(false, isField(modifier, field));
        }
    }

    @Test
    void shouldEmitEmptyArrayForNullCollectionWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new ArrayBean());
        assertEquals("{\"strings\":[]}", json);
    }

    @Test
    void shouldEmitEmptyArrayForNullJavaArrayWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new JavaArrayBean());
        assertEquals("{\"values\":[]}", json);
    }

    @Test
    void shouldEmitEmptyStringForNullStringWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new StringBean());
        assertEquals("{\"text\":\"\"}", json);
    }

    @Test
    void shouldEmitEmptyStringForNullCharacterWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new CharacterBean());
        assertEquals("{\"value\":\"\"}", json);
    }

    @Test
    void shouldEmitEmptyStringForNullStringBuilderWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new StringBuilderBean());
        assertEquals("{\"text\":\"\"}", json);
    }

    @Test
    void shouldEmitEmptyStringForNullDateWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new DateBean());
        assertEquals("{\"date\":\"\"}", json);
    }

    @Test
    void shouldEmitEmptyObjectForNullObjectWithDefaultModifier() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new ObjectBean());
        assertEquals("{\"payload\":{}}", json);
    }

    @Test
    void shouldPreserveNullsWhenAllRulesAreDisabled() throws Exception {
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier(
                false, false, false, false, false, false);
        JsonMapper mapper = mapperWithModifier(modifier);
        String json = mapper.writeValueAsString(new MixedBean());
        assertTrue(json.contains("\"number\":null"));
        assertTrue(json.contains("\"flag\":null"));
        assertTrue(json.contains("\"strings\":null"));
        assertTrue(json.contains("\"text\":null"));
        assertTrue(json.contains("\"date\":null"));
        assertTrue(json.contains("\"payload\":null"));
    }

    @Test
    void shouldEmitZeroForNullNumberWhenRuleEnabled() throws Exception {
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier(
                false, true, false, false, false, false);
        JsonMapper mapper = mapperWithModifier(modifier);
        String json = mapper.writeValueAsString(new NumberBean());
        assertEquals("{\"count\":0}", json);
    }

    @Test
    void shouldEmitFalseForNullBooleanWhenRuleEnabled() throws Exception {
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier(
                false, false, false, false, true, false);
        JsonMapper mapper = mapperWithModifier(modifier);
        String json = mapper.writeValueAsString(new BooleanBean());
        assertEquals("{\"flag\":false}", json);
    }

    @Test
    void shouldEmitEmptyArrayForNullCollectionEvenWithoutDateAndObjectRules() throws Exception {
        MyBeanSerializerModifier modifier = new MyBeanSerializerModifier(
                true, false, false, false, false, false);
        JsonMapper mapper = mapperWithModifier(modifier);
        String json = mapper.writeValueAsString(new ArrayBean());
        assertEquals("{\"strings\":[]}", json);
    }

    @Test
    void shouldHandleAllSupportedDateTypes() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new DateVariantsBean());
        assertTrue(json.contains("\"sqlDate\":\"\""));
        assertTrue(json.contains("\"localDate\":\"\""));
        assertTrue(json.contains("\"localDateTime\":\"\""));
        assertTrue(json.contains("\"localTime\":\"\""));
    }

    @Test
    void shouldHandlePojoAndMapAsObjectTypes() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new MapBean());
        assertEquals("{\"values\":{}}", json);
    }

    @Test
    void shouldHandleEnumAsNullWhenEnumExcludedFromObjectRule() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new EnumBean());
        // isJsonObjectType excludes enums (!rawClass.isEnum()), so null enum
        // values are not rewritten and remain null.
        assertEquals("{\"kind\":null}", json);
    }

    @Test
    void shouldHandlePrimitiveFieldWithoutRewriting() throws Exception {
        JsonMapper mapper = mapperWithModifier(new MyBeanSerializerModifier());
        String json = mapper.writeValueAsString(new PrimitiveBean());
        assertEquals("{\"flag\":true}", json);
    }

    private static JsonMapper mapperWithModifier(MyBeanSerializerModifier modifier) {
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(modifier);
        return JsonMapper.builder().addModule(module).build();
    }

    private static boolean isField(Object target, String fieldName) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getBoolean(target);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    // ---------- Test payload beans ----------

    private static final class ArrayBean {
        public List<String> strings;
    }

    private static final class JavaArrayBean {
        public String[] values;
    }

    private static final class StringBean {
        public String text;
    }

    private static final class CharacterBean {
        public Character value;
    }

    private static final class StringBuilderBean {
        public StringBuilder text;
    }

    private static final class DateBean {
        public java.util.Date date;
    }

    private static final class DateVariantsBean {
        public Date sqlDate;
        public LocalDate localDate;
        public LocalDateTime localDateTime;
        public LocalTime localTime;
    }

    private static final class ObjectBean {
        public Nested payload;
    }

    private static final class MapBean {
        public Map<String, String> values;
    }

    private static final class NumberBean {
        public Integer count;
    }

    private static final class BooleanBean {
        public Boolean flag;
    }

    private static final class MixedBean {
        public Integer number;
        public Boolean flag;
        public List<String> strings;
        public String text;
        public java.util.Date date;
        public Nested payload;
    }

    private static final class Nested {
    }

    private static final class EnumBean {
        public Color kind;
    }

    private static final class PrimitiveBean {
        public boolean flag = true;
    }

    private enum Color {
        RED
    }
}
