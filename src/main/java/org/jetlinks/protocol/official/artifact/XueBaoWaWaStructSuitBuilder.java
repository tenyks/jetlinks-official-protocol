package org.jetlinks.protocol.official.artifact;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.official.binary2.*;

public class XueBaoWaWaStructSuitBuilder {

    public static StructSuit buildV26() {
        StructSuit suit = new StructSuit(
                "雪暴网络娃娃机协议",
                "2.6",
                "协议文件《网络娃娃机主板对接协议4.0》",
                new V26FeatureCodeExtractor()
        );

        suit.addStructDeclaration(buildStartGameStructDcl());
        suit.addStructDeclaration(buildGameOverStructDcl());
        suit.addStructDeclaration(buildCtrlMotorStructDcl());
        suit.addStructDeclaration(buildCheckOnlineStructDcl());
        suit.addStructDeclaration(buildCheckOnlineReplyStructDcl());
        suit.addStructDeclaration(buildReportMachineErrorStructDcl());
        suit.addStructDeclaration(buildPingStructDcl());
        suit.addStructDeclaration(buildReportPongStructDcl());
        suit.addStructDeclaration(buildReportPongStructDcl());

        suit.setDefaultACKStructDeclaration(buildACKDefaultStructDcl());

        return suit;
    }

    /**
     * 开局指令：服务器 -> 机器
     */
    private static DefaultStructDeclaration buildStartGameStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开局指令", "CMD:0x31");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableEncode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte)20));
        structDcl.addField(buildCmdFieldDcl((byte)0x31));

        structDcl.addField(new DefaultFieldDeclaration("超时时间", "timeOut", BaseDataType.UINT8, (short) 9));
        structDcl.addField(new DefaultFieldDeclaration("抓到结果", "result", BaseDataType.UINT8, (short) 10));
        structDcl.addField(new DefaultFieldDeclaration("抓起爪力", "pickUpCF", BaseDataType.UINT8, (short) 11));
        structDcl.addField(new DefaultFieldDeclaration("到顶爪力", "toTopCF", BaseDataType.UINT8, (short) 12));
        structDcl.addField(new DefaultFieldDeclaration("移动爪力", "moveCF", BaseDataType.UINT8, (short) 13));
        structDcl.addField(new DefaultFieldDeclaration("大爪力", "bigCF", BaseDataType.UINT8, (short) 14));
        structDcl.addField(new DefaultFieldDeclaration("抓起高度", "pickupHeight", BaseDataType.UINT8, (short) 15));
        structDcl.addField(new DefaultFieldDeclaration("下线长度", "letDownLength", BaseDataType.UINT8, (short) 16));
        structDcl.addField(new DefaultFieldDeclaration("前后电机的速度", "fbMotorSpeed", BaseDataType.UINT8, (short) 17));
        structDcl.addField(new DefaultFieldDeclaration("左右电机的速度", "lrMotorSpeed", BaseDataType.UINT8, (short) 18));
        structDcl.addField(new DefaultFieldDeclaration("上下电机的速度", "udMotorSpeed", BaseDataType.UINT8, (short) 19));

//        structDcl.addField(buildCRCFieldDcl((short) 20));

        return structDcl;
    }

    /**
     * 游戏结束返回命令：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildGameOverStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("游戏结束返回", "CMD:0x33");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte)12));
        structDcl.addField(buildCmdFieldDcl((byte)0x33));

        structDcl.addField(new DefaultFieldDeclaration("保留字节1", "reversed1", BaseDataType.UINT8, (short) 9));
        structDcl.addField(new DefaultFieldDeclaration("保留字节2", "reversed1", BaseDataType.UINT8, (short) 10));

//        structDcl.addField(buildCRCFieldDcl((short) 12));

        return structDcl;
    }

    /**
     * 控制电机命令：服务器 -> 机器
     */
    private static DefaultStructDeclaration buildCtrlMotorStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("控制电机命令", "CMD:0x32");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableEncode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte) 12));
        structDcl.addField(buildCmdFieldDcl((byte)0x32));

        structDcl.addField(new DefaultFieldDeclaration("运动动作", "action", BaseDataType.UINT8, (short) 9));
        structDcl.addField(new DefaultFieldDeclaration("时长（毫秒）", "duration", BaseDataType.UINT16, (short) 10));

//        structDcl.addField(buildCRCFieldDcl((short) 12));

        return structDcl;
    }

    /**
     * 查询机台是否上线命令：服务器 -> 机器
     */
    private static DefaultStructDeclaration buildCheckOnlineStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("查询机台是否上线命令", "CMD:0x34");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableEncode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte) 9));
        structDcl.addField(buildCmdFieldDcl((byte)0x34));

//        structDcl.addField(buildCRCFieldDcl((short) 9));

        return structDcl;
    }

    /**
     * 查询机台是否上线命令的返回：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildCheckOnlineReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("查询机台是否上线命令的返回", "CMD:0x34");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte)14));
        structDcl.addField(buildCmdFieldDcl((byte)0x34));

        structDcl.addField(new DefaultFieldDeclaration("机台状态", "machineStatus", BaseDataType.UINT8, (short) 9));
        structDcl.addField(new DefaultFieldDeclaration("抓起爪力", "pickupCF", BaseDataType.UINT8, (short) 10));
        structDcl.addField(new DefaultFieldDeclaration("到顶爪力", "toTopCF", BaseDataType.UINT8, (short) 11));
        structDcl.addField(new DefaultFieldDeclaration("移动爪力", "moveCF", BaseDataType.UINT8, (short) 12));
        structDcl.addField(new DefaultFieldDeclaration("大爪力", "bigCF", BaseDataType.UINT8, (short) 13));

//        structDcl.addField(buildCRCFieldDcl((short) 14));

        return structDcl;
    }

    /**
     * 错误上报：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildReportMachineErrorStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("错误上报", "CMD:0x37");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte)10));
        structDcl.addField(buildCmdFieldDcl((byte)0x37));

        structDcl.addField(new DefaultFieldDeclaration("机台错误代码", "machineErrorCode", BaseDataType.UINT8, (short) 9));

        structDcl.addField(buildCRCFieldDcl((short) 10));

        return structDcl;
    }

    /**
     * PING指令：服务器 -> 机器
     */
    private static DefaultStructDeclaration buildPingStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("PING心跳", "CMD:0x35");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte)9));
        structDcl.addField(buildCmdFieldDcl((byte)0x42));

//        structDcl.addField(buildCRCFieldDcl((short) 8));

        return structDcl;
    }

    /**
     * 心跳上报：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildReportPongStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("心跳上报", "CMD:0x35");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte) 21));
        structDcl.addField(buildCmdFieldDcl((byte)0x35));

        structDcl.addField(new DefaultFieldDeclaration("MAC码", "machineMAC", BaseDataType.HEX_STR, (short) 8,  (short) 12));

//        structDcl.addField(buildCRCFieldDcl((short) 21));

        return structDcl;
    }

    /**
     * 默认的ACK响应：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildACKDefaultStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("默认的ACK响应", "CMD:ACK_DEFAULT");
        structDcl.setCRCCalculator(buildCRCCalculatorInst());

        structDcl.enableDecode();
        structDcl.addField(buildMagicFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte) 0));
        structDcl.addField(buildCmdFieldDcl(null));

//        structDcl.addField(buildCRCFieldDcl((short) 21));

        return structDcl;
    }

    private static DefaultFieldDeclaration buildMagicFieldDcl() {
        return new DefaultFieldDeclaration("标识字段", "magicId", BaseDataType.UINT8, (short)0).setDefaultValue((byte)0xFE);
    }

    private static DefaultFieldDeclaration buildMessageIdFieldDcl() {
        return new DefaultFieldDeclaration("消息ID", "messageId", BaseDataType.UINT16, (short) 1);
    }

    private static DefaultFieldDeclaration buildPackageLengthFieldDcl(byte defaultValue) {
        return new DefaultFieldDeclaration("包长度", "packageLength", BaseDataType.UINT8, (short) 6)
                .setDefaultValue(defaultValue);
    }

    private static DefaultFieldDeclaration buildCmdFieldDcl(Byte defaultVal) {
        return new DefaultFieldDeclaration("CMD字段", "functionId", BaseDataType.UINT8, (short) 7).setDefaultValue(defaultVal);
    }

    private static DefaultFieldDeclaration buildCRCFieldDcl(short offset) {
        return new DefaultFieldDeclaration("CRC校验", "crc", BaseDataType.UINT8, offset);
    }

    private static CRCCalculator    buildCRCCalculatorInst() {
        return new SumAndModCRCCalculator(0, -1, 100);
    }

    private static class V26FeatureCodeExtractor implements FeatureCodeExtractor {

        private static final byte MAGIC_ID = (byte) 0xfe;

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[8];

            buf.readBytes(headerBuf);

            if (headerBuf[0] != MAGIC_ID) {
                return "WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            boolean flag = (headerBuf[0] == ~headerBuf[3] && headerBuf[1] == ~headerBuf[4] && headerBuf[2] == ~headerBuf[5]);
            if (!flag) {
                return "~NOT_EQ:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + Integer.toHexString(headerBuf[7]);
        }
    }

}
