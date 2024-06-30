package org.jetlinks.protocol.michong;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.codec.DeviceMessageDecoder;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.utils.BytesUtils;
import org.jetlinks.protocol.e53.E53IAxProtocolSupport;
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
 * @version 1.0.0
 * @date 2024/6/30
 * @since V3.1.0
 */
public class MiChongProtocolTest {

    private StructSuit suit = MiChongV2ProtocolSupport.buildStructSuitV2();

    private DeviceMessageDecoder decoder = E53IAxProtocolSupport.buildDeviceMessageCodec(new PluginConfig(new Properties()));

    private BinaryMessageCodec codec = E53IAxProtocolSupport.buildBinaryMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decodeReportProperties() throws DecoderException {
        String payload = "AA 16 21 01 02 01 01 00 01 00 02 00 03 02 01 00 04 00 05 00 06 18";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        //DeviceMessage devMsg = codec.decode(decodeCtx, input);
        //System.out.println(devMsg);

    }
}
