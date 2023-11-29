package org.jetlinks.protocol.official.mapping;

import org.jetlinks.protocol.official.binary2.BaseDataType;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/11/29
 * @since V1.3.0
 */
public interface FieldDeclaration {

    /**
     * 字段编码
     */
    String          getCode();

    String          getName();

    /**
     * 取值数据类型
     */
    BaseDataType    getDataType();

    Object          getDefaultValue();

    Iterable<ThingAnnotation> thingAnnotations();

}
