package me.tenyks.qiyun.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
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
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class YKCV1ProtocolSupportTest {

    private StructSuit suit = YKCV1ProtocolSupport.buildStructSuitV1();

    private BinaryMessageCodec codec = YKCV1ProtocolSupport.buildBinaryMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decodeAuthRequest() throws DecoderException {
        String payload = "68 22 00 01 00 01 55031412782305 00 02 0F 56342E312E353000 " +
                        "01 01010101010101010101 04 67 5A";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeAuthResponse() throws DecoderException {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("0000");
        reply.functionId("AuthResponse");

        reply.addOutput("rstFlag", "SUCCESS");

        String expect = "68 0C 00 00 00 02 55 03 14 12 78 23 05 00 DA 4C";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeHeartBeatPing() throws DecoderException {
        String payload = "68 0D 00 01 00 03 32010200000001 01 00 6890";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeHeartBeatPong() throws DecoderException {
        AcknowledgeDeviceMessage ack = new AcknowledgeDeviceMessage();
        ack.setDeviceId("55031412782305");
        ack.setMessageId("0000");
        ack.setFunctionId("HeartBeatPong");

        ack.addOutput("pongFlag", (byte) 0x01);

        String expect = "68 0D 36 00 00 04 55 03 14 12 78 23 05 01 00 65 B2";

        ByteBuf rst = codec.encode(encodeCtx, ack);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeCheckFeeTermsRequest() throws DecoderException {
        String payload = "68 0D 00 02 00 05 32010200000001 0001 9C00";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeCheckFeeTermsRequestReply() throws DecoderException {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("0000");
        reply.functionId("CheckFeeTermsRequestReply");

        reply.addOutput("termsNo", (short) 0x0001);
        reply.addOutput("rstFlag", "PASS");

        String expect = "68 0E CE 04 00 06 55 03 14 12 78 23 05 00 01 00 8E 2F";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect, real);
    }
}