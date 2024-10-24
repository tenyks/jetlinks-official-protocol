package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;

public interface FormatFieldWriter {

    boolean write(FieldInstance instance, JsonNode outputBuf);

    static FormatFieldWriter create(StructFieldDeclaration fieldDcl) {
        return new DeclarationBasedFormatFieldWriter(fieldDcl);
    }

}
