package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeclarationBasedFieldReader implements FieldReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFieldReader.class);

    private StructFieldDeclaration fieldDcl;

    public DeclarationBasedFieldReader(StructFieldDeclaration fieldDcl) {
        this.fieldDcl = fieldDcl;
    }

    @Override
    public FieldInstance read(ByteBuf buf) {
        short offset = fieldDcl.getOffset();
        short size = fieldDcl.getSize();

        int ri = buf.readerIndex();
        if (offset >= 0) {
            buf.readerIndex(offset);
        }
        if (buf.readableBytes() < size) {
            log.error("[FieldReader]字段读取越界：fn={}, offset={}, size={}, buf.readableBytes={}",
                    fieldDcl.getCode(), offset, size, buf.readableBytes());

            buf.readerIndex(ri);
            return null;
        }

        Object val = fieldDcl.getDataType().read(buf, size);
        if (val == null) {
            val = fieldDcl.getDefaultValue();
        }

        return new SimpleFieldInstance(fieldDcl, offset, size, val);
    }

    public StructFieldDeclaration getFieldDeclaration() {
        return fieldDcl;
    }

    @Override
    public String toString() {
        return "DeclarationBasedFieldReader{" +
                "fieldDcl=" + fieldDcl +
                '}';
    }
}
