package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;

/**
 * @author v-lizy81
 * @date 2023/6/27 22:28
 */
public class DirectStructAndMessageMapper implements StructAndMessageMapper {

    private StructAndThingMapping   structAndThingMapping;

    private FieldAndPropertyMapping fieldAndPropertyMapping;


    @Override
    public StructInstance toStructInstance(DeviceMessage message) {

        StructDeclaration   structDcl = structAndThingMapping.map(message);




        return null;
    }

    @Override
    public DeviceMessage toDeviceMessage(StructInstance structInst) {
        return null;
    }

}
