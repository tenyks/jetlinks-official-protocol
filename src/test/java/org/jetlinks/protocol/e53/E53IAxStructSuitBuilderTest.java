package org.jetlinks.protocol.e53;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.tenyks.core.utils.ShortCodeGenerator;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.DeviceMessageDecoder;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.function.*;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.TestMessageDecodeContext;
import org.jetlinks.protocol.official.TestMessageEncodeContext;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.binary2.StructSuit;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.util.context.Context;

import java.util.Properties;

/**
 * @author v-lizy81
 * @date 2024/3/28 22:48
 */
public class E53IAxStructSuitBuilderTest {

    private StructSuit suit = E53IAxProtocolSupport.buildStructSuitV1();

    private DeviceMessageDecoder decoder = E53IAxProtocolSupport.buildDeviceMessageCodec(new PluginConfig(new Properties()));

    private BinaryMessageCodec codec = E53IAxProtocolSupport.buildBinaryMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decodeReportProperties() throws DecoderException {
        String payload = "fa 11 01 02 10 00 0e 3f 8c cc cd 3f 99 99 9a 40 06 66 66 01 00".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(payload));

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x10[MessageId=258,humidity=1.2,highWaterMark=0,packageLength=14,lowWaterMark=1,MagicId=64017,temperature=1.1,luminance=2.1,MessageType=16]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"messageType":"REPORT_PROPERTY","messageId":"258","deviceId":"devId-001","properties":{"luminance":2.1,"temperature":1.1,"lowWaterMark":1,"humidity":1.2,"highWaterMark":0},"timestamp":1711782640801}
    }

    @Test
    public void encodePumpInWaterOn() {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("PumpInWaterOn");
        funInvMsg.addInput("degree", (byte)2);
        funInvMsg.addInput("duration", 66051);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String x = ByteUtils.toHexStr(rst);
        System.out.println(x);

        String expected = "fa 11 00 01 11 05 02 00 01 02 03".replace(" ", "");
        Assert.assertEquals(expected, x);
    }

    @Test
    public void encodePumpInWaterOff() {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("PumpInWaterOff");
        funInvMsg.addInput("degree", (byte)2);
        funInvMsg.addInput("duration", 66051);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String x = ByteUtils.toHexStr(rst);
        System.out.println(x);

        String expected = "fa 11 00 01 12 00".replace(" ", "");
        Assert.assertEquals(expected, x);
    }

    @Test
    public void encodePumpOutWaterOn() {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("PumpOutWaterOn");
        funInvMsg.addInput("degree", (byte)3);
        funInvMsg.addInput("duration", 66052);
        funInvMsg.addInput("autoStopAtLWM", (byte)1);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String x = ByteUtils.toHexStr(rst);
        System.out.println(x);

        String expected = "fa 11 00 01 13 06 03 00 01 02 04 01".replace(" ", "");
        Assert.assertEquals(expected, x);
    }

    @Test
    public void encodePumpOutWaterOff() {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("PumpOutWaterOff");
        funInvMsg.addInput("degree", (byte)3);
        funInvMsg.addInput("duration", 66052);
        funInvMsg.addInput("autoStopAtLWM", (byte)1);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String x = ByteUtils.toHexStr(rst);
        System.out.println(x);

        String expected = "fa 11 00 01 14 00".replace(" ", "");
        Assert.assertEquals(expected, x);
    }
}
