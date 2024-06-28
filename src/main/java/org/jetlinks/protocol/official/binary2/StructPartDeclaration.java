package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

/**
 * 结构组成部分声明
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface StructPartDeclaration {

    /**
     * 字段或字段组编码
     */
    String          getCode();

    /**
     * 动态的参考锚点
     */
    DynamicAnchor   getDynamicAnchor();

    DynamicSize     getDynamicSize();

    /**
     * 数据长度
     */
    short           getSize();

    /**
     * 数值
     */
    short           getOffset();

    Iterable<ThingAnnotation> thingAnnotations();

}
