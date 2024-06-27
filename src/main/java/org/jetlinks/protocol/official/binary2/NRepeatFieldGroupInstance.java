package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface NRepeatFieldGroupInstance {

    @NotNull
    StructDeclaration       getDeclaration();

    Iterable<FieldInstance> filedInstances();

    FieldInstance           getFieldInstance(StructFieldDeclaration fieldDcl);

}
