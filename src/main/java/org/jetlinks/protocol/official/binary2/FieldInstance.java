package org.jetlinks.protocol.official.binary2;

public interface FieldInstance {

    StructFieldDeclaration  getDeclaration();

    Object getValue();

    int  getIntValue();

    short  getShortValue();

    String getCode();

    Short getOffset();

    Short getSize();

}
