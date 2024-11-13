package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedStructWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatStructWriter extends AbstractDeclarationBasedStructWriter implements FormatStructWriter {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFormatStructWriter.class);

    private final Map<StructFieldDeclaration, FormatFieldWriter> fieldWriters;

    public DeclarationBasedFormatStructWriter(StructDeclaration structDcl) {
        super(structDcl);

        this.fieldWriters = new HashMap<>();
        for (StructFieldDeclaration fieldDcl : structDcl.fields()) {
            fieldWriters.put(fieldDcl, FormatFieldWriter.create(fieldDcl));
        }
    }

    @Override
    public JsonNode write(StructInstance instance) {
        ObjectNode rst = new ObjectNode(JsonNodeFactory.instance);

        for (StructFieldDeclaration fDcl : getStructDeclaration().fields()) {
            //TODO 支持N个字段组

            FormatFieldWriter writer = fieldWriters.get(fDcl);
            if (writer == null) {
                log.warn("[FormatFieldWriter]无字段对应的Writer：fieldDcl={}", fDcl);
                continue;
            }

            FieldInstance fInst = instance.getFieldInstance(fDcl);
            if (fInst == null) {
                Object defVal = fDcl.getDefaultValue();
                if (defVal != null) {
                    fInst = new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
                    instance.addFieldInstance(fInst);
                } else {
                    continue;
                }
            }
            writer.write(fInst, rst);
        }

        return rst;
    }
}
