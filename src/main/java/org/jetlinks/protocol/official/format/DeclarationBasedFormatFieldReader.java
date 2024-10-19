package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.SimpleFieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedFieldReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatFieldReader extends AbstractDeclarationBasedFieldReader implements FormatFieldReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFormatFieldReader.class);

    public DeclarationBasedFormatFieldReader(StructFieldDeclaration fieldDcl) {
        super(fieldDcl);
    }

    @Nullable
    @Override
    public FieldInstance read(ObjectNode root) {
        StructFieldDeclaration fDcl = getDeclaration();

        if (root == null) {
            return new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
        }

        JsonNode valNode = root.get(fDcl.getPathInStruct());
        if (valNode == null) {
            return new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
        }

        Object val = fDcl.getDataType().fromJson(valNode);
        if (val == null) {
            val = fDcl.getDefaultValue();
        }

        return new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
    }

    @Override
    public String toString() {
        return "DeclarationBasedFormatFieldReader{" +
                "fieldDcl=" + getDeclaration() +
                '}';
    }
}
