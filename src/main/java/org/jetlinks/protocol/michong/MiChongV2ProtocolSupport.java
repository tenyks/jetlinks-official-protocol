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

    private static final short  DATA_BEGIN_IDX = 4;

    public static DeviceMessageCodec buildDeviceMessageCodec(PluginConfig config) {
        IntercommunicateStrategy strategy = buildIntercommunicateStrategy(config);
        BinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);

        return new StructLwM2M11DeviceMessageCodec(bmCodec, strategy);
    }

    public static BinaryMessageCodec buildBinaryMessageCodec(PluginConfig config) {
        StructSuit structSuit = buildStructSuitV2();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedBinaryMessageCodec(structSuit, mapper);
    }

    public static IntercommunicateStrategy  buildIntercommunicateStrategy(PluginConfig config) {
        return new AbstractIntercommunicateStrategy() {};
    }

    public static StructSuit buildStructSuitV2() {
        StructSuit suit = new StructSuit(
                "米充充电桩协议",
                "V2.0",
                "document-mqtt-MiChong.md",
                new MiChongFeatureCodeExtractor()
        );

        suit.addStructDeclaration(buildReportDataStructDcl());

        suit.addStructDeclaration(buildFaultEventStructDcl());
        suit.addStructDeclaration(buildFaultRestoreEventStructDcl());
        suit.addStructDeclaration(buildPortRoundEndEventStructDcl());

        suit.addStructDeclaration(buildSwitchOnPortPowerStructDcl());
        suit.addStructDeclaration(buildSwitchOnPortPowerResponseStructDcl());

        suit.addStructDeclaration(buildSwitchOffPortPowerStructDcl());
        suit.addStructDeclaration(buildSwitchOffPortPowerResponseStructDcl());

        suit.addStructDeclaration(buildLockOrUnlockPortStructDcl());
        suit.addStructDeclaration(buildLockOrUnlockPortResponseStructDcl());

        suit.addStructDeclaration(buildReadPortStateStructDcl());
        suit.addStructDeclaration(buildReadPortStateResponseStructDcl());

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
     * [数据上报] 上报端口实时状态
     */
    private static DefaultStructDeclaration buildReportDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("设备每间隔30~60秒上传端口信息", "CMD:0x21");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)0x00));
        structDcl.addField(buildCMDFieldDcl((byte)0x10));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration portNumFieldDcl = buildPortNumFieldDcl(DATA_BEGIN_IDX);
        structDcl.addField(portNumFieldDcl);

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("端口X的状况", "portXState", (short)6, (short)8);
        groupDcl.setDynamicNRepeat(portNumFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(portNumFieldDcl.asAnchor(), (short)0);

        DynamicAnchor anchor = groupDcl.asAnchor();

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, anchor, (short)0);
        groupDcl.addIncludedField(field);

        field = buildDataFieldDcl("端口状态", "Status", BaseDataType.UINT8, anchor, (short)1);
        groupDcl.addIncludedField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("剩余用电时长", "RemainTime", BaseDataType.UINT16, anchor, (short)3);
        groupDcl.addIncludedField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("当前用电功率", "WorkingPower", BaseDataType.UINT16, anchor, (short)5);
        groupDcl.addIncludedField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("本轮用电电量", "CurrentRoundEC", BaseDataType.UINT16, anchor, (short)7);
        groupDcl.addIncludedField(field.addMeta(ThingAnnotation.Property()));

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * [事件上报] 设备故障事件
     */
    private static DefaultStructDeclaration buildFaultEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("设备上传机器故障码给服务器", "CMD:0x0A");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FaultEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)0x06));
        structDcl.addField(buildCMDFieldDcl((byte)0x0A));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("错误码", "errorCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * [事件上报] 设备故障恢复事件
     */
    private static DefaultStructDeclaration buildFaultRestoreEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("设备上传机器故障码给服务器", "CMD:0x0A");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FaultEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)0x06));
        structDcl.addField(buildCMDFieldDcl((byte)0x0A));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 端口当轮用电结束事件
     */
    private static DefaultStructDeclaration buildPortRoundEndEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("端口用电结束事件", "CMD:0x16");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FaultEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)6));
        structDcl.addField(buildCMDFieldDcl((byte)0x16));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.EventData()));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.EventData()));

        field = buildDataFieldDcl("本轮用电电量", "ec", BaseDataType.UINT8,  (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field.addMeta(ThingAnnotation.EventData()));

        field = buildDataFieldDcl("停止的原因编码", "reasonCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 5));
        structDcl.addField(field.addMeta(ThingAnnotation.EventData()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 开启端口供电指令
     */
    private static DefaultStructDeclaration buildSwitchOnPortPowerStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启端口供电指令", "CMD:0x14");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOnPortPower"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)7));
        structDcl.addField(buildCMDFieldDcl((byte)0x14));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field);

        field = buildDataFieldDcl("可用金额", "maxMoney", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.setDefaultValue((short) 30000));

        field = buildDataFieldDcl("可用电时长", "maxTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field);

        field = buildDataFieldDcl("可用电电量", "maxEC", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 5));
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 开启端口供电指令回复
     */
    private static DefaultStructDeclaration buildSwitchOnPortPowerResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启端口供电指令回复", "CMD:0x14");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOnPortPowerResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)2));
        structDcl.addField(buildCMDFieldDcl((byte)0x14));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        field = buildDataFieldDcl("结果编码", "rstCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 关停端口供电指令
     */
    private static DefaultStructDeclaration buildSwitchOffPortPowerStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("端口关停供电指令", "CMD:0x0D");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOffPortPower"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)2));
        structDcl.addField(buildCMDFieldDcl((byte)0x0D));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field);

        field = buildDataFieldDcl("类型", "type", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.setDefaultValue((byte)0x00));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 关停端口供电指令回复
     */
    private static DefaultStructDeclaration buildSwitchOffPortPowerResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停端口供电指令回复", "CMD:0x0D");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOffPortPowerResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)3));
        structDcl.addField(buildCMDFieldDcl((byte)0x0D));
        structDcl.addField(buildRESULTFieldDcl((byte)0x00));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 查询指定端口的状况的指令
     */
    private static DefaultStructDeclaration buildReadPortStateStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("查询指定端口的状况的指令", "CMD:0x15");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReadPortState"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)1));
        structDcl.addField(buildCMDFieldDcl((byte)0x15));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 查询指定端口的状况的指令响应
     */
    private static DefaultStructDeclaration buildReadPortStateResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("查询指定端口的状况的指令响应", "CMD:0x15");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReadPortStateResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)9));
        structDcl.addField(buildCMDFieldDcl((byte)0x15));
        structDcl.addField(buildRESULTFieldDcl((byte)0x00));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("当前用电功率", "workingPower", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("本轮用电剩余电量", "remainEC", BaseDataType.UINT16,  (short)(DATA_BEGIN_IDX + 53));
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        field = buildDataFieldDcl("本轮用电剩余金额", "remainMoney", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(field.addMeta(ThingAnnotation.Property()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 锁定/解锁指定端口命令
     */
    private static DefaultStructDeclaration buildLockOrUnlockPortStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("锁定/解锁指定端口命令", "CMD:0x0C");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LockOrUnlockPort"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte)2));
        structDcl.addField(buildCMDFieldDcl((byte)0x0C));
        structDcl.addField(buildRESULTFieldDcl((byte)0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field);

        field = buildDataFieldDcl("控制标志", "flag", BaseDataType.UINT8, (short) (DATA_BEGIN_IDX + 1));
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 锁定/解锁指定端口命令响应
     */
    private static DefaultStructDeclaration buildLockOrUnlockPortResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("锁定/解锁指定端口命令响应", "CMD:0x0C");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LockOrUnlockPortResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 1));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0C));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput()));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

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
        return new DefaultFieldDeclaration("异或校验", "SUM", BaseDataType.UINT8, (short) -1)
                .setDefaultValue((byte)0x00);
    }

    private static DefaultFieldDeclaration buildPortNumFieldDcl(short absOffset) {
        return new DefaultFieldDeclaration("设备端口数", "PORT_NUM", BaseDataType.UINT8, absOffset);
    }

    private static CRCCalculator    buildCRCCalculatorInst() {
        return new XORCRCCalculator(1, -1);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor nextToThisAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(nextToThisAnchor, (short)0);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType, short absOffset) {
        return new DefaultFieldDeclaration(name, code, dataType, absOffset);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor anchor, short offsetToAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(anchor, offsetToAnchor);
    }

    private static CRCCalculator buildCRCCalculator() {
        return new XORCRCCalculator(1, -1);
    }

    private static class MiChongFeatureCodeExtractor implements FeatureCodeExtractor {
        private static final byte MAGIC_ID_OF_V2_1 = (byte) 0xAA;
        private static final byte[] MAGIC_ID_OF_V2_1_DOUBLE_HEX = new byte[]{(byte) 0x41, (byte) 0x41};

        private static final byte MAGIC_ID_OF_V2_2 = (byte) 0x55;
        private static final byte[] MAGIC_ID_OF_V2_2_DOUBLE_HEX = new byte[]{(byte) 0x35, (byte) 0x35};


        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[6];

            buf.readerIndex(0);
            if (buf.readableBytes() < headerBuf.length) {
                return "[MiChong]WRONG_SIZE:" + Hex.encodeHexString(buf.array());
            }
            buf.readBytes(headerBuf);

            if (headerBuf[0] != MAGIC_ID_OF_V2_1 && headerBuf[0] != MAGIC_ID_OF_V2_2) {
                return "[MiChong]WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + Integer.toHexString(0x000000ff & headerBuf[2]).toUpperCase();
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

            return (headerBuf[0] == MAGIC_ID_OF_V2_1_DOUBLE_HEX[0] && headerBuf[1] == MAGIC_ID_OF_V2_1_DOUBLE_HEX[1])
                    ||
                    (headerBuf[0] == MAGIC_ID_OF_V2_2_DOUBLE_HEX[0] && headerBuf[1] == MAGIC_ID_OF_V2_2_DOUBLE_HEX[1]);
        }
    }

    private static class MiChongEncodeSigner implements EncodeSigner {

        private final CRCCalculator crcCalculator = buildCRCCalculator();

        @Override
        public ByteBuf apply(ByteBuf buf) {
            int saveReaderIdx = buf.readerIndex();
            int saveWriterIdx = buf.writerIndex();
            int sumIdx = buf.readableBytes() - 1;

            int crc = crcCalculator.apply(buf);
            buf.writerIndex(sumIdx);
            buf.writeByte(crc);

            buf.writerIndex(saveWriterIdx);
            buf.readerIndex(saveReaderIdx);

            return buf;
        }

    }
}
