package org.jetlinks.protocol.e53;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.official.binary2.*;

/**
 * E53版IA2协议
 *
 * 消息报文字节结构:
 * <li>MagicId：UINT16，0xFA11上线消息, 0xFA37下行消息</li>
 * <li>消息ID：UINT16，Big-End</li>
 * <li>消息编码：UINT8，参考物模型定义</li>
 * <li>输入/输出参数负载总字节数：UINT16</li>
 * <li>参数1：参考物模型定义</li>
 * <li>参数N：参考物模型定义</li>
 *
 * @author v-lizy81
 * @date 2024/3/20 21:42
 */
public class E53IAxProtocolSupport {

    public static final String NAME_OF_IA2 = "E53_IA2_V1.0.0";

    /**
     * 心跳上报结构【事件】：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildReportPongStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("数据上报结构【消息】", "CMD:0x10");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte) 21));
        structDcl.addField(buildCmdFieldDcl((byte)0x35));

        structDcl.addField(new DefaultFieldDeclaration("MAC码", "machineMAC", BaseDataType.STRING, (short) 8,  (short) 12)
                .addMeta(ThingAnnotation.EventData())
                .addMeta(ThingAnnotation.DeviceId()));

//        structDcl.addField(buildCRCFieldDcl((short) 21));

        return structDcl;
    }

    private static DefaultFieldDeclaration buildMagicFieldDcl() {
        return new DefaultFieldDeclaration("结构类型标识字段", "magicId", BaseDataType.UINT16, (short)0)
                    .setDefaultValue(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_DOWNLINK);
    }

    private static DefaultFieldDeclaration buildMessageIdFieldDcl() {
        return new DefaultFieldDeclaration("消息ID", "messageId", BaseDataType.UINT16, (short) 1)
                .addMeta(ThingAnnotation.MsgId());
    }

    private static DefaultFieldDeclaration buildMessageTypeFieldDcl(String thingId) {
        return new DefaultFieldDeclaration("消息编码", "messageType", BaseDataType.UINT8, (short) 1)
                .addMeta(ThingAnnotation.ServiceId(thingId));
    }

    private static DefaultFieldDeclaration buildIOParamsPayloadLengthFieldDcl(byte defaultValue) {
        return new DefaultFieldDeclaration("输入/输出参数负载总字节数", "packageLength", BaseDataType.UINT16, (short) 6)
                .setDefaultValue(defaultValue);
    }

    private static DefaultFieldDeclaration buildCmdFieldDcl(Byte defaultVal) {
        return new DefaultFieldDeclaration("CMD字段", "functionId", BaseDataType.INT8, (short) 7).setDefaultValue(defaultVal);
    }

    private static class E53IAxFeatureCodeExtractor implements FeatureCodeExtractor {
        private static final short MAGIC_ID_OF_UNLINK = (byte) 0xfa11;
        private static final short MAGIC_ID_OF_DOWNLINK = (byte) 0xfa37;

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[8];

            if (buf.readableBytes() < headerBuf.length) {
                return "WRONG_SIZE:" + Hex.encodeHexString(headerBuf);
            }
            buf.readBytes(headerBuf);

            short magId = (short)(((short) headerBuf[0]) << 8 | (short)headerBuf[1]);

            if (magId != MAGIC_ID_OF_UNLINK) {
                return "WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + Integer.toHexString(headerBuf[4]);
        }

        @Override
        public boolean isValidFeatureCode(String featureCode) {
            return (featureCode != null && featureCode.startsWith("CMD:"));
        }
    }

    private static class E53IAxEncodeSigner implements EncodeSigner {

        @Override
        public ByteBuf apply(ByteBuf buf) {
            int saveWriterIdx = buf.writerIndex();

            buf.writerIndex(0);
            buf.writeByte((byte)(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_DOWNLINK >> 8 & 0xFF));
            buf.writeByte((byte)(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_DOWNLINK & 0xFF));

            buf.writerIndex(saveWriterIdx);

            return buf;
        }

    }
}
