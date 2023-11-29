package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.mapping.ThingAnnotation;

/**
 * 字段结构声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructFieldDeclaration {

    /**
     * 字段编码
     */
    String          getCode();

    boolean         isPayloadField();

    /**
     * 取值数据类型
     */
    BaseDataType    getDataType();

    /**
     * 数据长度
     */
    short           getSize();

    /**
     * 数值
     */
    short           getOffset();

    DynamicAnchor   getDynamicAnchor();

    DynamicSize     getDynamicSize();

    Object          getDefaultValue();

    Iterable<ThingAnnotation> thingAnnotations();
}
