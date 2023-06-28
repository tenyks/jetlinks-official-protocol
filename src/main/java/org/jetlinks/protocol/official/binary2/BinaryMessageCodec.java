package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;

/**
 * 二进制消息的编解码
 */
public interface BinaryMessageCodec {

    <T extends DeviceMessage> T  decode(ByteBuf buf);

    ByteBuf encode(DeviceMessage message);

}
