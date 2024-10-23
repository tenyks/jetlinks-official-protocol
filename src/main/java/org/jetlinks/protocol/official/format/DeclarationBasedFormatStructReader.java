package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections.CollectionUtils;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedStructReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatStructReader extends AbstractDeclarationBasedStructReader implements FormatStructReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFormatStructReader.class);

    public DeclarationBasedFormatStructReader(StructDeclaration structDcl) {
        super(structDcl);
    }

    @Override @Nullable
    public StructInstance read(JsonNode input) {
        StructInstance sInst = createNewStructInstance();

        for (StructPartReader partReader : getStructPartReaders()) {
            if (partReader instanceof NRepeatGroupReader) {
                NRepeatGroupReader fgReader = (NRepeatGroupReader) partReader;
                NRepeatGroupDeclaration fgDcl = fgReader.getDeclaration();

                fgReader.bind(sInst);

                List<FieldInstance> fInstList = fgReader.read(input);
                if (CollectionUtils.isEmpty(fInstList)) {
                    log.error("[FormatStructReader]字段组读取返回空判定字节流反序列化为失败：fieldGroup={}", fgDcl);
                    return null;
                }

                sInst.addFieldInstance(fInstList);
            } else {
                FormatFieldReader fReader = (FormatFieldReader) partReader;
                StructFieldDeclaration fDcl = fReader.getDeclaration();

                FieldInstance fInst = fReader.read(input);
                if (fInst == null) {
                    log.error("[FormatStructReader]字段读取返回空判定字节流反序列化为失败：field={}", fDcl);
                    return null;
                }

                sInst.addFieldInstance(fInst);
            }
        }

        return sInst;
    }
}
