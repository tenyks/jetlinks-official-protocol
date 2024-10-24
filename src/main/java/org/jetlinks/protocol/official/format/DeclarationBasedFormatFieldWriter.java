package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedFieldWriter;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatFieldWriter extends AbstractDeclarationBasedFieldWriter implements FormatFieldWriter {

    private final String[]  pathParts;

    public DeclarationBasedFormatFieldWriter(StructFieldDeclaration fieldDcl) {
        super(fieldDcl);

        if (StringUtils.isEmpty(fieldDcl.getPathInStruct())) {
            throw new IllegalArgumentException("字段定义中PathInStruct不能为空");
        }

        pathParts = fieldDcl.getPathInStruct().split("\\.");
    }

    @Override
    public boolean write(FieldInstance instance, JsonNode outputBuf) {
        JsonNode parent = ensurePathParent(pathParts, outputBuf);

        JsonNode valNode = getDeclaration().getDataType().toJson(instance.getValue());
        ((ObjectNode) parent).set(pathParts[pathParts.length - 1], valNode);

        return true;
    }

    private static JsonNode ensurePathParent(String[] path, JsonNode outputBuf) {
        if (path == null || path.length <= 1) return outputBuf;

        JsonNode latestNode = outputBuf;
        for (int i = 0; i < path.length - 1; i++) {
            String p = path[i];
            //TODO 实现数组的支持
            JsonNode jn = latestNode.get(p);
            if (jn == null) {
                jn = new ObjectNode(JsonNodeFactory.instance);
                ((ObjectNode) latestNode).set(p, jn);
            }

            latestNode = jn;
        }

        return latestNode;
    }
}
