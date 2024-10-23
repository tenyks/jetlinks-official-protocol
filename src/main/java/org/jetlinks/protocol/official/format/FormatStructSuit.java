package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import me.tenyks.utils.JsonUtils;
import org.jetlinks.protocol.official.binary2.SimpleStructInstance;
import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.AbstractStructSuit;
import org.jetlinks.protocol.official.common.FeatureCodeExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 一套结构
 * @author v-lizy81
 * @date 2023/6/18 21:55
 */
public class FormatStructSuit extends AbstractStructSuit {

    private static final Logger log = LoggerFactory.getLogger(FormatStructSuit.class);

    private final FeatureCodeExtractor<JsonNode>  fcExtractor;

    private final Map<String, FormatStructReader>   idxByFcReaderMap;

    private final Map<String, FormatStructWriter>   idxByFcWriterMap;

    private FormatStructReader          defaultReader;

    public FormatStructSuit(String name, String version, String description,
                            FeatureCodeExtractor<JsonNode> featureCodeExtractor) {
        super(name, version, description);

        this.fcExtractor = featureCodeExtractor;
        this.idxByFcReaderMap = new HashMap<>();
        this.idxByFcWriterMap = new HashMap<>();
    }

    public void addStructDeclaration(StructDeclaration structDcl) {
        if (structDcl.isEnableDecode()) {
            idxByFcReaderMap.put(structDcl.getFeatureCode(), FormatStructReader.createInstance(structDcl));
        }
        if (structDcl.isEnableEncode()) {
            idxByFcWriterMap.put(structDcl.getFeatureCode(), FormatStructWriter.createInstance(structDcl));
        }
    }

    public void setDefaultACKStructDeclaration(StructDeclaration structDcl) {
        super.addStructDeclaration(structDcl);

        if (structDcl.isEnableDecode()) {
            this.defaultReader = FormatStructReader.createInstance(structDcl);
        }
    }

    public StructDeclaration    getStructDeclarationOfEncode(String featureCode) {
        FormatStructWriter writer = idxByFcWriterMap.get(featureCode);
        return (writer != null ? writer.getStructDeclaration() : null);
    }

    public StructDeclaration    getStructDeclarationOfDecode(String featureCode) {
        FormatStructReader reader = idxByFcReaderMap.get(featureCode);
        return (reader != null ? reader.getStructDeclaration() : null);
    }

    public StructInstance       createStructInstance(String featureCode) {
        FormatStructWriter dcl = idxByFcWriterMap.get(featureCode);
        return (dcl != null ? new SimpleStructInstance(dcl.getStructDeclaration()) : null);
    }

    public FeatureCodeExtractor<JsonNode> getFcExtractor() {
        return fcExtractor;
    }

    public StructInstance deserialize(String buf) {
        JsonNode payload = JsonUtils.parse(buf);

        String fc = fcExtractor.extract(payload);
        if (!fcExtractor.isValidFeatureCode(fc)) {
            log.warn("[FormatStructSuit]不支持的报文：featureCode={}", fc);
            return null;
        }

        FormatStructReader reader = idxByFcReaderMap.get(fc);
        if (reader == null) {
            if (defaultReader == null) {
                log.warn("[FormatStructSuit]缺少支持的Reader：featureCode={}", fc);
                return null;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("[FormatStructSuit]缺少支持的Reader使用默认Reader：featureCode={}", fc);
                }
                reader = defaultReader;
            }
        }

        return reader.read(payload);
    }

    public String   serialize(StructInstance structInst) {
        StructDeclaration structDcl = structInst.getDeclaration();
        if (structDcl == null) {
            throw new IllegalArgumentException("StructInst参数不完整：缺少结构声明数据");
        }

        FormatStructWriter writer = idxByFcWriterMap.get(structDcl.getFeatureCode());
        if (writer == null) {
            writer = FormatStructWriter.createInstance(structDcl);
            idxByFcWriterMap.put(structDcl.getFeatureCode(), writer);
        }

        JsonNode output = writer.write(structInst);

        return JsonUtils.toJsonStr(output);
    }

}
