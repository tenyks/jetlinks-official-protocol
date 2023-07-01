package org.jetlinks.protocol.official;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.MessageEncodeContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestMessageEncodeContext implements MessageEncodeContext {

    private DeviceOperator  deviceOperator;

    public TestMessageEncodeContext(String deviceId, String sessionId) {
        this.deviceOperator = new TestDeviceOperator(deviceId, sessionId);
    }

    @Nonnull
    @Override
    public Message getMessage() {
        return null;
    }

    @Nullable
    @Override
    public DeviceOperator getDevice() {
        return deviceOperator;
    }
}
