package io.github.hiwepy.jackson.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 将字符串转为Long
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
public class ToLongDeserializer extends ValueDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws JacksonException {
        String value = jsonParser.getText();
        try {
            return StringUtils.isNotBlank(value) ? new BigDecimal(value).longValue() : null;
        } catch (NumberFormatException e) {
            log.error("解析长整形错误", e);
            return null;
        }
    }

}
