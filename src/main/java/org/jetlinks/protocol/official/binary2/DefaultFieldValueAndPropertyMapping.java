package org.jetlinks.protocol.official.binary2;

/**
 * 默认的值映射：不变
 * //TODO 根据物模型元信息，正则化值
 */
public class DefaultFieldValueAndPropertyMapping implements FieldValueAndPropertyMapping {

    @Override
    public Object toPropertyValue(FieldInstance fieldInst) {
        return fieldInst.getValue();
    }

    @Override
    public Object toFieldValue(FieldDeclaration fieldDcl, Object val) {
        return val;
    }
}
