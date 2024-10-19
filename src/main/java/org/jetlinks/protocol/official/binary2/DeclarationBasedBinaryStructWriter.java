package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.tenyks.core.crc.CRCCalculator;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedStructWriter;

/**
 * @author v-lizy81
 * @date 2023/6/18 22:28
 */
public class DeclarationBasedBinaryStructWriter extends AbstractDeclarationBasedStructWriter implements BinaryStructWriter {

    public DeclarationBasedBinaryStructWriter(StructDeclaration structDcl) {
        super(structDcl);
    }

    @Override
    public ByteBuf write(StructInstance instance) {
        ByteBuf rst = Unpooled.buffer();

        for (StructFieldDeclaration fDcl : getStructDeclaration().fields()) {
            FieldInstance fInst = instance.getFieldInstance(fDcl);

            BaseDataType dataType = fDcl.getDataType();
            if (fInst == null || fInst.getValue() == null) {
                dataType.write(rst, fDcl.getDefaultValue());
            }else {
                dataType.write(rst, fInst.getValue());
            }
        }

        //TODO 优化CRC字段
        CRCCalculator crcCal = getStructDeclaration().getCRCCalculator();
        if (crcCal != null) {
            int crcVal = crcCal.apply(rst);
            if (crcCal.size() == 2) {
                rst.writeShort(crcVal);
            } else {
                rst.writeByte(crcVal);
            }
        }

        return rst;
    }


}
