package org.jetlinks.protocol.official.core;

import org.jetlinks.core.message.DeviceMessage;

public interface PayloadWriter {
    void write(DeviceMessage message);
}
