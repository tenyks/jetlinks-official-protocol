package org.jetlinks.protocol.michong;

import io.netty.buffer.ByteBuf;
import me.tenyks.core.utils.ShortCodeGenerator;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.DeviceMessageDecoder;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.utils.BytesUtils;
import org.jetlinks.protocol.e53.E53IAxProtocolSupport;
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

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/30
 * @since V3.1.0
 */
public class MiChongProtocolTest {

    private StructSuit suit = MiChongV2ProtocolSupport.buildStructSuitV2();

    private BinaryMessageCodec codec = MiChongV2ProtocolSupport.buildBinaryMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void decodeReportProperties() throws DecoderException {
        String payload = "AA 16 21 01 02 01 01 00 01 00 02 00 03 02 01 00 04 00 05 00 06 18";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        /*
        CMD:0x21[SOP=170|0xAA,LEN=22|0x16,CMD=33|0x21,RESULT=1|0x1,PORT_NUM=2|0x2,
                port1PortNo=1|0x1,port1Status=1|0x1,port1RemainTime=1|0x1,port1WorkingPower=2|0x2,port1CurrentRoundEC=3|0x3,
                port2PortNo=2|0x2,port2Status=1|0x1,port2RemainTime=4|0x4,port2WorkingPower=5|0x5,port2CurrentRoundEC=6|0x6,
                SUM=24|0x18
            ]
         */

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{
        //	"messageType": "REPORT_PROPERTY",
        //	"deviceId": "devId-001",
        //	"properties": {
        //		"port2Status": "SOP_FREE",
        //		"port1CurrentRoundEC": 3,
        //		"port1RemainTime": 1,
        //		"port1StatusDesc": "端口空闲",
        //		"port2StatusDesc": "端口空闲",
        //		"port2RemainTime": 4,
        //		"port1WorkingPower": 2,
        //		"port1Status": "SOP_FREE",
        //		"port2WorkingPower": 5,
        //		"port2CurrentRoundEC": 6
        //	},
        //	"timestamp": 1719835267651
        //}
    }

    @Test
    public void decodeFaultOrRestoreEvent() throws DecoderException {
        String payload = "AA 05 0A 01 04 A2 18";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0A[SOP=170|0xAA,LEN=5|0x5,CMD=10|0xA,RESULT=1|0x1,portNo=4|0x4,faultCode=162|0xA2,SUM=24|0x18]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"data":{"portNo":4,"faultCode":"FC_LOW_TEMPERATURE","faultDesc":"低温异常"},"messageType":"EVENT","event":"FaultOrRestoreEvent","deviceId":"devId-001","timestamp":1719837665991}

        payload = "AA 05 0A 01 05 22 18";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0A[SOP=170|0xAA,LEN=5|0x5,CMD=10|0xA,RESULT=1|0x1,portNo=5|0x5,faultCode=34|0x22,SUM=24|0x18]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"data":{"portNo":5,"faultCode":"OK_LOW_TEMPERATURE","faultDesc":"低温恢复正常"},"messageType":"EVENT","event":"FaultOrRestoreEvent","deviceId":"devId-001","timestamp":1719837666147}
    }

    @Test
    public void decodePortRoundEndEvent() throws DecoderException {
        String payload = "AA 08 16 01 03 01 02 01 03 01 18";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x16[SOP=170|0xAA,LEN=8|0x8,CMD=22|0x16,RESULT=1|0x1,portNo=3|0x3,remainTime=258|0x102,ec=259|0x103,reasonCode=1|0x1,SUM=24|0x18]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"data":{"portNo":3,"remainTime":258,"reasonCode":"RC_MANUAL_STOP","ec":259,"reasonDesc":"手动停止：拔插头或禁止停止等"},"messageType":"EVENT","event":"PortRoundEndEvent","deviceId":"devId-001","timestamp":1719843997493}

        payload = "AA 08 16 01 04 01 02 01 03 00 18";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x16[SOP=170|0xAA,LEN=8|0x8,CMD=22|0x16,RESULT=1|0x1,portNo=4|0x4,remainTime=258|0x102,ec=259|0x103,reasonCode=0|0x0,SUM=24|0x18]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"data":{"portNo":4,"remainTime":258,"reasonCode":"RC_OUT_OF_TIME","ec":259,"reasonDesc":"达到最大用电时长"},"messageType":"EVENT","event":"PortRoundEndEvent","deviceId":"devId-001","timestamp":1719843997653}
    }

    @Test
    public void decodePingEvent() throws DecoderException {
        String payload = "AA 04 0B 01 01 05";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0B[SOP=170|0xAA,LEN=4|0x4,CMD=11|0xB,RESULT=1|0x1,void=1|0x1,SUM=5|0x5]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"data":{"void":1},"messageType":"EVENT","event":"PingEvent","deviceId":"devId-001","timestamp":1719922231799}
    }

    @Test
    public void encodeSwitchOnPortPower() throws DecoderException {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("SwitchOnPortPower");
        funInvMsg.addInput("portNo", (short)8);
        funInvMsg.addInput("availableTime", 259);
        funInvMsg.addInput("availableEC", 258);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String x = ByteUtils.toHexStrPretty(rst);
        System.out.println(x);
        //AA 0A 14 01 08 75 30 01 03 01 02 53

        String expected = "AA 0A 14 01 08 75 30 01 03 01 02 53";
        Assert.assertEquals(expected, x);
    }

    @Test
    public void decodeSwitchOnPortPowerReply() throws DecoderException {
        String payload = "AA 05 14 01 08 01 53";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x14[SOP=170|0xAA,LEN=5|0x5,CMD=20|0x14,RESULT=1|0x1,portNo=8|0x8,rstCode=1|0x1,SUM=83|0x53]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"portNo":8,"rstDesc":"命令施工成功且端口成功通电","rstCode":"SUCCESS"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719903301448}

        payload = "AA 05 14 01 08 0B 53";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x14[SOP=170|0xAA,LEN=5|0x5,CMD=20|0x14,RESULT=1|0x1,portNo=8|0x8,rstCode=11|0xB,SUM=83|0x53]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"portNo":8,"rstDesc":"命令施工结束但端口充电故障","rstCode":"FAIL_CHARGING_FAULT"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719903301610}

        payload = "AA 05 14 00 08 01 53";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x14[SOP=170|0xAA,LEN=5|0x5,CMD=20|0x14,RESULT=0|0x0,portNo=8|0x8,rstCode=1|0x1,SUM=83|0x53]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"portNo":8,"rstDesc":"命令下发失败","rstCode":"FAIL"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719903301610}

        payload = "AA 05 14 0C 08 0E 53";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x14[SOP=170|0xAA,LEN=5|0x5,CMD=20|0x14,RESULT=12|0xC,portNo=8|0x8,rstCode=1|0x1,SUM=83|0x53]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"portNo":8,"rstDesc":"其他原因命令下发失败(12)","rstCode":"FAIL_OTHER_12"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719903463920}
    }

    @Test
    public void encodeSwitchOffPortPower() throws DecoderException {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("SwitchOffPortPower");
        funInvMsg.addInput("portNo", (byte)131);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        //AA 05 0D 01 83 00 8A

        String expected = "AA 05 0D 01 83 00 8A";
        Assert.assertEquals(expected, real);

        funInvMsg = new FunctionInvokeMessage().functionId("SwitchOffPortPower");
        funInvMsg.addInput("portNo", (short)132);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        rst = codec.encode(encodeCtx, funInvMsg);
        real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);
        //AA 05 0D 01 83 00 8A

        expected = "AA 05 0D 01 84 00 8D";
        Assert.assertEquals(expected, real);
    }

    @Test
    public void decodeSwitchOffPortPowerReply() throws DecoderException {
        String payload = "AA 06 0D 01 02 01 E0 53";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0D[SOP=170|0xAA,LEN=6|0x6,CMD=13|0xD,RESULT=1|0x1,portNo=161|0xA1,remainTime=480|0x1E0,SUM=83|0x53]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令施工成功","portNo":161,"rstCode":"SUCCESS","remainTime":480},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719908739829}

        payload = "AA 06 0D 00 00 01 E1 54";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0D[SOP=170|0xAA,LEN=6|0x6,CMD=13|0xD,RESULT=0|0x0,portNo=0|0x0,remainTime=481|0x1E1,SUM=84|0x54]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令下发或施工失败","portNo":0,"rstCode":"FAIL","remainTime":481},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719908917761}
    }

    @Test
    public void encodeReadPortState() throws DecoderException {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("ReadPortState");
        funInvMsg.addInput("portNo", (byte)1);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        String expected = "AA 04 15 01 01 11";
        Assert.assertEquals(expected, real);

        funInvMsg = new FunctionInvokeMessage().functionId("ReadPortState");
        funInvMsg.addInput("portNo", (short)2);
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        rst = codec.encode(encodeCtx, funInvMsg);
        real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        expected = "AA 04 15 01 02 12";
        Assert.assertEquals(expected, real);
    }

    @Test
    public void decodeReadPortStateReply() throws DecoderException {
        String payload = "AA 0C 15 01 03 00 01 00 02 00 03 01 04 53";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x15[SOP=170|0xAA,LEN=12|0xC,CMD=21|0x15,RESULT=1|0x1,portNo=3|0x3,remainTime=1|0x1,workingPower=2|0x2,remainEC=3|0x3,remainMoney=260|0x104,SUM=83|0x53]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令施工成功","portNo":3,"rstCode":"SUCCESS","workingPower":2,"remainEC":3,"remainTime":1,"remainMoney":260},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719910750369}

        payload = "AA 0C 15 FF 03 00 01 00 02 00 03 01 04 53";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x15[SOP=170|0xAA,LEN=12|0xC,CMD=21|0x15,RESULT=255|0xFF,portNo=3|0x3,remainTime=1|0x1,workingPower=2|0x2,remainEC=3|0x3,remainMoney=260|0x104,SUM=83|0x53]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令下发失败：无网络/未链接","portNo":3,"rstCode":"FAIL_NET","workingPower":2,"remainEC":3,"remainTime":1,"remainMoney":260},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719910750522}
    }

    @Test
    public void encodeLockOrUnlockPort() throws DecoderException {
        FunctionInvokeMessage funInvMsg;
        funInvMsg = new FunctionInvokeMessage().functionId("LockOrUnlockPort");
        funInvMsg.addInput("portNo", (byte) 1);
        funInvMsg.addInput("flag", "LOCK");
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        ByteBuf rst = codec.encode(encodeCtx, funInvMsg);
        String real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        String expected = "AA 05 0C 01 01 00 09";
        Assert.assertEquals(expected, real);

        funInvMsg = new FunctionInvokeMessage().functionId("LockOrUnlockPort");
        funInvMsg.addInput("portNo", (byte) 2);
        funInvMsg.addInput("flag", "UNLOCK");
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        rst = codec.encode(encodeCtx, funInvMsg);
        real = ByteUtils.toHexStrPretty(rst);
        System.out.println(real);

        expected = "AA 05 0C 01 02 01 0B";
        Assert.assertEquals(expected, real);

        funInvMsg = new FunctionInvokeMessage().functionId("LockOrUnlockPort");
        funInvMsg.addInput("portNo", (byte) 1);
        funInvMsg.addInput("flag", "UNK");
        funInvMsg.setMessageId(ShortCodeGenerator.INSTANCE.next());

        rst = codec.encode(encodeCtx, funInvMsg);
        Assert.assertNull(rst);
    }

    @Test
    public void decodeLockOrUnlockPortReply() throws DecoderException {
        String payload = "AA 04 0C 01 04 09";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        StructInstance structInst;
        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0C[SOP=170|0xAA,LEN=4|0x4,CMD=12|0xC,RESULT=1|0x1,portNo=4|0x4,SUM=9|0x9]

        DeviceMessage devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令施工成功","portNo":4,"rstCode":"SUCCESS"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719921058863}

        payload = "AA 04 0C 00 04 09";
        input = BytesUtils.fromHexStrWithTrim(payload);

        structInst = suit.deserialize(input);
        System.out.println(structInst);
        //CMD:0x0C[SOP=170|0xAA,LEN=4|0x4,CMD=12|0xC,RESULT=0|0x0,portNo=4|0x4,SUM=9|0x9]

        devMsg = codec.decode(decodeCtx, input);
        System.out.println(devMsg);
        //{"output":{"rstDesc":"命令下发或施工失败","portNo":4,"rstCode":"FAIL"},"messageType":"INVOKE_FUNCTION_REPLY","success":true,"deviceId":"devId-001","timestamp":1719921149418}
    }

    @Test
    public void buildPongReply() {
        ByteBuf rst;

        rst = MiChongV2ProtocolSupport.buildPongReply(null, null);
        System.out.println(ByteUtils.toHexStrPretty(rst));
    }


}
