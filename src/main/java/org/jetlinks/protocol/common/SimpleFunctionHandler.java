package org.jetlinks.protocol.common;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 根据物模型消息的serviceId(functionId或eventId)路由到不同的响应函数
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/5
 * @since V3.1.0
 */
public class SimpleFunctionHandler implements FunctionHandler {

    private final Map<String, BiFunction<EncodedMessage, DeviceMessage, ByteBuf>> idxToCallable;

    public SimpleFunctionHandler() {
        this.idxToCallable = new HashMap<>();
    }

    public SimpleFunctionHandler addCallable(String serviceId, BiFunction<EncodedMessage, DeviceMessage, ByteBuf> callable) {
        this.idxToCallable.put(serviceId, callable);

        return this;
    }

    @Override
    public ByteBuf apply(@Nonnull EncodedMessage srcMsg, @Nonnull DeviceMessage thingMsg) {
        String serviceId = null;
        if (thingMsg instanceof EventMessage) {
            serviceId = ((EventMessage) thingMsg).getEvent();
        } else if (thingMsg instanceof FunctionInvokeMessageReply) {
            serviceId = ((FunctionInvokeMessageReply) thingMsg).getFunctionId();
        }
        if (serviceId == null) return null;

        BiFunction<EncodedMessage, DeviceMessage, ByteBuf> callable = idxToCallable.get(serviceId);
        if (callable == null) return null;

        return callable.apply(srcMsg, thingMsg);
    }
}
