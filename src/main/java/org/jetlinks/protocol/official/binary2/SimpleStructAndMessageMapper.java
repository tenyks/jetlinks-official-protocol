package org.jetlinks.protocol.official.binary2;

import com.alibaba.fastjson.JSON;
import org.jetlinks.core.message.CommonDeviceMessage;
import org.jetlinks.core.message.CommonDeviceMessageReply;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.protocol.common.mapping.DefaultThingContext;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.common.mapping.ThingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @date 2023/6/27 22:28
 */
public class SimpleStructAndMessageMapper implements StructAndMessageMapper {

    private static final Logger log = LoggerFactory.getLogger(SimpleStructAndMessageMapper.class);

    private StructAndThingMapping           structAndThingMapping;

    private FieldAndPropertyMapping         fieldAndPropertyMapping;

    private FieldValueAndPropertyMapping    fieldValueAndPropertyMapping;


    public SimpleStructAndMessageMapper(StructAndThingMapping structAndThingMapping,
                                        FieldAndPropertyMapping fieldAndPropertyMapping,
                                        FieldValueAndPropertyMapping fieldValueAndPropertyMapping) {
        this.structAndThingMapping = structAndThingMapping;
        this.fieldAndPropertyMapping = fieldAndPropertyMapping;
        this.fieldValueAndPropertyMapping = fieldValueAndPropertyMapping;
    }

    @Override
    public StructInstance toStructInstance(@Nullable MapperContext context, DeviceMessage message) {
        StructDeclaration   structDcl = structAndThingMapping.map(message);
        StructInstance structInst = new SimpleStructInstance(structDcl);

        for (StructFieldDeclaration fieldDcl : structDcl.fields()) {
            if (fieldDcl.thingAnnotations() == null) continue;

            String itemKey = fieldAndPropertyMapping.toProperty(fieldDcl);

            for (ThingAnnotation tAnn : fieldDcl.thingAnnotations()) {
                Object itemVal = tAnn.invokeGetter(null, message, itemKey);

                if (itemVal == null && tAnn.isRequired()) {
                    log.warn("[CodecMapper]必要参数({})取值为空或不合法，devMsg={}", fieldDcl.getCode(), message.toString());
                    return null;
                }

                Object fieldVal = fieldValueAndPropertyMapping.toFieldValue(context, fieldDcl, itemVal);
                FieldInstance fieldInst = new SimpleFieldInstance(fieldDcl, fieldVal);
                structInst.addFieldInstance(fieldInst);
            }
        }

        return structInst;
    }

    @Override
    public DeviceMessage toDeviceMessage(@Nullable MapperContext context, StructInstance structInst) {
        DeviceMessage msg = structAndThingMapping.map(structInst.getDeclaration());
        if (msg == null) {
            log.warn("[CodecMapper]{}没有映射到DeviceMessage", structInst.getDeclaration().getName());
            return null;
        }

        if (msg.getDeviceId() == null && context != null && context.getDeviceId() != null) {
            if (msg instanceof CommonDeviceMessageReply) {
                ((CommonDeviceMessageReply) msg).setDeviceId(context.getDeviceId());
            } else if (msg instanceof CommonDeviceMessage) {
                ((CommonDeviceMessage)msg).setDeviceId(context.getDeviceId());
            }
        }

        ThingContext thingContext = new DefaultThingContext(structInst.getDeclaration(), structInst);

        Iterable<ThingAnnotation> structThAnns = structInst.getDeclaration().thingAnnotations();
        if (structThAnns != null) {
            for (ThingAnnotation tAnn : structThAnns) {
                tAnn.invokeSetter(thingContext, msg);
            }
        }

        for (FieldInstance fieldInst : structInst.filedInstances()) {
            if (fieldInst.getDeclaration().thingAnnotations() == null) continue;

            Object itemVal = fieldValueAndPropertyMapping.toPropertyValue(context, fieldInst);

            for (ThingAnnotation tAnn : fieldInst.getDeclaration().thingAnnotations()) {
                String itemKey = fieldAndPropertyMapping.toProperty(fieldInst);

                tAnn.invokeSetter(thingContext, msg, itemKey, itemVal);
            }
        }

        return msg;
    }

}
