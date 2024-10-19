package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.apache.commons.collections.CollectionUtils;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedStructReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DeclarationBasedBinaryStructReader extends AbstractDeclarationBasedStructReader implements BinaryStructReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedBinaryStructReader.class);

    public DeclarationBasedBinaryStructReader(StructDeclaration structDcl) {
        super(structDcl);
    }

    @Override
    public StructInstance read(ByteBuf buf) {
        StructInstance sInst = createNewStructInstance();

        for (StructPartReader partReader : getStructPartReaders()) {
            if (partReader instanceof NRepeatGroupReader) {
                NRepeatGroupReader fgReader = (NRepeatGroupReader) partReader;
                NRepeatGroupDeclaration fgDcl = fgReader.getDeclaration();

                DynamicAnchor dynamicAnchor = fgDcl.getDynamicAnchor();
                if (dynamicAnchor != null) dynamicAnchor.bind(sInst);

                fgReader.bind(sInst);

                List<FieldInstance> fInstList = fgReader.read(buf);
                if (CollectionUtils.isEmpty(fInstList)) {
                    log.error("[StructReader]字段组读取返回空判定字节流反序列化为失败：fieldGroup={}", fgDcl);
                    return null;
                }

                sInst.addFieldInstance(fInstList);
            } else {
                BinaryFieldReader fReader = (BinaryFieldReader) partReader;
                StructFieldDeclaration fDcl = fReader.getDeclaration();

                DynamicAnchor dynamicAnchor = fDcl.getDynamicAnchor();
                if (dynamicAnchor != null) dynamicAnchor.bind(sInst);

                DynamicSize dynamicSize = fDcl.getDynamicSize();
                if (dynamicSize != null) dynamicSize.bind(sInst);

                FieldInstance fInst = fReader.read(buf);
                if (fInst == null) {
                    log.error("[StructReader]字段读取返回空判定字节流反序列化为失败：field={}", fDcl);
                    return null;
                }

                sInst.addFieldInstance(fInst);
            }
        }

        return sInst;
    }

}
