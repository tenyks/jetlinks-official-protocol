package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * @author v-lizy81
 * @date 2023/6/20 23:27
 */
public class DeclarationBasedFieldWriter implements FieldWriter {

    private final StructFieldDeclaration fieldDcl;

    public DeclarationBasedFieldWriter(StructFieldDeclaration fieldDcl) {
        this.fieldDcl = fieldDcl;
    }

    @Override
    public short write(FieldInstance instance, ByteBuf outputBuf) {

        Object val = instance.getValue();
        if (val == null) val = fieldDcl.getDefaultValue();

        return fieldDcl.getDataType().write(outputBuf, val);
    }

    public StructFieldDeclaration getFieldDeclaration() {
        return fieldDcl;
    }
}
