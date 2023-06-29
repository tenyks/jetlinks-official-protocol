package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;

/**
 * @author v-lizy81
 * @date 2023/6/27 22:30
 */
public interface StructAndThingMapping  {

    DeviceMessageReply map(StructDeclaration structDcl) throws IllegalAccessException, InstantiationException;

    StructDeclaration map(DeviceMessage message);

}
