package org.jetlinks.protocol.michong;

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
 * 米充V2协议
 *
 * 消息报文字节结构:
 *
 * @author v-lizy81
 * @date 2024/3/20 21:42
 */
public class MiChongV2ProtocolSupport {

    public static final String NAME_AND_VER = "MI_CHONG_V2";

    public static DeviceMessageCodec buildDeviceMessageCodec(PluginConfig config) {
        IntercommunicateStrategy strategy = buildIntercommunicateStrategy(config);
        BinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);

        return new StructLwM2M11DeviceMessageCodec(bmCodec, strategy);
    }

    public static BinaryMessageCodec buildBinaryMessageCodec(PluginConfig config) {
        StructSuit structSuit = buildStructSuitV1();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedBinaryMessageCodec(structSuit, mapper);
    }

    public static IntercommunicateStrategy  buildIntercommunicateStrategy(PluginConfig config) {
        return new AbstractIntercommunicateStrategy() {};
    }

    public static StructSuit buildStructSuitV1() {
        StructSuit suit = new StructSuit(
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

    public static StructAndMessageMapper    buildMapper(StructSuit structSuit) {
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
     * 上报端口实时状态
     */
    private static DefaultStructDeclaration buildReportDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("设备每间隔30~60秒上传端口信息", "CMD:0x21");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)0x00));
        structDcl.addField(buildCMDFieldDcl((byte)0x10));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field = buildPortNumFieldDcl((byte)0x05);
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

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 开启给水指令，下行
     */
    private static DefaultStructDeclaration buildPumpInWaterOnStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启给水指令结构", "CMD:0x11");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PumpInWaterOn"));

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

        structDcl.addField(buildSOP());
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

    /**
     * 公共字段：帧头
     */
    private static DefaultFieldDeclaration buildSOP() {
        return new DefaultFieldDeclaration("公共：帧头", "SOP", BaseDataType.UINT8, (short)0)
                    .setDefaultValue((byte)0xAA)
                    .addValidValue((byte)0xAA, (byte)0x55); //下行时取值0xAA，上行时兼容：0xAA和0x55
    }

    /**
     * 公共字段：报文长度
     * @param sizeOfData DATA字段的长度，单位：字节
     */
    private static DefaultFieldDeclaration buildLENFieldDcl(byte sizeOfData) {
        return new DefaultFieldDeclaration("报文长度", "LEN", BaseDataType.UINT8, (short) 1)
                .setDefaultValue(sizeOfData + 3);
    }

    /**
     * 公共字段：命令
     */
    private static DefaultFieldDeclaration buildCMDFieldDcl(byte cmdCode) {
        return new DefaultFieldDeclaration("命令", "CMD", BaseDataType.UINT8, (short) 2)
                    .setDefaultValue(cmdCode);
    }

    /**
     * 公共字段：结果
     */
    private static DefaultFieldDeclaration buildRESULTFieldDcl(byte result) {
        return new DefaultFieldDeclaration("结果", "RESULT", BaseDataType.UINT8, (short) 3)
                .setDefaultValue(result);
    }

    /**
     * 公共字段：校验
     */
    private static DefaultFieldDeclaration buildSUMFieldDcl() {
        return new DefaultFieldDeclaration("异或校验", "SUM", BaseDataType.UINT8, (short) 3)
                .setDefaultValue((byte)0x00);
    }

    private static DefaultFieldDeclaration buildPortNumFieldDcl(short absOffset) {
        return new DefaultFieldDeclaration("设备端口数", "PORT_NUM", BaseDataType.UINT8, absOffset);
    }

    private static CRCCalculator    buildCRCCalculatorInst() {
        return new XORCRCCalculator(1, -1);
    }

    private static DefaultFieldDeclaration buildIOParamFieldDcl(DynamicAnchor nextToThisAnchor, String name,
                                                                String code, BaseDataType dataType) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(nextToThisAnchor, (short)0);
    }

    private static class E53IAxFeatureCodeExtractor implements FeatureCodeExtractor {
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
