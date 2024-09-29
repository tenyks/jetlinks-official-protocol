package org.jetlinks.protocol.common;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/29
 * @since V3.1.0
 */
public class SimpleDeviceRequestHandler implements DeviceRequestHandler {

    private final Map<String, BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply>> idxToCallable;

    public SimpleDeviceRequestHandler() {
        this.idxToCallable = new HashMap<>();
    }

    public SimpleDeviceRequestHandler addCallable(String serviceId, BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply> callable) {
        this.idxToCallable.put(serviceId, callable);

        return this;
    }

    @Override
    public DeviceRequestMessageReply apply(@Nonnull DeviceOperator device, @Nonnull DeviceRequestMessage<?> reqMsg) {
        String serviceId = reqMsg.getFunctionId();
        if (serviceId == null) return null;

        BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply> callable = idxToCallable.get(serviceId);
        if (callable == null) return null;

        return callable.apply(device, reqMsg);
    }
}
