package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.format.DeclarationBasedFormatFieldReader;
import org.jetlinks.protocol.official.format.FormatFieldReader;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface StructPartReader {

    StructPartDeclaration   getDeclaration();

    static StructPartReader create(StructDeclaration structDcl, StructPartDeclaration partDcl) {
        if (partDcl instanceof NRepeatGroupDeclaration) {
            return new NRepeatDeclarationBasedFieldGroupReader(structDcl, (NRepeatGroupDeclaration)partDcl);
        } else {
            if (structDcl.isFormatStruct()) {
                return createFormat((StructFieldDeclaration) partDcl);
            } else {
                return createBinary((StructFieldDeclaration) partDcl);
            }
        }
    }

    static BinaryFieldReader createBinary(StructFieldDeclaration partDcl) {
        return new DeclarationBasedBinaryFieldReader(partDcl);
    }

    static FormatFieldReader createFormat(StructFieldDeclaration partDcl) {
        return new DeclarationBasedFormatFieldReader(partDcl);
    }

}
