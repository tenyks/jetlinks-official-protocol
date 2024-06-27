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
        if (dcl instanceof NRepeatFieldGroupDeclaration) {
            return new NRepeatDeclarationBasedFieldGroupReader((NRepeatFieldGroupDeclaration)dcl);
        } else {
            return new DeclarationBasedFieldReader((StructFieldDeclaration) dcl);
        }
    }

    static FieldReader create(StructFieldDeclaration dcl) {
        return new DeclarationBasedFieldReader((StructFieldDeclaration) dcl);
    }

}
