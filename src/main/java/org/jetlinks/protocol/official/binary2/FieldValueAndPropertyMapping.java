package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @date 2023/6/27 23:39
 */
public interface FieldValueAndPropertyMapping {

    Object toPropertyValue(@Nullable MapperContext context, FieldInstance fieldInst);

    Object toFieldValue(@Nullable MapperContext context, StructFieldDeclaration fieldDcl, Object val);

}
