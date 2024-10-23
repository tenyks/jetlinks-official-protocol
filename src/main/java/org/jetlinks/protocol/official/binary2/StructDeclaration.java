package org.jetlinks.protocol.official.binary2;

import me.tenyks.core.crc.CRCCalculator;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.validation.constraints.NotNull;

/**
 * （字节流或格式化）结构体声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructDeclaration {

    default boolean                     isFormatStruct() {
        return false;
    }

    StructFieldDeclaration              getField(String code);

    NRepeatGroupDeclaration             getFieldGroup(String code);

    StructPartDeclaration               getPart(String code);

    @NotNull
    String  getFeatureCode();

    String  getName();

    Iterable<ThingAnnotation>           thingAnnotations();

    String  getServiceIdOrFunctionId();

    MessageIdMappingAnnotation          messageIdMappingAnnotation();

    Iterable<StructPartDeclaration>     parts();

    Iterable<StructFieldDeclaration>    fields();

    CRCCalculator                       getCRCCalculator();

    boolean         isEnableEncode();

    boolean         isEnableDecode();

}
