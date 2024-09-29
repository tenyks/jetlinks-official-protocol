package org.jetlinks.protocol.common;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 业务请求处理
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/29
 * @since V3.1.0
 */
public interface DeviceRequestHandler {

    /**
     * 响应请求
     * @param device        设备数据
     * @param reqMsg        上行的解码后的物模型消息，（必要）
     * @return  如无返回空
     */
    @Nullable
    DeviceRequestMessageReply apply(@Nonnull DeviceOperator device, @Nonnull DeviceRequestMessage<?> reqMsg);

}
