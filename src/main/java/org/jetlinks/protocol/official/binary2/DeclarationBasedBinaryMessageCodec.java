package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;

/**
 * @author v-lizy81
 * @date 2023/6/29 00:27
 */
public class DeclarationBasedBinaryMessageCodec implements BinaryMessageCodec {

    private StructSuit  structSuit;

    private StructAndMessageMapper  mapper;

    public DeclarationBasedBinaryMessageCodec(StructSuit structSuit, StructAndMessageMapper mapper) {
        this.structSuit = structSuit;
        this.mapper = mapper;
    }

    @Override
    public DeviceMessage decode(ByteBuf buf) {
        StructInstance structInst = structSuit.deserialize(buf);

        DeviceMessage deviceMsg = mapper.toDeviceMessage(structInst);

        return deviceMsg;
    }

    @Override
    public ByteBuf encode(DeviceMessage message) {

        StructInstance structInst = mapper.toStructInstance(message);

        structSuit.serialize(structInst);

        return null;
    }
}
