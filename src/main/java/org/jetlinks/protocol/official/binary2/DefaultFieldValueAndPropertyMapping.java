package org.jetlinks.protocol.official.binary2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * 默认的值映射：不变
 * //TODO 根据物模型元信息，正则化值
 */
public class DefaultFieldValueAndPropertyMapping implements FieldValueAndPropertyMapping {

    private static final Logger log = LoggerFactory.getLogger(DefaultFieldValueAndPropertyMapping.class);

    @Override
    public Object toPropertyValue(@Nullable MapperContext context, FieldInstance fieldInst) {
        if ("messageId".equals(fieldInst.getDeclaration().getCode())) {
            if (context == null) {
                return fieldInst.getValue();
            }

            Short shortMsgId = (Short)fieldInst.getValue();
            String longMsgId = context.getThingMessageId(shortMsgId);
            return (longMsgId != null ? longMsgId : shortMsgId);
        }

        return fieldInst.getValue();
    }

    @Override
    public Object toFieldValue(@Nullable MapperContext context, StructFieldDeclaration fieldDcl, Object val) {
//        log.debug("[Codec]{}={}", fieldDcl.getCode(), val);

        if ("messageId".equals(fieldDcl.getCode())) {
            if (context == null) return val;

            return context.acquireAndBindThingMessageId((String) val);
        }
        if ("action".equals(fieldDcl.getCode())) {
            if (val instanceof String) {
                return Integer.valueOf((String)val);
            }

            return val;
        }

        return val;
    }



}
