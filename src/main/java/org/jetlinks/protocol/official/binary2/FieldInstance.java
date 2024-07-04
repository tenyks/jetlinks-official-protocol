package org.jetlinks.protocol.official.binary2;

public interface FieldInstance {

    StructFieldDeclaration  getDeclaration();

    Object getValue();

    int  getIntValue();

    short  getShortValue();

    default String getStringValue() {
        return getStringValue(null);
    }

    default String getStringValue(String defVal) {
        Object val = getValue();
        return (val instanceof String ? (String) val : (val != null ? val.toString() : defVal));
    }

    String getCode();

    Short getOffset();

    Short getSize();

}
