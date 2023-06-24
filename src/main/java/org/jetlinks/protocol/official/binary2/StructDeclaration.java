package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

/**
 * （字节流）结构体声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructDeclaration {

    StructDeclaration addField(FieldDeclaration field);

    FieldDeclaration    getField(String code);

    @NotNull
    String  getFeatureCode();

    Iterable<FieldDeclaration>  fields();

    CRCCalculator   getCRCCalculator();

    boolean     isEnableEncode();

    boolean     isEnableDecode();
}
