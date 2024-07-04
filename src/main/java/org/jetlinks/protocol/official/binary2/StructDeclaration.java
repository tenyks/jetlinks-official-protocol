package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.validation.constraints.NotNull;

/**
 * （字节流）结构体声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructDeclaration {

    StructFieldDeclaration  getField(String code);

    NRepeatGroupDeclaration getFieldGroup(String code);

    StructPartDeclaration   getPart(String code);

    @NotNull
    String  getFeatureCode();

    String  getName();

    Iterable<ThingAnnotation>           thingAnnotations();

    MessageIdMappingAnnotation          messageIdMappingAnnotation();

    Iterable<StructPartDeclaration>     parts();

    Iterable<StructFieldDeclaration>    fields();

    CRCCalculator                       getCRCCalculator();

    boolean     isEnableEncode();

    boolean     isEnableDecode();

}
