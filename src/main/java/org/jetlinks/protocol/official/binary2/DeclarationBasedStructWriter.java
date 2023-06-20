package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2023/6/18 22:28
 */
public class DeclarationBasedStructWriter implements StructWriter {

    private StructDeclaration   structDcl;

    private List<FieldWriter>   fieldWriters;

    public DeclarationBasedStructWriter(StructDeclaration structDcl) {
        this.structDcl = structDcl;

        this.fieldWriters = new ArrayList<>();
        for (FieldDeclaration fieldDcl : structDcl.fields()) {
            this.fieldWriters.add(new DeclarationBasedFieldWriter(fieldDcl));
        }
    }

    @Override
    public ByteBuf write(StructInstance instance) {
        return null;
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }
}
