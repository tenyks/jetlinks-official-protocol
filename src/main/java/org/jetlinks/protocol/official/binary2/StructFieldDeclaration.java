package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.annotation.Nullable;

/**
 * 字段结构声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructFieldDeclaration extends StructPartDeclaration {

    @Nullable
    NRepeatFieldGroupDeclaration    includingGroup();

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



    DynamicSize     getDynamicSize();

    Object          getDefaultValue();

    Iterable<ThingAnnotation> thingAnnotations();
}
