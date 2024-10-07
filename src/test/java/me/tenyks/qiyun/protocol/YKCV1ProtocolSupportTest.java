package me.tenyks.qiyun.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessageReply;
import org.jetlinks.core.utils.BytesUtils;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.TestMessageDecodeContext;
import org.jetlinks.protocol.official.TestMessageEncodeContext;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.binary2.StructSuit;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Test;

import java.util.Properties;

public class YKCV1ProtocolSupportTest {

    private StructSuit suit = YKCV1ProtocolSupport.buildStructSuitV1();

    private BinaryMessageCodec codec = YKCV1ProtocolSupport.buildBinaryMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decodeAuthRequest() throws DecoderException {
        String payload = "68 22 00 00 00 01 55031412782305 00 02 0F 56342E312E353000 " +
                        "01 01010101010101010101 04 67 5A";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
    }

    @Test
    public void encodeAuthResponse() throws DecoderException {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("0000");
        reply.functionId("AuthResponse");

        reply.addOutput("rstFlag", "SUCCESS");

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
    }
}