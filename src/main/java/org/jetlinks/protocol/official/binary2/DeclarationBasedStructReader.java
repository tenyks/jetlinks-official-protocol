package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DeclarationBasedStructReader implements StructReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedStructReader.class);

    private final StructDeclaration   structDcl;

    private final List<StructPartReader> partReaders;

    public DeclarationBasedStructReader(StructDeclaration structDcl) {
        this.structDcl = structDcl;

        this.partReaders = new ArrayList<>();
        for (StructFieldDeclaration fieldDcl : structDcl.fields()) {
            this.partReaders.add(new DeclarationBasedFieldReader(fieldDcl));
        }
    }

    @Override
    public StructInstance read(ByteBuf buf) {
        StructInstance sInst = new SimpleStructInstance(structDcl);

        for (StructPartReader partReader : partReaders) {
            if (partReader instanceof FieldReader) {
                FieldReader fReader = (FieldReader) partReader;
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
            } else {

            }








        }

        return sInst;
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }
}
