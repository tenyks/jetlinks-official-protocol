package org.jetlinks.protocol.official.common;

import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public abstract class AbstractDeclarationBasedFieldReader {

    private final StructFieldDeclaration fieldDcl;

    public AbstractDeclarationBasedFieldReader(StructFieldDeclaration fieldDcl) {
        this.fieldDcl = fieldDcl;
    }

    public StructFieldDeclaration getDeclaration() {
        return fieldDcl;
    }


}
