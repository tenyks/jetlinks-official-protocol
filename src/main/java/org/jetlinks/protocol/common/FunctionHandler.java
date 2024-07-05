package org.jetlinks.protocol.common;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.EncodedMessage;

import javax.annotation.Nonnull;


/**
 * 响应上行的命令请求，如：查询时间
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/5
 * @since V3.1.0
 */
public interface FunctionHandler {

    /**
     * 响应请求
     * @param srcMsg        上行的源消息，（必要）
     * @param thingMsg      上行的解码后的物模型消息，（必要）
     * @return  如无返回空
     */
    ByteBuf apply(@Nonnull EncodedMessage srcMsg, @Nonnull DeviceMessage thingMsg);

}
