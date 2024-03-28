package org.jetlinks.protocol.e53;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.TestMessageDecodeContext;
import org.jetlinks.protocol.official.TestMessageEncodeContext;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.binary2.StructSuit;
import org.junit.Test;

import java.util.Properties;

/**
 * @author v-lizy81
 * @date 2024/3/28 22:48
 */
public class E53IAxStructSuitBuilderTest {

    private StructSuit suit = E53IAxProtocolSupport.buildStructSuitV1();

    private MessageCodecContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageCodecContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decode() throws DecoderException {
        String payload = "fa 11 01 02 10 00 0e 3f 8c cc cd 3f 99 99 9a 40 06 66 66 01 00".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(payload));

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
    }

}
