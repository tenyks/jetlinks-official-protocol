package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

public class DeclarationBasedFieldReader implements FieldReader {

    private FieldDeclaration fieldDcl;

    public DeclarationBasedFieldReader(FieldDeclaration fieldDcl) {
        this.fieldDcl = fieldDcl;
    }

    @Override
    public FieldInstance read(ByteBuf buf) {
        //TODO 补充边界情况处理
        int offset = fieldDcl.getOffset();
        int size = fieldDcl.getSize();
        if (offset > 0) {

        }
        

        return null;
    }

    public FieldDeclaration getFieldDeclaration() {
        return fieldDcl;
    }
}
