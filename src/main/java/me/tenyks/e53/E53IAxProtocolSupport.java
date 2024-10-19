package me.tenyks.e53;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.codec.DeviceMessageCodec;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractIntercommunicateStrategy;
import org.jetlinks.protocol.official.common.IntercommunicateStrategy;
import org.jetlinks.protocol.official.lwm2m.StructLwM2M11DeviceMessageCodec;

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

    public static DeviceMessageCodec buildDeviceMessageCodec(PluginConfig config) {
        IntercommunicateStrategy strategy = buildIntercommunicateStrategy(config);
        BinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);

        return new StructLwM2M11DeviceMessageCodec(bmCodec, strategy);
    }

    public static BinaryMessageCodec buildBinaryMessageCodec(PluginConfig config) {
        BinaryStructSuit structSuit = buildStructSuitV1();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedBinaryMessageCodec(structSuit, mapper);
    }

    public static IntercommunicateStrategy  buildIntercommunicateStrategy(PluginConfig config) {
        return new AbstractIntercommunicateStrategy() {};
    }

    public static BinaryStructSuit buildStructSuitV1() {
        BinaryStructSuit suit = new BinaryStructSuit(
                "E53版IA2协议",
                "1.0",
                "document-coap-e53.md",
                new E53IAxFeatureCodeExtractor()
        );

        suit.addStructDeclaration(buildReportDataStructDcl());

        suit.addStructDeclaration(buildPumpInWaterOnStructDcl());
        suit.addStructDeclaration(buildPumpInWaterOffStructDcl());

        suit.addStructDeclaration(buildPumpOutWaterOnStructDcl());
        suit.addStructDeclaration(buildPumpOutWaterOffStructDcl());

        suit.addStructDeclaration(buildFanInAirOnStructDcl());
        suit.addStructDeclaration(buildFanInAirOffStructDcl());

        suit.addStructDeclaration(buildFanOutAirOnStructDcl());
        suit.addStructDeclaration(buildFanOutAirOffStructDcl());

        suit.addStructDeclaration(buildHeaterAOnStructDcl());
        suit.addStructDeclaration(buildHeaterAOffStructDcl());

        suit.addStructDeclaration(buildHeaterBOnStructDcl());
        suit.addStructDeclaration(buildHeaterBOffStructDcl());

        suit.addStructDeclaration(buildLightOnStructDcl());
        suit.addStructDeclaration(buildLightOffStructDcl());

        suit.addStructDeclaration(buildFunInvReplyDcl());

        return suit;
    }

    public static StructAndMessageMapper    buildMapper(BinaryStructSuit structSuit) {
        DefaultStructAndThingMapping structAndThingMapping = new DefaultStructAndThingMapping();

        //Encode
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "PumpInWaterOn", structSuit.getStructDeclaration("开启给水指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "PumpInWaterOff", structSuit.getStructDeclaration("关停给水指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "PumpOutWaterOn", structSuit.getStructDeclaration("开启排水指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "PumpOutWaterOff", structSuit.getStructDeclaration("关停排水指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "FanInAirOn", structSuit.getStructDeclaration("开启送风指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "FanInAirOff", structSuit.getStructDeclaration("关停送风指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "FanOutAirOn", structSuit.getStructDeclaration("开启排风指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "FanOutAirOff", structSuit.getStructDeclaration("关停排风指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "HeaterAOn", structSuit.getStructDeclaration("开启加热器A指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "HeaterAOff", structSuit.getStructDeclaration("关停加热器A指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "HeaterBOn", structSuit.getStructDeclaration("开启加热器B指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "HeaterBOff", structSuit.getStructDeclaration("关停加热器B指令结构"));

        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "LightOn", structSuit.getStructDeclaration("开启补光指令结构"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "LightOff", structSuit.getStructDeclaration("关停补光指令结构"));

        //Decode
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("数据上报消息结构"), ReportPropertyMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("指令下发反馈消息结构"), FunctionInvokeMessageReply.class);

        for (StructDeclaration structDcl : structSuit.structDeclarations()) {
            if (!structDcl.getName().endsWith("回复结构")) continue;
            structAndThingMapping.addMapping(structDcl, FunctionInvokeMessageReply.class);
        }

        DefaultFieldAndPropertyMapping fieldAndPropertyMapping = new DefaultFieldAndPropertyMapping();
        DefaultFieldValueAndPropertyMapping fieldValueAndPropertyMapping = new DefaultFieldValueAndPropertyMapping();

        return new SimpleStructAndMessageMapper(structAndThingMapping, fieldAndPropertyMapping, fieldValueAndPropertyMapping);
    }

    /**
     * 数据上报消息，上行
     */
    private static DefaultStructDeclaration buildReportDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("数据上报消息结构", "CMD:0x10");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x10));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)16);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "温度", "temperature", BaseDataType.FLOAT);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildIOParamFieldDcl(field.asAnchor(), "相对湿度", "humidity", BaseDataType.FLOAT);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildIOParamFieldDcl(field.asAnchor(), "亮度", "luminance", BaseDataType.FLOAT);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildIOParamFieldDcl(field.asAnchor(), "低水位标志", "lowWaterMark", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildIOParamFieldDcl(field.asAnchor(), "高水位标志", "highWaterMark", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildIOParamFieldDcl(field.asAnchor(), "外设工作标志", "workingFlag", BaseDataType.UINT16);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        return structDcl;
    }

    /**
     * 开启给水指令，下行
     */
    private static DefaultStructDeclaration buildPumpInWaterOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启给水指令结构", "CMD:0x11");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpInWaterOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x11));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停给水指令，下行
     */
    private static DefaultStructDeclaration buildPumpInWaterOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停给水指令结构", "CMD:0x12");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpInWaterOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x12));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 开启排水指令，下行
     */
    private static DefaultStructDeclaration buildPumpOutWaterOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启排水指令结构", "CMD:0x13");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpOutWaterOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x13));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)6);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "低水位时自动停止", "autoStopAtLWM", BaseDataType.UINT8).setDefaultValue(0);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停排水指令，下行
     */
    private static DefaultStructDeclaration buildPumpOutWaterOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停排水指令结构", "CMD:0x14");

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启送风指令结构", "CMD:0x15");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanInAirOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x15));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停送风指令，下行
     */
    private static DefaultStructDeclaration buildFanInAirOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停送风指令结构", "CMD:0x16");

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启排风指令结构", "CMD:0x17");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FanOutAirOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x17));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停排风指令，下行
     */
    private static DefaultStructDeclaration buildFanOutAirOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停排风指令结构", "CMD:0x18");

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启加热器A指令结构", "CMD:0x19");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterAOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x19));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停加热器A指令，下行
     */
    private static DefaultStructDeclaration buildHeaterAOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停加热器A指令结构", "CMD:0x1A");

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启加热器B指令结构", "CMD:0x1B");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeaterBOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1B));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停加热器B指令，下行
     */
    private static DefaultStructDeclaration buildHeaterBOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停加热器B指令结构", "CMD:0x1C");

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启补光指令结构", "CMD:0x1D");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LightOn"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1D));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)5);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "工作挡位", "degree", BaseDataType.UINT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));
        field = buildIOParamFieldDcl(field.asAnchor(), "工作时长", "duration", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 关停补光指令，下行
     */
    private static DefaultStructDeclaration buildLightOffStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停补光指令结构", "CMD:0x1E");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LightOff"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0x1E));
        structDcl.addField(buildIOParamsPayloadLengthFieldDcl((byte)0));

        return structDcl;
    }

    /**
     * 指令下发反馈消息，上行
     */
    private static DefaultStructDeclaration buildFunInvReplyDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("指令下发反馈消息结构", "CMD:0xF0");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FunInvReply"));

        structDcl.addField(buildMagicIdFieldDcl());
        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildMessageTypeFieldDcl((byte)0xF0));

        DefaultFieldDeclaration field = buildIOParamsPayloadLengthFieldDcl((byte)6);
        structDcl.addField(field);

        field = buildIOParamFieldDcl(field.asAnchor(), "结果编码", "rstCode", BaseDataType.INT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        field = buildIOParamFieldDcl(field.asAnchor(), "指令的编码", "cmdCode", BaseDataType.INT8);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        field = buildIOParamFieldDcl(field.asAnchor(), "附加信息", "extInfo", BaseDataType.UINT32);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        return structDcl;
    }

    private static DefaultFieldDeclaration buildMagicIdFieldDcl() {
        return new DefaultFieldDeclaration("协议及版本标识字段", "MagicId", BaseDataType.UINT16, (short)0)
                    .setDefaultValue(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2_V1);
    }

    private static DefaultFieldDeclaration buildMessageIdFieldDcl() {
        return new DefaultFieldDeclaration("消息ID", "MessageId", BaseDataType.UINT16, (short) 2)
                .addMeta(ThingAnnotation.MsgIdUint16());
    }

    private static DefaultFieldDeclaration buildMessageTypeFieldDcl(byte typeCode) {
        return new DefaultFieldDeclaration("消息编码", "MessageType", BaseDataType.UINT8, (short) 4)
                    .setDefaultValue(typeCode);
    }

    private static DefaultFieldDeclaration buildIOParamsPayloadLengthFieldDcl(byte defaultValue) {
        return new DefaultFieldDeclaration("输入/输出参数负载总字节数", "paramsPayloadLength", BaseDataType.UINT16, (short) 5)
                .setDefaultValue(defaultValue);
    }

    private static DefaultFieldDeclaration buildIOParamFieldDcl(DynamicAnchor nextToThisAnchor, String name,
                                                                String code, BaseDataType dataType) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(nextToThisAnchor, (short)0);
    }

    private static class E53IAxFeatureCodeExtractor implements BinaryFeatureCodeExtractor {
        private static final short  MAGIC_ID_OF_IA2_V1 = (short)0xfa11;
        private static final byte[] MAGIC_ID_OF_IA2_V1_HEX = new byte[]{(byte) 0xfa, (byte) 0x11};
        private static final byte[] MAGIC_ID_OF_IA2_V1_DOUBLE_HEX = new byte[]{
                (byte) 0x66, (byte) 0x61, (byte)0x31, (byte)0x31
        };

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[7];

            buf.readerIndex(0);
            if (buf.readableBytes() < headerBuf.length) {
                return "WRONG_SIZE:" + Hex.encodeHexString(buf.array());
            }
            buf.readBytes(headerBuf);

            if (headerBuf[0] != MAGIC_ID_OF_IA2_V1_HEX[0] && headerBuf[1] != MAGIC_ID_OF_IA2_V1_HEX[1]) {
                return "WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + Integer.toHexString(0x000000ff & headerBuf[4]).toUpperCase();
        }

        @Override
        public boolean isValidFeatureCode(String featureCode) {
            return (featureCode != null && featureCode.startsWith("CMD:"));
        }

        @Override
        public boolean isDoubleHex(ByteBuf buf) {
            byte[] headerBuf = new byte[4];
            if (buf.readableBytes() < headerBuf.length) return false;

            buf.readBytes(headerBuf);

            return (
                    headerBuf[0] == MAGIC_ID_OF_IA2_V1_DOUBLE_HEX[0] &&
                    headerBuf[1] == MAGIC_ID_OF_IA2_V1_DOUBLE_HEX[1] &&
                    headerBuf[2] == MAGIC_ID_OF_IA2_V1_DOUBLE_HEX[2] &&
                    headerBuf[3] == MAGIC_ID_OF_IA2_V1_DOUBLE_HEX[3]
            );
        }
    }

    private static class E53IAxEncodeSigner implements EncodeSigner {

        @Override
        public ByteBuf apply(ByteBuf buf) {
            int saveWriterIdx = buf.writerIndex();

            buf.writerIndex(0);
            buf.writeByte(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2_V1_HEX[0]);
            buf.writeByte(E53IAxFeatureCodeExtractor.MAGIC_ID_OF_IA2_V1_HEX[1]);

            buf.writerIndex(saveWriterIdx);

            return buf;
        }

    }
}
