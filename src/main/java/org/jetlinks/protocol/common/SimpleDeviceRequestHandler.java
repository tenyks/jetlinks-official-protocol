package org.jetlinks.protocol.common;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.event.ThingEventMessage;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/29
 * @since V3.1.0
 */
public class SimpleDeviceRequestHandler implements DeviceRequestHandler {

    private final Map<String, BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply>> idxToCallable;

    private final Map<String, BiFunction<DeviceOperator, DeviceRequestMessage<?> , Tuple2<DeviceRequestMessageReply, ThingEventMessage>>> idxToCallable2;

    public SimpleDeviceRequestHandler() {
        this.idxToCallable = new HashMap<>();
        this.idxToCallable2 = new HashMap<>();
    }

    public SimpleDeviceRequestHandler addCallableSilent(String serviceId,
                                                        BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply> callable) {
        this.idxToCallable.put(serviceId, callable);

        return this;
    }

    public SimpleDeviceRequestHandler addCallable(String serviceId,
                                                  BiFunction<DeviceOperator, DeviceRequestMessage<?> , Tuple2<DeviceRequestMessageReply, ThingEventMessage>> callable) {
        this.idxToCallable2.put(serviceId, callable);

        return this;
    }

    @Override
    public Tuple2<DeviceRequestMessageReply, Optional<ThingEventMessage>>
    apply(@Nonnull DeviceOperator device, @Nonnull DeviceRequestMessage<?> reqMsg) {
        String funId = reqMsg.getFunctionId();
        if (funId == null) return null;

        BiFunction<DeviceOperator, DeviceRequestMessage<?> , DeviceRequestMessageReply> callable;
        callable = idxToCallable.get(funId);
        if (callable != null) {
            DeviceRequestMessageReply reply = callable.apply(device, reqMsg);

            return Tuples.of(reply, Optional.empty());
        }

        BiFunction<DeviceOperator, DeviceRequestMessage<?> , Tuple2<DeviceRequestMessageReply, ThingEventMessage>> callable2;
        callable2 = idxToCallable2.get(funId);
        if (callable2 != null) {
            Tuple2<DeviceRequestMessageReply, ThingEventMessage> reply = callable2.apply(device, reqMsg);

            return Tuples.of(reply.getT1(), Optional.of(reply.getT2()));
        }

        return null;
    }
}
