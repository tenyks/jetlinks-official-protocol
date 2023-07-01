package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @date 2023/6/27 22:28
 */
public class SimpleStructAndMessageMapper implements StructAndMessageMapper {

    private StructAndThingMapping   structAndThingMapping;

    private FieldAndPropertyMapping fieldAndPropertyMapping;

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

        for (FieldDeclaration fieldDcl : structDcl.fields()) {
            if (fieldDcl.thingAnnotations() == null) continue;

            String itemKey = fieldAndPropertyMapping.toProperty(fieldDcl);

            for (ThingAnnotation tAnn : fieldDcl.thingAnnotations()) {
                Object itemVal = tAnn.invokeGetter(null, message, itemKey);

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

        if (context != null && context.getDeviceId() != null) {
            msg.messageId(context.getDeviceId());
        }

        Iterable<ThingAnnotation> structThAnns = structInst.getDeclaration().thingAnnotations();
        if (structThAnns != null) {
            for (ThingAnnotation tAnn : structThAnns) {
                tAnn.invokeSetter(null, msg);
            }
        }

        for (FieldInstance fieldInst : structInst.filedInstances()) {
            if (fieldInst.getDeclaration().thingAnnotations() == null) continue;

            Object itemVal = fieldValueAndPropertyMapping.toPropertyValue(context, fieldInst);

            for (ThingAnnotation tAnn : fieldInst.getDeclaration().thingAnnotations()) {
                String itemKey = fieldAndPropertyMapping.toProperty(fieldInst.getDeclaration());

                tAnn.invokeSetter(null, msg, itemKey, itemVal);
            }
        }

        return msg;
    }

}
