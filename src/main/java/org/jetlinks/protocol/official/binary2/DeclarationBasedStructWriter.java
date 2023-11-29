package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
        for (StructFieldDeclaration fieldDcl : structDcl.fields()) {
            this.fieldWriters.add(new DeclarationBasedFieldWriter(fieldDcl));
        }
    }

    @Override
    public ByteBuf write(StructInstance instance) {
        ByteBuf rst = Unpooled.buffer();

        for (StructFieldDeclaration fDcl : structDcl.fields()) {
            FieldInstance fInst = instance.getFieldInstance(fDcl);

            BaseDataType dataType = fDcl.getDataType();
            if (fInst == null || fInst.getValue() == null) {
                dataType.write(rst, fDcl.getDefaultValue());
            }else {
                dataType.write(rst, fInst.getValue());
            }
        }

        //TODO 优化CRC字段
        CRCCalculator crcCal = structDcl.getCRCCalculator();
        if (crcCal != null) {
            int crcVal = structDcl.getCRCCalculator().apply(rst);
            rst.writeByte(crcVal);
        }

        return rst;
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }
}
