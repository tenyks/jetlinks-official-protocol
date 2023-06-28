package org.jetlinks.protocol.official.binary2;

/**
 * @author v-lizy81
 * @date 2023/6/27 23:39
 */
public interface FieldValueAndPropertyMapping {

    Object toPropertyValue(FieldInstance fieldInst);

    Object toFieldValue(FieldDeclaration fieldDcl, Object val);

}
