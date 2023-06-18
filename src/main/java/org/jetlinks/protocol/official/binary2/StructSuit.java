package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一套结构
 * @author v-lizy81
 * @date 2023/6/18 21:55
 */
public class StructSuit {

    private FeatureCodeExtractor    fcExtractor;

    private Map<String, DeclarationBasedStructReader> idxByFcReaderMap;

    private Map<String, DeclarationBasedStructWriter>  idxByFcWriterMap;

    public StructSuit(FeatureCodeExtractor featureCodeExtractor, List<StructDeclaration> structDclList) {
        this.fcExtractor = featureCodeExtractor;

        this.idxByFcReaderMap = new HashMap<>();
        this.idxByFcWriterMap = new HashMap<>();
        structDclList.forEach(v -> {
            idxByFcReaderMap.put(v.getFeatureCode(), new DeclarationBasedStructReader(v));
            idxByFcWriterMap.put(v.getFeatureCode(), new DeclarationBasedStructWriter(v));
        });
    }

    public StructInstance deserialize(ByteBuf buf) {

        String fc = fcExtractor.extract(buf);
        StructReader reader = idxByFcReaderMap.get(fc);
        if (reader == null) {
            //TODO 补充错误处理
            return null;
        }

        return reader.read(buf);
    }

    public ByteBuf serialize(StructInstance structInst) {

        StructDeclaration structDcl = structInst.getDeclaration();
        if (structDcl == null) {
            //TODO 补充边界情况处理
            return null;
        }

        StructWriter writer = idxByFcWriterMap.get(structDcl.getFeatureCode());
        if (writer == null) {
            DeclarationBasedStructWriter writerImpl = new DeclarationBasedStructWriter(structDcl);
            idxByFcWriterMap.put(structDcl.getFeatureCode(), writerImpl);

            writer = writerImpl;
        }

        return writer.write(structInst);
    }



}
