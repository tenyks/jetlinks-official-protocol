package org.jetlinks.protocol.official.binary2;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.jetlinks.protocol.official.format.FormatFieldReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2024/6/27 22:40
 */
public class NRepeatDeclarationBasedFieldGroupReader implements NRepeatGroupReader {

    private static final Logger log = LoggerFactory.getLogger(NRepeatDeclarationBasedFieldGroupReader.class);

    private final NRepeatGroupDeclaration       declaration;

    private final List<BinaryFieldReader>       binaryFieldReaders;

    private final List<FormatFieldReader>       formatFieldReaders;

    private transient StructInstance            boundInstance;

    public NRepeatDeclarationBasedFieldGroupReader(StructDeclaration struct, NRepeatGroupDeclaration declaration) {
        this.declaration = declaration;
        this.binaryFieldReaders = new ArrayList<>();
        this.formatFieldReaders = new ArrayList<>();

        if (struct.isFormatStruct()) {
            for (StructFieldDeclaration fDcl : declaration.getIncludedFields()) {
                this.formatFieldReaders.add(StructPartReader.createFormat(fDcl));
            }
        }else {
            for (StructFieldDeclaration fDcl : declaration.getIncludedFields()) {
                this.binaryFieldReaders.add(StructPartReader.createBinary(fDcl));
            }
        }
    }

    @Override
    public NRepeatGroupDeclaration getDeclaration() {
        return declaration;
    }

    @Nullable
    @Override
    public List<FieldInstance> read(ByteBuf buf) {
        DynamicNRepeat nRepeat = declaration.getDynamicNRepeat();
        if (nRepeat == null) {
            log.warn("[NRepeatGroupReader]缺少DynamicNRepeat声明, buf={}", ByteUtils.toHexStr(buf));
            return null;
        }
        nRepeat.bind(boundInstance);
        short n = nRepeat.getNRepeat();

        List<FieldInstance> rst = new ArrayList<>();

        for (short i = 0; i < n; i++) {
            List<FieldInstance> grpRst = new ArrayList<>();
            for (BinaryFieldReader fReader : binaryFieldReaders) {
                StructFieldDeclaration fDcl = fReader.getDeclaration();

                DynamicAnchor dynamicAnchor = fDcl.getDynamicAnchor();
                if (dynamicAnchor != null) dynamicAnchor.bind(boundInstance, i);

                DynamicSize dynamicSize = fDcl.getDynamicSize();
                if (dynamicSize != null) dynamicSize.bind(boundInstance);

                FieldInstance fInst = fReader.read(buf);
                if (fInst == null) {
                    log.error("[NRepeatGroupReader]字段读取返回空判定字节流反序列化为失败：field={}", fDcl);
                    return null;
                }

                grpRst.add(fInst);
            }

            if (declaration.getInstancePostProcessor() != null) {
                grpRst = declaration.getInstancePostProcessor().apply((int)i, grpRst);
            }

            rst.addAll(grpRst);
        }

        return rst;
    }

    @Nullable
    @Override
    public List<FieldInstance> read(JsonNode input) {
        List<FieldInstance> rst = new ArrayList<>();

        //TODO 待实现

        return rst;
    }

    @Override
    public void bind(StructInstance structInst) {
        this.boundInstance = structInst;
    }
}
