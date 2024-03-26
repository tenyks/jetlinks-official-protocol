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
     * 数据上报【消息】，上行
     */
    private static DefaultStructDeclaration buildReportDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("数据上报结构【消息】", "CMD:0x10");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x10));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)14));

        structDcl.addField(buildIOParamFieldDcl("温度", "temperature", BaseDataType.FLOAT));
        structDcl.addField(buildIOParamFieldDcl("相对湿度", "humidity", BaseDataType.FLOAT));
        structDcl.addField(buildIOParamFieldDcl("亮度", "luminance", BaseDataType.FLOAT));
        structDcl.addField(buildIOParamFieldDcl("低水位标志", "lowWaterMark", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("高水位标志", "highWaterMark", BaseDataType.UINT8));

        return structDcl;
    }

    /**
     * 开启给水指令【指令】，下行
     */
    private static DefaultStructDeclaration buildPumpInWaterOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启给水指令【指令】", "CMD:0x11");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpInWaterOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x11));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停给水指令【指令】，下行
     */
    private static DefaultStructDeclaration buildPumpInWaterOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启给水指令【指令】", "CMD:0x12");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpInWaterOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x12));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启排水指令【指令】，下行
     */
    private static DefaultStructDeclaration buildPumpOutWaterOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启排水指令【指令】", "CMD:0x13");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpOutWaterOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x13));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停排水指令，下行
     */
    private static DefaultStructDeclaration buildPumpOutWaterOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停排水指令", "CMD:0x14");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpOutWaterOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x14));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启送风指令，下行
     */
    private static DefaultStructDeclaration buildFanInAirOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启送风指令", "CMD:0x15");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanInAirOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x15));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停送风指令，下行
     */
    private static DefaultStructDeclaration buildFanInAirOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停送风指令", "CMD:0x16");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanInAirOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x16));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启排风指令，下行
     */
    private static DefaultStructDeclaration buildFanOutAirOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启排风指令", "CMD:0x17");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanOutAirOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x17));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停排风指令，下行
     */
    private static DefaultStructDeclaration buildFanOutAirOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停排风指令", "CMD:0x18");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanOutAirOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x18));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启加热器A指令，下行
     */
    private static DefaultStructDeclaration buildHeaterAOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启加热器A指令", "CMD:0x19");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterAOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x19));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停加热器A指令，下行
     */
    private static DefaultStructDeclaration buildHeaterAOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停加热器A指令", "CMD:0x1A");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterAOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1A));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启加热器B指令，下行
     */
    private static DefaultStructDeclaration buildHeaterBOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启加热器B指令", "CMD:0x1B");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterBOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1B));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停加热器B指令，下行
     */
    private static DefaultStructDeclaration buildHeaterBOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停加热器B指令", "CMD:0x1C");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterBOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1C));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启补光指令，下行
     */
    private static DefaultStructDeclaration buildLightOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启补光指令", "CMD:0x1D");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LightOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1D));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)5));

        structDcl.addField(buildIOParamFieldDcl("工作挡位", "degree", BaseDataType.UINT8));
        structDcl.addField(buildIOParamFieldDcl("工作时长", "duration", BaseDataType.UINT32));

        return structDcl;
    }

    /**
     * 关停补光指令，下行
     */
    private static DefaultStructDeclaration buildLightOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停补光指令", "CMD:0x1E");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LightOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1E));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    private static DefaultFieldDeclaration buildMagicIdFieldDcl() {
        return new DefaultFieldDeclaration("协议及版本标识字段", "MagicId", BaseDataType.UINT16, (short)0)
                    .setDefaultValue(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2);
    }

    private static DefaultFieldDeclaration buildMessageIdFieldDcl() {
        return new DefaultFieldDeclaration("消息ID", "MessageId", BaseDataType.UINT16, (short) 2)
                .addMeta(ThingAnnotation.MsgId());
    }

    private static DefaultFieldDeclaration buildMessageTypeFieldDcl(byte typeCode) {
        return new DefaultFieldDeclaration("消息编码", "MessageType", BaseDataType.UINT8, (short) 5)
                    .setDefaultValue(typeCode);
    }

    private static DefaultFieldDeclaration buildIOParamsPayloadLengthFieldDcl(byte defaultValue) {
        return new DefaultFieldDeclaration("输入/输出参数负载总字节数", "packageLength", BaseDataType.UINT16, (short) 6)
                .setDefaultValue(defaultValue);
    }

    private static DefaultFieldDeclaration buildIOParamFieldDcl(String name, String code, BaseDataType dataType) {
        return new DefaultFieldDeclaration(name, code, dataType);
    }

    private static class E53IAxFeatureCodeExtractor implements FeatureCodeExtractor {
        private static final short MAGIC_ID_OF_IA2 = (byte) 0xfa11;

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[8];

            if (buf.readableBytes() < headerBuf.length) {
                return "WRONG_SIZE:" + Hex.encodeHexString(headerBuf);
            }
            buf.readBytes(headerBuf);

            short magId = (short)(((short) headerBuf[0]) << 8 | (short)headerBuf[1]);

            if (magId != MAGIC_ID_OF_IA2) {
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
            buf.writeByte((byte)(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2 >> 8 & 0xFF));
            buf.writeByte((byte)(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2 & 0xFF));

            buf.writerIndex(saveWriterIdx);

            return buf;
        }

    }
}
