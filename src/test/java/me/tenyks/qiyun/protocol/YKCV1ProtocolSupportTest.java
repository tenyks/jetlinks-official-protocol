package me.tenyks.qiyun.protocol;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
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
        reply.setMessageId("YKCV1_1728399796355_000001_0000");
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

    @Test
    public void decodeBillingTermsRequest() throws DecoderException {
        String payload = "68 0B 02 00 00 09 55031412782305 DD25";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeBillingTermsRequestReply() throws DecoderException {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("YKCV1_19271117778_000001_0200");
        reply.functionId("BillingTermsRequestReply");

        reply.addOutput("termsNo", (short) 0x0100);
        reply.addOutput("sharpEUP", 0x400D0300);
        reply.addOutput("sharpSUP", 0x9C400000);
        reply.addOutput("peakEUP", 0xE0930400);
        reply.addOutput("peakSUP", 0x9C400000);
        reply.addOutput("shoulderEUP", 0x801A0600);
        reply.addOutput("shoulderSUP", 0x9C400000);
        reply.addOutput("offPeakEUP", 0x20A10700);
        reply.addOutput("offPeakSUP", 0x9C400000);
        reply.addOutput("withLostRate", (byte) 0x00);

        reply.addOutput("rateNoOf00000030", (byte) 0x0);
        reply.addOutput("rateNoOf00300100", (byte) 0x1);
        reply.addOutput("rateNoOf01000130", (byte) 0x2);
        reply.addOutput("rateNoOf01300200", (byte) 0x3);

        reply.addOutput("rateNoOf02000230", (byte) 0x0);
        reply.addOutput("rateNoOf02300300", (byte) 0x1);
        reply.addOutput("rateNoOf03000330", (byte) 0x2);
        reply.addOutput("rateNoOf03300400", (byte) 0x3);


        String expect = "68 0E CE 04 00 06 55 03 14 12 78 23 05 00 01 00 8E 2F";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect, real);
    }

    @Test
    public void encodeCallOfRealTimeMonitorData() throws DecoderException {
        FunctionInvokeMessage msg = new FunctionInvokeMessage();
        msg.setDeviceId("32010200000001");
        msg.setMessageId("YKCV1_19271117778_000001_0000");
        msg.functionId("CallOfRealTimeMonitorData");

        msg.addInput("gunNo", (byte) 0x01);

        String expect = "68 0E CE 04 00 06 55 03 14 12 78 23 05 00 01 00 8E 2F";

        ByteBuf rst = codec.encode(encodeCtx, msg);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeReportRealTimeMonitorData() throws DecoderException {
        String payload = "68 40 1A03 00 13 01020304050607080908070605040302 55031412782305 " +
                "01 00 01 01 0200 0000 00 1020304050607080 00 00 1234 4567 11223344 55667788 12345678 0000 9DAC";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportChargingHandshakeData() throws DecoderException {
        String payload = "68 4D 0015 00 15 03201020000000011151116155535026 32010200000001 " +
                "01 010101 01 0102 0103 42594442 01020304 1E 01 02 000010 01 00 5744434446374245314741383031313738 100A0B07DF000000 FED2";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        //WDCDF7BE1GA801178=5744434446374245314741383031313738
        //BYDB=42594442

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportChargingSettingWithBMS() throws DecoderException {
        String payload = "68 31 0015 00 17 03201020000000011151116155535026 32010200000001 01 " +
                "0001 0002 0003 0004 05 0006 0007 0008 0009 000A 000B D18A";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportChargingFinishEvent() throws DecoderException {
        String payload = "68 2B 0016 00 19 03201020000000011151116155535026 32010200000001 01 " +
                "01 0002 0003 04 05 0006 0007 00000008 AE36";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportErrorEvent() throws DecoderException {
        String payload = "68 24 0017 00 1B 03201020000000011151116155535026 32010200000001 01 " +
                "0101010101010101 A2F3";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

}