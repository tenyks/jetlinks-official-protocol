package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;

/**
 * 二进制消息的编解码
 */
public interface BinaryMessageCodec {

    DeviceMessage decode(MessageDecodeContext context, ByteBuf buf);

    ByteBuf encode(MessageEncodeContext context, DeviceMessage message);

}
