package org.jetlinks.protocol.official.format;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.protocol.official.common.StructAndMessageMapper;

/**
 * (设备通讯协议消息与物模型消息)文本有格式消息的编解码
 */
public interface FormatMessageCodec {

    /**
     * 解码设备上发的消息
     * @param context       编解码上下文，（必要）；
     * @param buf           消息字节流，（必要）；
     * @return  如果是支持的消息返回物模型消息，否则返回空；
     */
    DeviceMessage decode(MessageCodecContext context, String buf);

    /**
     * 编码设备消息
     * @param context       编解码上下文，（必要）；
     * @param message       待下发的设备消息，（必要）；
     * @return  如果是支持的物模型消息返回编码后的消息字节流，否则返回空
     */
    String encode(MessageCodecContext context, DeviceMessage message);

    static FormatMessageCodec    create(FormatStructSuit structSuit, StructAndMessageMapper mapper) {
        return new DeclarationBasedFormatMessageCodec(structSuit, mapper);
    }

}
