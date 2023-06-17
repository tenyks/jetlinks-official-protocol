package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nullable;

/**
 * 结构体实例
 *
 * @author v-lizy81
 * @date 2023/6/16 21:38
 */
public interface StructInstance {

    @Nullable
    StructDeclaration    getDeclaration();

    FieldInstance getFieldInstance(FieldDeclaration fieldDcl);

    void addFieldInstance(FieldInstance inst);

}
