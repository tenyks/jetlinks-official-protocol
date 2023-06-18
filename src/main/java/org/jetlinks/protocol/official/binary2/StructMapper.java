package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:14
 */
public interface StructMapper {

    StructInstance fromDeviceMessage(DeviceMessage message);

    DeviceMessage toDeviceMessage(StructInstance structInst);

}
