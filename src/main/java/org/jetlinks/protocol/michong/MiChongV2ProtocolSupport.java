package org.jetlinks.protocol.michong;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.protocol.common.FunctionHandler;
import org.jetlinks.protocol.common.SimpleFunctionHandler;
import org.jetlinks.protocol.common.mapping.*;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractIntercommunicateStrategy;
import org.jetlinks.protocol.official.common.DictBook;
import org.jetlinks.protocol.official.common.IntercommunicateStrategy;
import org.jetlinks.protocol.qiyun.mqtt.QiYunOverMqttDeviceMessageCodec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 米充V2协议
 *
 * 消息报文字节结构:
 *
 * @author v-lizy81
 * @date 2024/3/20 21:42
 */
public class MiChongV2ProtocolSupport {

    public static final String      NAME_AND_VER = "MI_CHONG_V2.0.0";

    private static final short      DATA_BEGIN_IDX = 4;

    private static final int        MAX_TIME = 30000;

    private static final short      MAX_MONEY = 30000;

    private static final String     CODE_OF_CMD_FIELD = "CMD";

    /**
     * 命令是否成功字典
     */
    private static final DictBook<Short, String> CMD_RESULT_DICT = new DictBook<>();

    private static final DictBook<Short, String> CMD_REPLY_RESULT_DICT = new DictBook<>();

    /**
     * 端口状态字典
     */
    private static final DictBook<Short, String> STATUS_OF_PORT_DICT = new DictBook<>();

    /**
     * 开启端口通电指令结果
     */
    private static final DictBook<Short, String> RESULT_OF_SWITCH_ON_PORT_CMD = new DictBook<>();

    /**
     * 用电结束原因
     */
    private static final DictBook<Short, String> REASON_OF_ROUND_END_CMD = new DictBook<>();

    /**
     * 故障码或故障恢复码字典
     */
    private static final DictBook<Short, String> FAULT_CODE_DICT = new DictBook<>();

    private static final MiChongEncodeSigner    Signer = new MiChongEncodeSigner();

    private static final ThingValueNormalization<Integer> NormToInt = ThingValueNormalizations.ofToInt(-1);


    static {
        CMD_RESULT_DICT.add((short) 0x01, "SUCCESS", "命令下发成功", true);
        CMD_RESULT_DICT.add((short) 0x00, "FAIL_CMD", "命令下发失败");
        CMD_RESULT_DICT.add((short) 0xFF, "FAIL_NET", "命令下发失败：无网络/未链接");
        CMD_RESULT_DICT.addOtherItemTemplate((srcCode) -> "FAIL_OTHER_" + srcCode.toString(), "其他原因下发命令失败");

        CMD_REPLY_RESULT_DICT.add((short) 0x01, "SUCCESS", "命令施工成功", true);
        CMD_REPLY_RESULT_DICT.add((short) 0x00, "FAIL", "命令下发或施工失败");
        CMD_REPLY_RESULT_DICT.add((short) 0xFF, "FAIL_NET", "命令下发失败：无网络/未链接");
        CMD_REPLY_RESULT_DICT.addOtherItemTemplate((srcCode) -> "FAIL_OTHER_" + srcCode.toString(), "其他原因命令下发或施工失败");

        STATUS_OF_PORT_DICT.add((short) 0x01, "SOP_FREE", "端口空闲");
        STATUS_OF_PORT_DICT.add((short) 0x02, "SOP_FAIL_OCCUPIED", "端口使用中");
        STATUS_OF_PORT_DICT.add((short) 0x03, "SOP_FAIL_LOCKED", "端口已禁用");
        STATUS_OF_PORT_DICT.add((short) 0x04, "SOP_FAIL_FAULT", "端口故障");
        STATUS_OF_PORT_DICT.addOtherItemTemplate((srcCode) -> "SOP_UNKNOWN" + srcCode.toString(), "端口状态未知");

        RESULT_OF_SWITCH_ON_PORT_CMD.add((short) 0x01, "SUCCESS", "命令施工成功且端口成功通电");
        RESULT_OF_SWITCH_ON_PORT_CMD.add((short) 0x0B, "FAIL_CHARGING_FAULT", "命令施工失败：端口充电故障");
        RESULT_OF_SWITCH_ON_PORT_CMD.add((short) 0x0C, "FAIL_OCCUPIED", "命令施工失败：端口已被使用");
        RESULT_OF_SWITCH_ON_PORT_CMD.addOtherItemTemplate((srcCode) -> "FAIL_OTHER_" + srcCode.toString(), "其他原因命令施工失败");

        REASON_OF_ROUND_END_CMD.add((short) 0x00, "RC_OUT_OF_TIME", "达到最大用电时长");
        REASON_OF_ROUND_END_CMD.add((short) 0x01, "RC_MANUAL_STOP", "手动停止：拔插头或紧急停止等");
        REASON_OF_ROUND_END_CMD.add((short) 0x02, "RC_AUTO_STOP_CHARGE_FULL", "自动停止：充电已满");
        REASON_OF_ROUND_END_CMD.add((short) 0x03, "RC_AUTO_STOP_OVER_POWER", "自动停止：超出限定功率");
        REASON_OF_ROUND_END_CMD.add((short) 0x04, "RC_REMOTE_STOP", "远程停止");
        REASON_OF_ROUND_END_CMD.add((short) 0x0B, "RC_STOP_BY_FAULT", "故障原因停止：设备故障或端口故障等");
        REASON_OF_ROUND_END_CMD.addOtherItemTemplate((srcCode) -> "RC_STOP_BY_OTHER" + srcCode.toString(), "其他原因停止");

        FAULT_CODE_DICT.add((short) 0xA0, "FC_STOP_FAULT", "停止充电异常：继电器粘连、短路等");
        FAULT_CODE_DICT.add((short) 0x20, "OK_STOP_FAULT", "停止充电恢复正常");
        FAULT_CODE_DICT.add((short) 0xA1, "FC_HIGH_TEMPERATURE", "高温异常");
        FAULT_CODE_DICT.add((short) 0x21, "OK_HIGH_TEMPERATURE", "高温恢复正常");
        FAULT_CODE_DICT.add((short) 0xA2, "FC_LOW_TEMPERATURE", "低温异常");
        FAULT_CODE_DICT.add((short) 0x22, "OK_LOW_TEMPERATURE", "低温恢复正常");
        FAULT_CODE_DICT.add((short) 0xA3, "FC_IDLE_LOAD", "空载异常：充电头脱落、拔出等");
        FAULT_CODE_DICT.add((short) 0x23, "OK_IDLE_LOAD", "负载恢复正常");
        FAULT_CODE_DICT.add((short) 0xA4, "FC_FIRE_ALARM", "消防报警：烟感等");
        FAULT_CODE_DICT.add((short) 0x24, "OK_FIRE_ALARM", "消防报警恢复正常");
        FAULT_CODE_DICT.add((short) 0xA5, "FC_OVER_LOAD", "过载异常：端口或总功率");
        FAULT_CODE_DICT.add((short) 0x25, "OK_OVER_LOAD", "过载恢复正常");
        FAULT_CODE_DICT.addOtherItemBuilder(srcCode -> {
            if (srcCode > (short) 0xA0) {
                return new DictBook.Item<>(srcCode, "FC_OTHER_" + srcCode.toString(),
                        "其他故障" + srcCode.toString());
            } else {
                return new DictBook.Item<>(srcCode, "OK_OTHER_" + srcCode.toString(),
                        srcCode.toString() + "故障恢复正常");
            }
        });
    }

    public static QiYunOverMqttDeviceMessageCodec buildDeviceMessageCodec(PluginConfig config) {
//        IntercommunicateStrategy strategy = buildIntercommunicateStrategy(config);
        BinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);

        return new QiYunOverMqttDeviceMessageCodec(DefaultTransport.MQTT, config.getMQTTManufacturer(),
                bmCodec, buildFunctionHandler());
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
        suit.addStructDeclaration(buildFaultOrRestoreEventStructDcl());
        suit.addStructDeclaration(buildPortRoundEndEventStructDcl());
        suit.addStructDeclaration(buildPingEventStructDcl());

        suit.addStructDeclaration(buildSwitchOnPortPowerStructDcl());
        suit.addStructDeclaration(buildSwitchOnPortPowerReplyStructDcl());

        suit.addStructDeclaration(buildSwitchOffPortPowerStructDcl());
        suit.addStructDeclaration(buildSwitchOffPortPowerReplyStructDcl());

        suit.addStructDeclaration(buildLockOrUnlockPortStructDcl());
        suit.addStructDeclaration(buildLockOrUnlockPortReplyStructDcl());

        suit.addStructDeclaration(buildReadPortStateStructDcl());
        suit.addStructDeclaration(buildReadPortStateReplyStructDcl());

        suit.setSigner(new MiChongEncodeSigner());

        return suit;
    }

    public static FunctionHandler buildFunctionHandler() {
        return new SimpleFunctionHandler()
                .addCallable("PingEvent", MiChongV2ProtocolSupport::buildPongReply);
    }

    public static StructAndMessageMapper    buildMapper(StructSuit structSuit) {
        DefaultStructAndThingMapping structAndThingMapping = new DefaultStructAndThingMapping();

        //上行消息：Decode
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("数据上报消息：上报端口实时状态"), ReportPropertyMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("设备故障或恢复事件"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("端口当轮用电结束事件"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("Ping事件"), EventMessage.class);

        MessageIdMappingAnnotation msgIdMappingAnn = new AbstractMessageIdMappingAnnotation.OfFunction(
                structInst -> structInst.getFieldStringValueWithDef(CODE_OF_CMD_FIELD, "NO_CMD_FIELD")
        );

        //下行指令：Encode
        DefaultStructDeclaration targetStructDcl = (DefaultStructDeclaration)structSuit.getStructDeclaration("开启端口供电指令");
        targetStructDcl.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "SwitchOnPortPower", targetStructDcl);

        targetStructDcl = (DefaultStructDeclaration) structSuit.getStructDeclaration("关停端口供电指令");
        targetStructDcl.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "SwitchOffPortPower", targetStructDcl);

        targetStructDcl = (DefaultStructDeclaration) structSuit.getStructDeclaration("读端口状况指令");
        targetStructDcl.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "ReadPortState", targetStructDcl);

        targetStructDcl = (DefaultStructDeclaration) structSuit.getStructDeclaration("锁定或解锁指定端口指令");
        targetStructDcl.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "LockOrUnlockPort", targetStructDcl);

        //指令响应：Decode
        for (StructDeclaration structDcl : structSuit.structDeclarations()) {
            if (!structDcl.getName().endsWith("指令响应")) continue;

            ((DefaultStructDeclaration) structDcl).addMetaAnnotation(msgIdMappingAnn);
            structAndThingMapping.addMapping(structDcl, FunctionInvokeMessageReply.class);
        }

        DefaultFieldAndPropertyMapping fieldAndPropertyMapping = new DefaultFieldAndPropertyMapping();
        DefaultFieldValueAndPropertyMapping fieldValueAndPropertyMapping = new DefaultFieldValueAndPropertyMapping();

        return new SimpleStructAndMessageMapper(structAndThingMapping, fieldAndPropertyMapping, fieldValueAndPropertyMapping);
    }

    /**
     * 数据上报消息：上报端口实时状态
     */
    private static DefaultStructDeclaration buildReportDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("数据上报消息：上报端口实时状态", "CMD:0x21");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 0));
        structDcl.addField(buildCMDFieldDcl((byte) 0x10));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration portNumFieldDcl;
        portNumFieldDcl = new DefaultFieldDeclaration("设备端口数", "portNum", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(portNumFieldDcl.addMeta(ThingAnnotation.Property(NormToInt)));

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("端口X的状况", "portXState", (short)5, (short)8);
        groupDcl.setDynamicNRepeat(portNumFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(portNumFieldDcl.asAnchor(), (short) 0);
        groupDcl.setInstancePostProcessor((idx, fieldInstances) -> {
            int portNo = 0;
            for (FieldInstance fInst : fieldInstances) {
                if (fInst.getCode().equals("PortNo")) {
                    portNo = fInst.getIntValue();
                }
            }

            String prefix = String.format("port%d", portNo);
            fieldInstances.forEach(v -> ((AbstractFieldInstance) v).setCodePrefix(prefix));

            return fieldInstances;
        });

        DynamicAnchor anchor = groupDcl.asAnchor();

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "PortNo", BaseDataType.UINT8, anchor, (short)0);
        groupDcl.addIncludedField(field);

        ThingItemMapping<String> portStatusMapping = ThingItemMappings.ofDictExtendPostfix(STATUS_OF_PORT_DICT, "Desc");
        field = buildDataFieldDcl("端口状态", "Status", BaseDataType.UINT8, anchor, (short)1);
        field.addMeta(ThingAnnotation.Property(portStatusMapping));
        groupDcl.addIncludedField(field);

        field = buildDataFieldDcl("当轮用电剩余时长", "RemainTime", BaseDataType.UINT16, anchor, (short)2);
        field.addMeta(ThingAnnotation.Property());
        groupDcl.addIncludedField(field);

        field = buildDataFieldDcl("当前用电功率", "WorkingPower", BaseDataType.UINT16, anchor, (short)4);
        field.addMeta(ThingAnnotation.Property());
        groupDcl.addIncludedField(field);

        field = buildDataFieldDcl("当轮用电电量", "CurrentRoundEC", BaseDataType.UINT16, anchor, (short)6);
        field.addMeta(ThingAnnotation.Property());
        groupDcl.addIncludedField(field);

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * [事件上报] 设备故障或恢复事件
     */
    private static DefaultStructDeclaration buildFaultOrRestoreEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("设备故障或恢复事件", "CMD:0x0A");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FaultOrRestoreEvent"));//TODO 根据条件

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 5));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0A));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        field.addMeta(ThingAnnotation.EventData(NormToInt));
        structDcl.addField(field);

        field = buildDataFieldDcl("故障码", "faultCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        field.addMeta(ThingAnnotation.EventData(
                ThingItemMappings.ofDictExtend(FAULT_CODE_DICT, "faultDesc")
        ));
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * Ping事件，需返回系统的时间戳
     */
    private static DefaultStructDeclaration buildPingEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("Ping事件", "CMD:0x0B");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PingEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 1));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0A));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("占位参数", "void", BaseDataType.UINT8, DATA_BEGIN_IDX);
        field.addMeta(ThingAnnotation.EventData());
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    public static ByteBuf   buildPongReply(@Nullable EncodedMessage srcMsg, @Nullable DeviceMessage thingMsg) {
        byte[] buf = new byte[] {
                (byte)0xAA, (byte)0x04, (byte)0x0B, (byte)0x01,
                (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04,
                (byte)0x00
        };

        ByteBuf rst = Unpooled.wrappedBuffer(buf);
        rst.writerIndex(DATA_BEGIN_IDX);

        BaseDataType.UINT32.write(rst, (int) (System.currentTimeMillis() / 1000));
        Signer.apply(rst);

        return rst;
    }

    /**
     * 端口当轮用电结束事件
     */
    private static DefaultStructDeclaration buildPortRoundEndEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("端口当轮用电结束事件", "CMD:0x16");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PortRoundEndEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 6));
        structDcl.addField(buildCMDFieldDcl((byte) 0x16));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.EventData(NormToInt)));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.EventData(NormToInt)));

        field = buildDataFieldDcl("本轮用电电量", "ec", BaseDataType.UINT16,  (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field.addMeta(ThingAnnotation.EventData(NormToInt)));

        field = buildDataFieldDcl("停止的原因编码", "reasonCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 5));
        field.addMeta(ThingAnnotation.EventData(
            ThingItemMappings.ofDictExtend(REASON_OF_ROUND_END_CMD, "reasonDesc")
        ));
        structDcl.addField(field);

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
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildCMDFieldDcl((byte) 0x14));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        field = buildDataFieldDcl("可用金额", "availableMoney", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()).setDefaultValue(MAX_MONEY));

        field = buildDataFieldDcl("可用电时长", "availableTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        field = buildDataFieldDcl("可用电电量", "availableEC", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 5));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 开启端口供电指令响应
     */
    private static DefaultStructDeclaration buildSwitchOnPortPowerReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("开启端口供电指令响应", "CMD:0x14");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOnPortPowerReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 2));
        structDcl.addField(buildCMDFieldDcl((byte) 0x14));
        DefaultFieldDeclaration cmdRstField = buildRESULTFieldDcl((byte) 0x00);
        structDcl.addField(cmdRstField);

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("结果编码", "rstCode", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        field.addMeta(ThingAnnotation.FuncOutput(ThingItemMappings.ofWithPreconditionDictExtend(
                        cmdRstField, CMD_RESULT_DICT, RESULT_OF_SWITCH_ON_PORT_CMD, "rstDesc")));
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 关停端口供电指令
     */
    private static DefaultStructDeclaration buildSwitchOffPortPowerStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停端口供电指令", "CMD:0x0D");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOffPortPower"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 2));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0D));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        field = buildDataFieldDcl("类型", "type", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.setDefaultValue((byte) 0x00));

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 关停端口供电指令响应
     */
    private static DefaultStructDeclaration buildSwitchOffPortPowerReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("关停端口供电指令响应", "CMD:0x0D");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("SwitchOffPortPowerReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 3));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0D));
        structDcl.addField(buildRESULTOfReplyFieldDcl());

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        structDcl.addField(buildSUMFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 读端口状况指令
     */
    private static DefaultStructDeclaration buildReadPortStateStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读端口状况指令", "CMD:0x15");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReadPortState"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 1));
        structDcl.addField(buildCMDFieldDcl((byte) 0x15));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 读端口状况的指令响应
     */
    private static DefaultStructDeclaration buildReadPortStateReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读端口状况指令响应", "CMD:0x15");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReadPortStateReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildCMDFieldDcl((byte) 0x15));
        structDcl.addField(buildRESULTOfReplyFieldDcl());

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("本轮用电剩余时长", "remainTime", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 1));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("当前用电功率", "workingPower", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 3));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("本轮用电剩余电量", "remainEC", BaseDataType.UINT16,  (short)(DATA_BEGIN_IDX + 5));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        field = buildDataFieldDcl("本轮用电剩余金额", "remainMoney", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 锁定或解锁指定端口指令
     */
    private static DefaultStructDeclaration buildLockOrUnlockPortStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("锁定或解锁指定端口指令", "CMD:0x0C");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LockOrUnlockPort"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 2));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0C));
        structDcl.addField(buildRESULTFieldDcl((byte) 0x01));

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncInput()));

        field = buildDataFieldDcl("控制标志", "flag", BaseDataType.UINT8, (short) (DATA_BEGIN_IDX + 1));
        field.addMeta(ThingAnnotation.FuncInput(itemValue -> {
            if ("LOCK".equals(itemValue)) {
                return (byte) 0x00;
            } else if ("UNLOCK".equals(itemValue)) {
                return (byte) 0x01;
            } else {
                return null;
            }
        }).setRequired(true));
        structDcl.addField(field);

        structDcl.addField(buildSUMFieldDcl());

        return structDcl;
    }

    /**
     * 锁定或解锁指定端口指令响应
     */
    private static DefaultStructDeclaration buildLockOrUnlockPortReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("锁定或解锁指定端口指令响应", "CMD:0x0C");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("LockOrUnlockPortReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 1));
        structDcl.addField(buildCMDFieldDcl((byte) 0x0C));
        structDcl.addField(buildRESULTOfReplyFieldDcl());

        DefaultFieldDeclaration field;
        field = buildDataFieldDcl("端口号", "portNo", BaseDataType.UINT8, DATA_BEGIN_IDX);
        structDcl.addField(field.addMeta(ThingAnnotation.FuncOutput(NormToInt)));

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
        return new DefaultFieldDeclaration("命令", CODE_OF_CMD_FIELD, BaseDataType.UINT8, (short) 2)
                    .setDefaultValue(cmdCode);
    }

    /**
     * 公共字段：结果
     */
    private static DefaultFieldDeclaration buildRESULTFieldDcl(byte result) {
        return new DefaultFieldDeclaration("结果", "RESULT", BaseDataType.UINT8, (short) 3)
                .setDefaultValue(result);
    }

    private static DefaultFieldDeclaration buildRESULTOfReplyFieldDcl() {
        DefaultFieldDeclaration field = new DefaultFieldDeclaration("结果", "RESULT", BaseDataType.UINT8, (short) 3);
        field.addMeta(ThingAnnotation.FuncOutput(ThingItemMappings.ofDictExtend2(CMD_REPLY_RESULT_DICT, "rstCode", "rstDesc")));

        return field;
    }

    /**
     * 公共字段：校验
     */
    private static DefaultFieldDeclaration buildSUMFieldDcl() {
        return new DefaultFieldDeclaration("异或校验", "SUM", BaseDataType.UINT8, (short)-1)
                .setDefaultValue((byte) 0x00);
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

    private static CRCCalculator    buildCRCCalculator() {
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

            return "CMD:0x" + String.format("%02X", 0x000000ff & headerBuf[2]);
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

            int crc = crcCalculator.apply(buf);

            buf.writerIndex(saveWriterIdx - 1);
            buf.writeByte((byte)crc);

            buf.writerIndex(saveWriterIdx);
            buf.readerIndex(saveReaderIdx);

            return buf;
        }

    }
}
