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

    StructDeclaration       addField(StructFieldDeclaration field);

    StructDeclaration       addFieldGroup(NRepeatFieldGroupDeclaration fieldGrp);

    StructFieldDeclaration  getField(String code);

    NRepeatFieldGroupDeclaration  getFieldGroup(String code);

    StructPartDeclaration   getPart(String code);

    @NotNull
    String  getFeatureCode();

    String  getName();

    Iterable<ThingAnnotation>           thingAnnotations();

    Iterable<StructPartDeclaration>     parts();

    CRCCalculator                       getCRCCalculator();

    boolean     isEnableEncode();

    boolean     isEnableDecode();
}
