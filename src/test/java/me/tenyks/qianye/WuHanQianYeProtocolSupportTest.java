package me.tenyks.qianye;

import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionParameter;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.TestMessageDecodeContext;
import org.jetlinks.protocol.official.TestMessageEncodeContext;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.format.FormatMessageCodec;
import org.jetlinks.protocol.official.format.FormatStructSuit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Properties;

public class WuHanQianYeProtocolSupportTest {

    private FormatStructSuit suit = WuHanQianYeProtocolSupport.buildStructSuitV1();

    private FormatMessageCodec codec = WuHanQianYeProtocolSupport.buildFormatMessageCodec(new PluginConfig(new Properties()));

    private MessageDecodeContext decodeCtx = new TestMessageDecodeContext("devId-001", "dev-session-002");
    private MessageEncodeContext encodeCtx = new TestMessageEncodeContext("devId-001", "dev-session-002");

    @Test
    public void encodeCallOfDeviceInfoFunInv() throws DecoderException {
        FunctionInvokeMessage funInvMsg = new FunctionInvokeMessage();
        funInvMsg.setDeviceId("devId-001");
        funInvMsg.setFunctionId("CallOfDeviceInfoFunInv");
        funInvMsg.setInputs(new ArrayList<>());

        String msg = codec.encode(encodeCtx, funInvMsg);
        System.out.println(msg);
    }

    @Test
    public void decodeReportDeviceInfo() throws DecoderException {
        String payload = "{\"id\":\"123\",\"version\":\"1.0\",\"params\":{\"version\":\"3.4.0\",\"littlefs_version\":\"v1.3.2\",\"deviceId\":\"BCDDC2575959\",\"ssid\":\"HBJX\",\"ip\":\"192.168.0.109\",\"mac\":\"BC:DDC2:57:59:59\",\"relay\":0,\"mode\":6},\"method\":\"thing.deviceinfo.post\"}";

        StructInstance structInst;
        structInst = suit.deserialize(payload);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, payload);
        System.out.println(msg);
    }

    @Test
    public void decodeReportSocketState() throws DecoderException {
        String payload = "{\"id\":\"123\",\"version\":\"1.0\",\"params\":{\"Power\":{\"Vol\":230,\"Current\":5.35,\"ActiveP\":1230.5},\"status\":0},\"method\":\"thing.property.post\"}";

        StructInstance structInst;
        structInst = suit.deserialize(payload);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, payload);
        System.out.println(msg);
    }

    @Test
    public void encodeReadSocketStateFunInv() throws DecoderException {
        FunctionInvokeMessage funInvMsg = new FunctionInvokeMessage();
        funInvMsg.setDeviceId("devId-001");
        funInvMsg.setFunctionId("ReadSocketStateFunInv");
        funInvMsg.setInputs(new ArrayList<>());

        funInvMsg.getInputs().add(new FunctionParameter("option", "RELAY"));

        String msg = codec.encode(encodeCtx, funInvMsg);
        System.out.println(msg);
    }

    @Test
    public void decodeReadSocketStateFunInvReply() throws DecoderException {
        String payload = "{\"id\":\"123\",\"version\":\"1.0\",\"code\":200,\"message\":\"success\",\"data\":{\"Power\":{\"Vol\":230,\"Current\":5.35,\"ActiveP\":1230.5},\"Relay\":{\"value\":true}},\"method\":\"thing.property.get\"}";

        StructInstance structInst;
        structInst = suit.deserialize(payload);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, payload);
        System.out.println(msg);
    }

    @Test
    public void encodeSocketSwitchOnOffFunInv() throws DecoderException {
        FunctionInvokeMessage funInvMsg = new FunctionInvokeMessage();
        funInvMsg.setDeviceId("devId-001");
        funInvMsg.setFunctionId("SocketSwitchOnOffFunInv");
        funInvMsg.setInputs(new ArrayList<>());

        funInvMsg.getInputs().add(new FunctionParameter("option", "ON"));

        String msg = codec.encode(encodeCtx, funInvMsg);
        System.out.println(msg);
    }

    @Test
    public void decodeSocketSwitchOnOffFunInvReply() throws DecoderException {
        String payload = "{\"id\":\"123\",\"version\":\"1.0\",\"code\":200,\"message\":\"success\",\"data\":{\"Relay\":{\"value\":1}},\"method\":\"thing.property.set\"}";

        StructInstance structInst;
        structInst = suit.deserialize(payload);
        System.out.println(structInst);

        DeviceMessage msg = codec.decode(decodeCtx, payload);
        System.out.println(msg);
    }

}