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
import org.jetlinks.core.utils.DateUtils;
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
        AcknowledgeDeviceMessage reply = new AcknowledgeDeviceMessage();
        reply.setDeviceId("10001001000001");
        reply.setMessageId("YKCV1_1728399796355_000001_0100");
        reply.setFunctionId("AuthResponse");

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
        ack.setDeviceId("10001001000001");
        ack.setMessageId("YKCV1_1728399796355_000001_0270");
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
        reply.setDeviceId("10001001000001");
        reply.setMessageId("YKCV1_1728399796355_000001_0225");
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
        reply.setDeviceId("10001001000001");
        reply.setMessageId("YKCV1_19271117778_000001_0155");
        reply.functionId("BillingTermsRequestReply");

//        reply.addOutput("termsNo", (short) 0x0100);
//        reply.addOutput("sharpEUP", 0x400D0300);
//        reply.addOutput("sharpSUP", 0x9C400000);
//        reply.addOutput("peakEUP", 0xE0930400);
//        reply.addOutput("peakSUP", 0x9C400000);
//        reply.addOutput("shoulderEUP", 0x801A0600);
//        reply.addOutput("shoulderSUP", 0x9C400000);
//        reply.addOutput("offPeakEUP", 0x20A10700);
//        reply.addOutput("offPeakSUP", 0x9C400000);
//        reply.addOutput("withLostRate", (byte) 0x00);
//
//        reply.addOutput("rateNoOf00000030", (byte) 0x0);
//        reply.addOutput("rateNoOf00300100", (byte) 0x1);
//        reply.addOutput("rateNoOf01000130", (byte) 0x2);
//        reply.addOutput("rateNoOf01300200", (byte) 0x3);
//
//        reply.addOutput("rateNoOf02000230", (byte) 0x0);
//        reply.addOutput("rateNoOf02300300", (byte) 0x1);
//        reply.addOutput("rateNoOf03000330", (byte) 0x2);
//        reply.addOutput("rateNoOf03300400", (byte) 0x3);

        reply.addOutput("termsNo", (short) 0x0100);
        reply.addOutput("sharpEUP", 0x000186a0);
        reply.addOutput("sharpSUP", 0x000186a0);
        reply.addOutput("peakEUP", 0x000186a0);
        reply.addOutput("peakSUP", 0x000186a0);
        reply.addOutput("shoulderEUP", 0x000186a0);
        reply.addOutput("shoulderSUP", 0x000186a0);
        reply.addOutput("offPeakEUP", 0x000186a0);
        reply.addOutput("offPeakSUP", 0x000186a0);
        reply.addOutput("withLostRate", (byte) 0x00);

        reply.addOutput("rateNoOf00000030", (byte) 0x0);
        reply.addOutput("rateNoOf00300100", (byte) 0x0);
        reply.addOutput("rateNoOf01000130", (byte) 0x0);
        reply.addOutput("rateNoOf01300200", (byte) 0x0);

        reply.addOutput("rateNoOf02000230", (byte) 0x0);
        reply.addOutput("rateNoOf02300300", (byte) 0x0);
        reply.addOutput("rateNoOf03000330", (byte) 0x0);
        reply.addOutput("rateNoOf03300400", (byte) 0x0);


        String expect = "68 5E 02 00 00 0A 55 03 14 12 78 23 05 01 00 400D0300 9C400000 E0930400 9C400000 801A0600 9C400000 20A10700 9C400000 " +
                "00 " +
                "00 00 00 00 00 00 00 00" +
                "00 00 00 00 00 00 00 00" +
                "00 00 00 00 00 00 00 00" +
                "00 00 00 00 00 00 00 00" +
                "00 00 00 00 00 00 00 00" +
                "00 00 00 00 00 00 00 00" +
                "5E 60"
                ;


        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        Assert.assertEquals(expect.replace(" ", ""), real.replace(" ", ""));
    }

    @Test
    public void encodeCallOfRealTimeMonitorData() throws DecoderException {
        FunctionInvokeMessage msg = new FunctionInvokeMessage();
        msg.setDeviceId("32010200000001");
        msg.setMessageId("YKCV1_19271117778_000001_0000");
        msg.functionId("CallOfRealTimeMonitorData");

        msg.addInput("gunNo", (byte) 0x01);

        String expect = "68 0C 00 00 00 12 32 01 02 00 00 00 01 01 00 69";

        ByteBuf rst = codec.encode(encodeCtx, msg);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        // 68 0C 00 00 00 12 32 01 02 00 00 00 01 01 E3 1D
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

    @Test
    public void decodeReportBMSStopEvent() throws DecoderException {
        String payload = "68 20 0018 00 1D 03201020000000011151116155535026 32010200000001 01 " +
                "03 0003 03 8445";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportChargerStopEvent() throws DecoderException {
        String payload = "68 20 0018 00 21 32010200000000111511161555350260 32010200000001 01 " +
                "03 0003 03 8445";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportBMSRequirementAndChargerOutputData() throws DecoderException {
        String payload = "68 30 0019 00 23 32010200000000111511161555350260 32010200000001 01 " +
                "0001 0002 01 0004 0005 0006 0A 0007 0008 0009 0010 1D57";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportBMSChargingData() throws DecoderException {
        String payload = "68 23 0021 00 25 32010200000000111511161555350260 32010200000001 01 " +
                "01 30 31 01 0101 72B9";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeAny() throws DecoderException {
        String payload = "68 0C 01 24 00 02 10 00 10 01 00 00 01 01 C0 5D";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodePileSwitchOnChargingRequest() throws DecoderException {
        String payload = "68 23 0021 00 31 32010200000001 01 " +
                "01 00 00000000D14B0A54 00000000000000000000000000000000 5744434446374245314741383031313738 0000";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodePileSwitchOnChargingRequestReply() {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply();
        reply.setDeviceId("32010200000001");
        reply.setMessageId("YKCV1_19271117778_000001_0004");
        reply.functionId("PileSwitchOnChargingRequestReply");

        reply.addOutput("transNo", "32010200000001012018061219595785");
        reply.addOutput("gunNo", (byte) 0x01);
        reply.addOutput("cardDisplayNo", "0000000000012345");
        reply.addOutput("accountAmount", (byte) 0x01);
        reply.addOutput("rstFlag", (byte) 0x01);

        String expect = "68 2A 00 04 00 32 32 01 02 00 00 00 01 01 20 18 06 12 19 59 57 85 32 01 02 00 00 00 01 01 00 00 00 00 00 01 23 45 00 00 00 01 01 00 22 E0";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void encodeSwitchOnChargingFunInv() {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("YKCV1_19271117778_000001_007C");
        reply.functionId("SwitchOnChargingFunInv");

        reply.addInput("transNo", "55031412782305012018061914444680");
        reply.addInput("gunNo", (byte) 0x01);
        reply.addInput("cardDisplayNo", "0000001000000573");
        reply.addInput("cardNo", "00000000D14B0A54");
        reply.addInput("accountAmount", 0xA0860100);


        String expect = "68 30 00 7C 00 34 55 03 14 12 78 23 05 01 20 18 06 19 14 44 46 80 55 03 14 12 78 23 05 01 00 00 00 10 00 00 05 73 00 00 00 00 D1 4B 0A 54 A0 86 01 00 13 F6";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeSwitchOnChargingFunInvReply() throws DecoderException {
        String payload = "68 1E 0002 00 33 32010200000000111511161555350260 32010200000001 01 01 00 0FE2";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 1E 0002 00 33 32010200000000111511161555350260 32010200000001 01 00 05 0FE2";
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeSwitchOffChargingFunInv() throws DecoderException {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("32010200000001");
        reply.setMessageId("YKCV1_19271117778_000001_0003");
        reply.functionId("SwitchOffChargingFunInv");

        reply.addInput("gunNo", (byte) 0x01);

        String expect = "68 0C 00 03 00 36 32 01 02 00 00 00 01 01 C1 A9";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodePileSwitchOffFunInvReply() throws DecoderException {
        String payload = "68 1E 0003 00 35 32010200000001 01 01 00 907E";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 1E 0003 00 35 32010200000001 01 00 02 907E";
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void decodeReportTransOrder() throws DecoderException {
        String payload = "68 A2 8001 00 3B 55031412782305012018061910262392 55031412782305 01 " +
                "98B70E11100314 98B70E11100314 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "0000000001 0000000009 00000008 00000008 80DE0F00 5744434446374245314741383031313738 02 98B70E11100314 40 00000000D14B0A54 388C"
                ;
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 A2 8001 00 3B 55031412782305012018061910262392 55031412782305 01 " +
                "98B70E11100314 98B70E11100314 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "D0FB0100 00000000 00000000 00000000 " +
                "0000000001 0000000009 00000008 00000008 80DE0F00 5744434446374245314741383031313738 02 98B70E11100314 64 00000000D14B0A54 388C"
        ;
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeReportTransOrderAck() {
        AcknowledgeDeviceMessage reply = new AcknowledgeDeviceMessage();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("YKCV1_19271117778_000001_0002");
        reply.setFunctionId("ReportTransOrderAck");

        reply.addOutput("transNo", "55031412782305012018061910262392");
        reply.addOutput("rstFlag", (byte) 0x00);

        String expect = "68 15 00 02 00 40 55 03 14 12 78 23 05 01 20 18 06 19 10 26 23 92 00 52 1E";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void encodeWritePileSettingFunInv() {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("32010200000001");
        reply.setMessageId("YKCV1_19271117778_000001_0008");
        reply.functionId("WritePileSettingFunInv");

        reply.addInput("enableFlag", "ENABLE");
        reply.addInput("maxOutputRate", 1);

        String expect = "68 0D 00 08 00 52 32 01 02 00 00 00 01 00 1E 3E 1A";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeWritePileSettingFunInvReply() throws DecoderException {
        String payload = "68 0C 0008 00 51 32010200000001 01 C1A9";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 0C 0008 00 51 32010200000001 00 C1A9";
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeWriteTimestampFunInv() {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("YKCV1_19271117778_000001_00DF");
        reply.functionId("WriteTimestampFunInv");

        reply.addInput("timestamp", DateUtils.fromYYYYMMDDHHmmss19("2020-03-16 17:14:47"));

        String expect = "68 12 00 DF 00 56 55 03 14 12 78 23 05 98 B7 0E 11 10 03 14 8A 13";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeWriteTimestampFunInvReply() throws DecoderException {
        String payload = "68 12 A101 00 55 55031412782305 98B70E11100314 0E9B";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeWriteBillingTermsFunInv() {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("55031412782305");
        reply.setMessageId("YKCV1_19271117778_000001_0025");
        reply.functionId("WriteBillingTermsFunInv");

        reply.addInput("timestamp", DateUtils.fromYYYYMMDDHHmmss19("2020-03-16 17:14:47"));

        reply.addInput("termsNo", "0100");
        reply.addInput("sharpEUP", 200000);
        reply.addInput("sharpSUP", 40000);
        reply.addInput("peakEUP", 200000);
        reply.addInput("peakSUP", 40000);
        reply.addInput("shoulderEUP", 200000);
        reply.addInput("shoulderSUP", 40000);
        reply.addInput("offPeakEUP", 200000);
        reply.addInput("offPeakSUP", 40000);
        reply.addInput("withLostRate", (byte) 0x00);

        String expect = "68 5E 00 25 00 58 55 03 14 12 78 23 05 01 00 40 0D 03 00 40 9C 00 00 40 0D 03 00 40 9C 00 00 40 0D 03 00 40 9C 00 00 40 0D 03 00 40 9C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FB 3B";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeWriteBillingTermsFunInvReply() throws DecoderException {
        String payload = "68 0C 0009 00 57 32010200000001 01 C1A9";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 0C 0009 00 57 32010200000001 00 C1A9";
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

    @Test
    public void encodeRebootFunInv() {
        FunctionInvokeMessage reply = new FunctionInvokeMessage();
        reply.setDeviceId("32010200000001");
        reply.setMessageId("YKCV1_19271117778_000001_0011");
        reply.functionId("RebootFunInv");

        reply.addInput("option", "RUN_AT_ONCE");

        String expect = "68 0C 00 11 00 92 32 01 02 00 00 00 01 01 D2 E7";

        ByteBuf rst = codec.encode(encodeCtx, reply);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        Assert.assertEquals(expect, real);
    }

    @Test
    public void decodeRebootFunInvReply() throws DecoderException {
        String payload = "68 0C 0011 00 91 32010200000001 01 C1A9";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, input);
        System.out.println(msg);

        payload = "68 0C 0011 00 91 32010200000001 00 C1A9";
        input = BytesUtils.fromHexStrWithTrim(payload);
        structInst = suit.deserialize(input);
        System.out.println(structInst);

        msg = codec.decode(decodeCtx, input);
        System.out.println(msg);
    }

}