package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;

/**
 * (设备通讯协议消息与物模型消息)二进制或文本负载消息的编解码
 */
public interface BinaryMessageCodec {

    /**
     * 解码设备上发的消息
     * @param context       编解码上下文，（必要）；
     * @param buf           消息负载字节流，（必要）；
     * @return  如果是支持的消息返回物模型消息，否则返回空；
     */
    DeviceMessage   decode(MessageCodecContext context, ByteBuf buf);

    /**
     * 编码设备消息
     * @param context       编解码上下文，（必要）；
     * @param message       待下发的设备消息，（必要）；
     * @return  如果是支持的物模型消息返回编码后的消息字节流，否则返回空
     */
    ByteBuf         encode(MessageCodecContext context, DeviceMessage message);

}
