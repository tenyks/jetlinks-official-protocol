package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.utils.BytesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 一套结构
 * @author v-lizy81
 * @date 2023/6/18 21:55
 */
public class StructSuit {

    private static final Logger log = LoggerFactory.getLogger(StructSuit.class);

    private final String  name;

    private final String  version;

    private final String  description;

    private final FeatureCodeExtractor    fcExtractor;

    private EncodeSigner            signer;

    private final Map<String, DeclarationBasedStructReader> idxByFcReaderMap;

    private final Map<String, DeclarationBasedStructWriter>  idxByFcWriterMap;

    private final Map<String, StructDeclaration> idxByNameMap;

    private DeclarationBasedStructReader    defaultReader;

    public StructSuit(String name, String version, String description,
                      FeatureCodeExtractor featureCodeExtractor) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.fcExtractor = featureCodeExtractor;

        this.idxByFcReaderMap = new HashMap<>();
        this.idxByFcWriterMap = new HashMap<>();
        this.idxByNameMap = new HashMap<>();
    }

    public void addStructDeclaration(StructDeclaration structDcl) {
        idxByNameMap.put(structDcl.getName(), structDcl);

        if (structDcl.isEnableDecode()) {
            idxByFcReaderMap.put(structDcl.getFeatureCode(), new DeclarationBasedStructReader(structDcl));
        }
        if (structDcl.isEnableEncode()) {
            idxByFcWriterMap.put(structDcl.getFeatureCode(), new DeclarationBasedStructWriter(structDcl));
        }
    }

    public void setDefaultACKStructDeclaration(StructDeclaration structDcl) {
        idxByNameMap.put(structDcl.getName(), structDcl);

        if (structDcl.isEnableDecode()) {
            this.defaultReader = new DeclarationBasedStructReader(structDcl);
        }
    }

    public StructDeclaration    getStructDeclarationOfEncode(String featureCode) {
        DeclarationBasedStructWriter writer = idxByFcWriterMap.get(featureCode);
        return (writer != null ? writer.getStructDeclaration() : null);
    }

    public StructDeclaration    getStructDeclarationOfDecode(String featureCode) {
        DeclarationBasedStructReader reader = idxByFcReaderMap.get(featureCode);
        return (reader != null ? reader.getStructDeclaration() : null);
    }

    public StructDeclaration getStructDeclaration(String name) {
        return idxByNameMap.get(name);
    }

    public Iterable<StructDeclaration>  structDeclarations() {
        return idxByNameMap.values();
    }

    public StructInstance createStructInstance(String featureCode) {
        DeclarationBasedStructWriter dcl = idxByFcWriterMap.get(featureCode);
        return (dcl != null ? new SimpleStructInstance(dcl.getStructDeclaration()) : null);
    }

    public StructInstance deserialize(ByteBuf buf) throws DecoderException {
        buf.readerIndex(0);
        String fc = fcExtractor.extract(buf);
        if (!fcExtractor.isValidFeatureCode(fc)) {
            buf.readerIndex(0);
            if (!fcExtractor.isDoubleHex(buf)) {
                log.warn("[StructSuit]不支持的字节流：featureCode={}", fc);
                return null;
            }

            buf.readerIndex(0);
            buf = BytesUtils.decodeHex(buf, 0, buf.readableBytes());

            buf.readerIndex(0);
            fc = fcExtractor.extract(buf);
            if (!fcExtractor.isValidFeatureCode(fc)) {
                log.warn("[StructSuit]不支持的字节流：featureCode={}", fc);
                return null;
            }
        }

        StructReader reader = idxByFcReaderMap.get(fc);
        if (reader == null) {
            if (defaultReader == null) {
                log.warn("[StructSuit]缺少支持的Reader：featureCode={}", fc);
                return null;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("[StructSuit]缺少支持的Reader使用默认Reader：featureCode={}", fc);
                }
                reader = defaultReader;
            }
        }

        //TODO 增加CRC检查

        return reader.read(buf);
    }

    public ByteBuf serialize(StructInstance structInst) {
        StructDeclaration structDcl = structInst.getDeclaration();
        if (structDcl == null) {
            throw new IllegalArgumentException("StructInst参数不完整：缺少结构声明数据");
        }

        StructWriter writer = idxByFcWriterMap.get(structDcl.getFeatureCode());
        if (writer == null) {
            DeclarationBasedStructWriter writerImpl = new DeclarationBasedStructWriter(structDcl);
            idxByFcWriterMap.put(structDcl.getFeatureCode(), writerImpl);

            writer = writerImpl;
        }

        return writer.write(structInst);
    }

    public EncodeSigner getSigner() {
        return signer;
    }

    public StructSuit setSigner(EncodeSigner signer) {
        this.signer = signer;
        return this;
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
