package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;
import org.jetlinks.protocol.official.artifact.XueBaoWaWaProtocolSupport;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeclarationBasedBinaryMessageCodecTest {

    private BinaryMessageCodec  codec;

    @Before
    public void setUp() throws Exception {
        codec = XueBaoWaWaProtocolSupport.buildCodec(null);
    }

    @Test
    public void decode() throws DecoderException {
        DeviceMessage rst;

        String cas1 = "fe 00 00 01 ff ff 0c 33 01 00 00 3e".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(cas1));

        rst = codec.decode(input);
        System.out.println(rst.toJson());

        String case2 = "fe 00 00 01 ff ff a5 35 00 01 00 02 00 03 00 04 00 05 00 06 3e".replace(" ", "");
        input = Unpooled.wrappedBuffer(Hex.decodeHex(case2));
        rst = codec.decode(input);
        System.out.println(rst.toJson());
    }

    @Test
    public void encode() {



    }
}