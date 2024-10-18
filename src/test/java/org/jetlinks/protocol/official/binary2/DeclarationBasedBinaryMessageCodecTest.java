package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.protocol.official.TestMessageDecodeContext;
import org.jetlinks.protocol.official.TestMessageEncodeContext;
import me.tenyks.xuebao.XueBaoWaWaProtocolSupport;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Before;
import org.junit.Test;

public class DeclarationBasedBinaryMessageCodecTest {

    private BinaryMessageCodec codec;

    @Before
    public void setUp() {
        codec = XueBaoWaWaProtocolSupport.buildBinaryMessageCodec(null);
    }

    @Test
    public void decode() throws DecoderException {
        DeviceMessage rst;
        TestMessageDecodeContext context = new TestMessageDecodeContext("D001", "SD001_002");

        String cas1 = "fe 00 01 01 ff fe 0c 33 01 00 00 3e".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(cas1));

        rst = codec.decode(context, input);
        System.out.println(rst.toJson());

        String case2 = "fe 00 02 01 ff fd a5 35 00 01 00 02 00 03 00 04 00 05 00 06 3e".replace(" ", "");
        input = Unpooled.wrappedBuffer(Hex.decodeHex(case2));
        rst = codec.decode(context, input);
        System.out.println(rst.toJson());
    }

    @Test
    public void decodeStartGameReply() throws DecoderException {
        DeviceMessage rst;
        TestMessageDecodeContext context = new TestMessageDecodeContext("D001", "SD001_002");

        String cas1 = "fe000201fffd14313c000000000000000000001d";
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(cas1));

        rst = codec.decode(context, input);
        System.out.println(rst.toJson());

    }

    @Test
    public void decodeGameOverEvent() throws DecoderException {
        DeviceMessage rst;
        TestMessageDecodeContext context = new TestMessageDecodeContext("D001", "SD001_002");

        String cas1 = "fe400001bfff0c330000003f";
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(cas1));

        rst = codec.decode(context, input);
        System.out.println(rst.toJson());

    }

    @Test
    public void encode() {
        TestMessageEncodeContext context = new TestMessageEncodeContext("D001", "SD001_002");
        ByteBuf rst;

        FunctionInvokeMessage startGameMsg = new FunctionInvokeMessage();
        startGameMsg.setFunctionId("startGame");
        startGameMsg.setMessageId("M001");

        startGameMsg.addInput("timeOut", (byte)1);
        startGameMsg.addInput("result", false);
        startGameMsg.addInput("pickUpCF", 2);
        startGameMsg.addInput("toTopCF", (short)3);
        startGameMsg.addInput("moveCF", (short)4);
        startGameMsg.addInput("bigCF", (short)5);
        startGameMsg.addInput("pickupHeight", (short)6);
        startGameMsg.addInput("letDownLength", (short)0);
        startGameMsg.addInput("fbMotorSpeed", (short)3);
        startGameMsg.addInput("lrMotorSpeed", (short)2);
        startGameMsg.addInput("udMotorSpeed", (short)1);

        rst = codec.encode(context, startGameMsg);
        ByteUtils.toHexStr(rst);
        System.out.println(ByteUtils.toHexStr(rst));

        startGameMsg = new FunctionInvokeMessage();
        startGameMsg.setFunctionId("startGame");
        startGameMsg.setMessageId("M002");
        startGameMsg.addInput("timeOut", (byte)2);
        startGameMsg.addInput("result", (short)1);
        startGameMsg.addInput("pickUpCF", (short)3);
        startGameMsg.addInput("toTopCF", (short)4);
        startGameMsg.addInput("moveCF", (short)5);
        startGameMsg.addInput("bigCF", (short)6);
        startGameMsg.addInput("pickupHeight", (short)5);
        startGameMsg.addInput("letDownLength", (short)4);
        startGameMsg.addInput("fbMotorSpeed", (short)3);
        startGameMsg.addInput("lrMotorSpeed", (short)2);
        startGameMsg.addInput("udMotorSpeed", (short)1);

        rst = codec.encode(context, startGameMsg);
        System.out.println(ByteUtils.toHexStr(rst));
    }


}