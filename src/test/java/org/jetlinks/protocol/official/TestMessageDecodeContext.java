package org.jetlinks.protocol.official;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestMessageDecodeContext implements MessageDecodeContext {

    private DeviceOperator deviceOperator;

    public TestMessageDecodeContext(String deviceId, String sessionId) {
        this.deviceOperator = new TestDeviceOperator(deviceId, sessionId);
    }

    @Nonnull
    @Override
    public EncodedMessage getMessage() {
        return null;
    }

    @Nullable
    @Override
    public DeviceOperator getDevice() {
        return deviceOperator;
    }
}
