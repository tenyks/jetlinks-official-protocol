package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一套结构
 * @author v-lizy81
 * @date 2023/6/18 21:55
 */
public class StructSuit {

    private static final Logger log = LoggerFactory.getLogger(StructSuit.class);

    private String  name;

    private String  version;

    private String  description;

    private FeatureCodeExtractor    fcExtractor;

    private CRCCalculator           crcCal;

    private Map<String, DeclarationBasedStructReader> idxByFcReaderMap;

    private Map<String, DeclarationBasedStructWriter>  idxByFcWriterMap;

    public StructSuit(String name, String version, String description,
                      FeatureCodeExtractor featureCodeExtractor,
                      CRCCalculator crcCal) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.fcExtractor = featureCodeExtractor;
        this.crcCal = crcCal;

        this.idxByFcReaderMap = new HashMap<>();
        this.idxByFcWriterMap = new HashMap<>();
    }

    public void addStructDeclaration(StructDeclaration structDcl) {
        idxByFcReaderMap.put(structDcl.getFeatureCode(), new DeclarationBasedStructReader(structDcl));
        idxByFcWriterMap.put(structDcl.getFeatureCode(), new DeclarationBasedStructWriter(structDcl));
    }

    public StructInstance deserialize(ByteBuf buf) {
        String fc = fcExtractor.extract(buf);
        StructReader reader = idxByFcReaderMap.get(fc);
        if (reader == null) {
            log.warn("[StructSuit]缺少FC={}的解码Reader.", fc);
            return null;
        }

        return reader.read(buf);
    }

    public ByteBuf serialize(StructInstance structInst) {
        StructDeclaration structDcl = structInst.getDeclaration();
        if (structDcl == null) {
            throw new IllegalArgumentException("structInst缺少结构声明数据");
        }

        StructWriter writer = idxByFcWriterMap.get(structDcl.getFeatureCode());
        if (writer == null) {
            DeclarationBasedStructWriter writerImpl = new DeclarationBasedStructWriter(structDcl);
            idxByFcWriterMap.put(structDcl.getFeatureCode(), writerImpl);

            writer = writerImpl;
        }

        return writer.write(structInst);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "StructSuit{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
