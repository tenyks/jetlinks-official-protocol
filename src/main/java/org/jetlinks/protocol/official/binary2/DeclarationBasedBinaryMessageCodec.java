package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;

/**
 * @author v-lizy81
 * @date 2023/6/29 00:27
 */
public class DeclarationBasedBinaryMessageCodec implements BinaryMessageCodec {

    private StructSuit  structSuit;

    private StructAndMessageMapper  mapper;

    @Override
    public <T extends DeviceMessage> T decode(ByteBuf buf) {
        return null;
    }

    @Override
    public ByteBuf encode(DeviceMessage message) {
        return null;
    }
}
