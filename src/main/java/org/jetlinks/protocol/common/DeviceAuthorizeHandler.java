package org.jetlinks.protocol.common;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.EncodedMessage;
import reactor.util.function.Tuple2;

import javax.annotation.Nonnull;

/**
 * 设备认证请求处理器
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/28
 * @since V3.1.0
 */
public interface DeviceAuthorizeHandler {

    /**
     * 响应设备请求
     * @param srcMsg        上行的源消息，（必要）
     * @param thingMsg      上行的解码后的物模型消息，（必要）
     * @return  返回
     */
    Tuple2<DeviceMessage, DeviceMessage> apply(@Nonnull EncodedMessage srcMsg, @Nonnull DeviceMessage thingMsg);

}
