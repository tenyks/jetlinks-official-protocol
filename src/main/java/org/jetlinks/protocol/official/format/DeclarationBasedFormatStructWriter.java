package org.jetlinks.protocol.official.format;

import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedStructWriter;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatStructWriter extends AbstractDeclarationBasedStructWriter implements FormatStructWriter {

    public DeclarationBasedFormatStructWriter(StructDeclaration structDcl) {
        super(structDcl);
    }

    @Override
    public String write(StructInstance instance) {
        return null;
    }
}
