package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;

/**
 * 二进制消息的编解码
 */
public interface BinaryMessageCodec {

    DeviceMessage decode(MessageCodecContext context, ByteBuf buf);

    ByteBuf encode(MessageCodecContext context, DeviceMessage message);

}
