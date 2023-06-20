package org.jetlinks.protocol.official.binary2;

/**
 * 字段结构声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface FieldDeclaration {

    String          getCode();

    boolean         isPayloadField();

    BaseDataType    getDataType();

    short           getSize();

    short           getOffset();

    DynamicAnchor   getDynamicAnchor();

    DynamicSize     getDynamicSize();

    Object          getDefaultValue();
}
