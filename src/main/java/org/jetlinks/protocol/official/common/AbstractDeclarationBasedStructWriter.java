package org.jetlinks.protocol.official.common;

import org.jetlinks.protocol.official.binary2.StructDeclaration;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public abstract class AbstractDeclarationBasedStructWriter {

    private final StructDeclaration     structDcl;

    //private final List<FieldWriter>   fieldWriters;

    public AbstractDeclarationBasedStructWriter(StructDeclaration structDcl) {
        this.structDcl = structDcl;

//        this.fieldWriters = new ArrayList<>();
//        for (StructFieldDeclaration fieldDcl : structDcl.fields()) {
//            this.fieldWriters.add(new DeclarationBasedFieldWriter(fieldDcl));
//        }
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }
}
