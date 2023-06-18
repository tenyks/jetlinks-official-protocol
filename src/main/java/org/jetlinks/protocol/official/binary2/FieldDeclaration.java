package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.binary.DataType;

public interface FieldDeclaration {

    String          getCode();

    boolean         isPayloadField();

    DataType        getDataType();

    short           getSize();

    short           getOffset();

    DynamicAnchor   getDynamicAnchor();

    DynamicSize     getDynamicSize();

}
