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
        short offset = fieldDcl.getOffset();
        short size = fieldDcl.getSize();

        int wi = buf.writerIndex();

        if (offset >= 0) {
            buf.readerIndex(offset);
        }

        Object val = fieldDcl.getDataType().read(buf, size);
        buf.writerIndex(wi);

        return new SimpleFieldInstance(fieldDcl, offset, size, val);
    }

    public FieldDeclaration getFieldDeclaration() {
        return fieldDcl;
    }
}
