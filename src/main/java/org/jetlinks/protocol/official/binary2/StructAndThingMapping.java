package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;


/**
 * @author v-lizy81
 * @date 2023/6/27 22:30
 */
public interface StructAndThingMapping  {

    DeviceMessage map(StructDeclaration structDcl);

    StructDeclaration map(DeviceMessage message);

}
