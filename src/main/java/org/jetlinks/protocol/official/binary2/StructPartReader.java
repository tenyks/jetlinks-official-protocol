package org.jetlinks.protocol.official.binary2;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface StructPartReader {

    StructPartDeclaration   getDeclaration();

    static StructPartReader create(StructPartDeclaration dcl) {
        if (dcl instanceof NRepeatGroupDeclaration) {
            return new NRepeatDeclarationBasedFieldGroupReader((NRepeatGroupDeclaration)dcl);
        } else {
            return new DeclarationBasedBinaryFieldReader((StructFieldDeclaration) dcl);
        }
    }

    static BinaryFieldReader create(StructFieldDeclaration dcl) {
        return new DeclarationBasedBinaryFieldReader(dcl);
    }

}
