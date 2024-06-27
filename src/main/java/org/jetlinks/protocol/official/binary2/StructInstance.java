package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

/**
 * 结构体实例
 *
 * @author v-lizy81
 * @date 2023/6/16 21:38
 */
public interface StructInstance {

    @NotNull
    StructDeclaration    getDeclaration();

    Iterable<FieldInstance> filedInstances();

    FieldInstance getFieldInstance(StructFieldDeclaration fieldDcl);

    void addFieldInstance(FieldInstance inst);

    default void addFieldInstance(Iterable<FieldInstance> instances) {
        if (instances == null) return ;

        for (FieldInstance fInst : instances) {
            addFieldInstance(fInst);
        }
    }

    void addFieldInstance(String fieldCode, Object value);
}
