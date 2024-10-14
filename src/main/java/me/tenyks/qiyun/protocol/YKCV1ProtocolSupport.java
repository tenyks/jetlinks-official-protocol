package me.tenyks.qiyun.protocol;

import io.netty.buffer.ByteBuf;
import me.tenyks.core.crc.CRC180DCRCCalculator;
import me.tenyks.core.crc.CRCCalculator;
import me.tenyks.qiyun.tcp.QiYunStrategyBaseTcpDeviceMessageCodec;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessageReply;
import org.jetlinks.protocol.common.MessageIdReverseMapping;
import org.jetlinks.protocol.common.SelfEmbedMessageIdReverseMappingShort;
import org.jetlinks.protocol.common.mapping.*;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractIntercommunicateStrategy;

/**
 * 云快充新能源汽车充电桩协议
 *
 * 参考：《充电桩与云快充服务平台交互协议》，版本V1.6
 *
 * @author v-lizy81
 * @date 2024/9/9 22:20
 */
public class YKCV1ProtocolSupport {

    public static final String      NAME_AND_VER = "YKC_V1.6";

    private static final short      DATA_BEGIN_IDX = 6;

    private static final String     CODE_OF_MSG_TYPE_FIELD = "MSG_TYPE";

    private static final String     CODE_OF_MSG_NO_FIELD = "MSG_NO";

    private static final String     CODE_OF_ENCY_FLAG_FIELD = "ENCY_FLAG";

    private static final MessageIdReverseMapping<Short>     MsgIdMapping = new SelfEmbedMessageIdReverseMappingShort("YKCV1");

    private static final ThingValueNormalization<Integer>   NormToInt = ThingValueNormalizations.ofToInt(-1);
    private static final ThingValueNormalization<Short>     NormToShort = ThingValueNormalizations.ofToShort((short) 0);

    public static QiYunStrategyBaseTcpDeviceMessageCodec    buildDeviceMessageCodec(PluginConfig config) {
        AbstractIntercommunicateStrategy strategy = buildIntercommunicateStrategy(config);
        DeclarationBasedBinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);
        strategy.setReplyResponder(new YKCV1ReplyResponderBuilder().build(bmCodec.getStructSuit()));

        return new QiYunStrategyBaseTcpDeviceMessageCodec(bmCodec, strategy);
    }

    public static DeclarationBasedBinaryMessageCodec        buildBinaryMessageCodec(PluginConfig config) {
        StructSuit structSuit = buildStructSuitV1();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedBinaryMessageCodec(structSuit, mapper);
    }

    public static StructSuit buildStructSuitV1() {
        StructSuit suit = new StructSuit(
                "云快充新能源汽车充电桩协议",
                "V1.6",
                "document-mqtt-YKCV1.md",
                new YKCV1FeatureCodeExtractor()
        );

        suit.addStructDeclaration(buildAuthRequestStructDcl());
        suit.addStructDeclaration(buildAuthResponseStructDcl());

        suit.addStructDeclaration(buildHeartBeatPingStructDcl());
        suit.addStructDeclaration(buildHeartBeatPongStructDcl());

        suit.addStructDeclaration(buildCheckFeeTermsRequestStructDcl());
        suit.addStructDeclaration(buildCheckFeeTermsRequestReplyStructDcl());

        suit.addStructDeclaration(buildBillingTermsRequestStructDcl());
        suit.addStructDeclaration(buildBillingTermsRequestReplyStructDcl());

        suit.addStructDeclaration(buildCallOfRealTimeMonitorDataStructDcl());
        suit.addStructDeclaration(buildReportRealTimeMonitorDataStructDcl());

        suit.addStructDeclaration(buildReportTransOrderStructDcl());
        suit.addStructDeclaration(buildReportTransOrderAckStructDcl());

        suit.addStructDeclaration(buildReportChargingHandshakeDataStructDcl());
        suit.addStructDeclaration(buildReportChargingSettingWithBMSStructDcl());
        suit.addStructDeclaration(buildReportChargingFinishEventStructDcl());
        suit.addStructDeclaration(buildReportErrorEventStructDcl());
        suit.addStructDeclaration(buildReportChargerStopEventStructDcl());
        suit.addStructDeclaration(buildReportBMSStopEventStructDcl());
        suit.addStructDeclaration(buildReportBMSRequirementAndChargerOutputDataStructDcl());
        suit.addStructDeclaration(buildReportBMSChargingDataStructDcl());


        suit.addStructDeclaration(buildPileSwitchOnChargingRequestStructDcl());
        suit.addStructDeclaration(buildPileSwitchOnChargingRequestReplyStructDcl());

        suit.addStructDeclaration(buildSwitchOnChargingFunInvStructDcl());
        suit.addStructDeclaration(buildSwitchOnChargingFunInvReplyStructDcl());

        suit.addStructDeclaration(buildSwitchOffChargingStructDcl());
        suit.addStructDeclaration(buildPileSwitchOffFunInvReplyStructDcl());

        suit.addStructDeclaration(buildWriteCardBalanceFunInvStructDcl());
        suit.addStructDeclaration(buildUpdateAccountBalanceFunInvReplyStructDcl());

        suit.addStructDeclaration(buildAddOrUpdateCardFunInvStructDcl());
        suit.addStructDeclaration(buildWriteICDataFunInvReplyStructDcl());

        suit.addStructDeclaration(buildCleanICDataFunInvStructDcl());
        suit.addStructDeclaration(buildClearICDataFunInvReplyStructDcl());

        suit.addStructDeclaration(buildWritePileSettingFunInvStructDcl());
        suit.addStructDeclaration(buildWritePileSettingFunInvReplyStructDcl());

        suit.addStructDeclaration(buildWriteTimestampFunInvStructDcl());
        suit.addStructDeclaration(buildWriteTimestampFunInvReplyStructDcl());

        suit.addStructDeclaration(buildWriteBillingTermsStructDcl());
        suit.addStructDeclaration(buildWriteBillingTermsReplyStructDcl());

        suit.addStructDeclaration(buildReportParkLockDataStructDcl());

        suit.addStructDeclaration(buildParkLockControlFunInvStructDcl());
        suit.addStructDeclaration(buildParkLockControlFunInvReplyStructDcl());

        suit.addStructDeclaration(buildRebootFunInvStructDcl());
        suit.addStructDeclaration(buildRebootFunInvReplyStructDcl());

        suit.addStructDeclaration(buildCallOfOTAFunInvStructDcl());
        suit.addStructDeclaration(buildCallOfOTAFunInvReplyStructDcl());

//        suit.setSigner(new YKCV1EncodeSigner());

        return suit;
    }

    public static AbstractIntercommunicateStrategy          buildIntercommunicateStrategy(PluginConfig config) {
        return new AbstractIntercommunicateStrategy() {}
        .setRequestHandler(new YKCV1APIBuilder().build());
    }

    public static StructAndMessageMapper        buildMapper(StructSuit structSuit) {
        DefaultStructAndThingMapping structAndThingMapping = new DefaultStructAndThingMapping();

//        MessageIdMappingAnnotation msgIdMappingAnn = new AbstractMessageIdMappingAnnotation.OfFunction(
//                structInst -> structInst.getFieldStringValueWithDef(CODE_OF_MSG_TYPE_FIELD, "NO_CMD_FIELD")
//        );

        DefaultStructDeclaration target;

        // 充电桩登陆认证相关
        //TODO 临时处理，应该在TCPGateway处实现
        target = (DefaultStructDeclaration)structSuit.getStructDeclaration("充电桩登录认证消息[上行]");
//        target.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(target, DefaultDeviceRequestMessage.class);
        target = (DefaultStructDeclaration)structSuit.getStructDeclaration("充电桩登录认证应答[下行]");
//        target.addMetaAnnotation(msgIdMappingAnn);
        structAndThingMapping.addMapping(DefaultDeviceRequestMessageReply.class, "AuthResponse", target);

        // 心跳包
        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("充电桩心跳包[上行]");
        structAndThingMapping.addMapping(target, EventMessage.class);
        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("心跳包应答[下行]");
        structAndThingMapping.addMapping(AcknowledgeDeviceMessage.class, "HeartBeatPong", target);

        // 计费相关
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("计费模型验证请求[上行]"), DefaultDeviceRequestMessage.class);
        structAndThingMapping.addMapping(DefaultDeviceRequestMessageReply.class, "CheckFeeTermsRequestReply", structSuit.getStructDeclaration("计费模型验证请求应答[下行]"));

        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电桩计费模型请求[上行]"), DefaultDeviceRequestMessage.class);
        structAndThingMapping.addMapping(DefaultDeviceRequestMessageReply.class, "BillingTermsRequestReply", structSuit.getStructDeclaration("计费模型请求应答[下行]"));

        // 数据上报相关
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "CallOfRealTimeMonitorData", structSuit.getStructDeclaration("读取实时监测数据[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("上传实时监测数据[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电握手[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("参数配置[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电过程BMS需求与充电机输出[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电过程BMS信息[上行]"), EventMessage.class);

        structAndThingMapping.addMapping(structSuit.getStructDeclaration("上报交易记录[上行]"), EventMessage.class);
        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("交易记录确认[下行]");
        structAndThingMapping.addMapping(AcknowledgeDeviceMessage.class, "ReportTransOrderAck", target);

        // 充电事件
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电结束[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("错误报文[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电阶段BMS中止[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电阶段充电机中止[上行]"), EventMessage.class);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "WriteBillingTermsFunInv", structSuit.getStructDeclaration("写计费模型设置[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("写计费模型设置响应[指令响应]"), FunctionInvokeMessageReply.class);

        // 充电发起
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("充电桩主动申请启动充电[上行]"), DefaultDeviceRequestMessage.class);
        structAndThingMapping.addMapping(DefaultDeviceRequestMessageReply.class, "PileSwitchOnChargingRequestReply", structSuit.getStructDeclaration("运营平台确认启动充电应答[下行]"));
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "SwitchOnChargingFunInv", structSuit.getStructDeclaration("运营平台远程控制启机命令[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("远程启动充电命令回复[指令响应]"), FunctionInvokeMessageReply.class);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "SwitchOffChargingFunInv", structSuit.getStructDeclaration("运营平台远程停机[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("远程停机命令回复[指令响应]"), FunctionInvokeMessageReply.class);

        // 运维相关
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "WritePileSettingFunInv", structSuit.getStructDeclaration("写充电桩工作参数设置[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("写充电桩工作参数设置响应[指令响应]"), FunctionInvokeMessageReply.class);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "WriteTimestampFunInv", structSuit.getStructDeclaration("写对时设置[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("写对时设置响应[指令响应]"), FunctionInvokeMessageReply.class);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "RebootFunInv", structSuit.getStructDeclaration("远程重启[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("远程重启应答[指令响应]"), FunctionInvokeMessageReply.class);
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "CallOfOTAFunInv", structSuit.getStructDeclaration("远程更新[指令]"));
        structAndThingMapping.addMapping(structSuit.getStructDeclaration("远程更新应答[指令响应]"), FunctionInvokeMessageReply.class);


        //指令响应：Decode
//        for (StructDeclaration structDcl : structSuit.structDeclarations()) {
//            if (!structDcl.getName().contains("指令响应")) continue;
//
//            ((DefaultStructDeclaration) structDcl).addMetaAnnotation(msgIdMappingAnn);
//        }

        //请求应答：Encode
//        for (StructDeclaration structDcl : structSuit.structDeclarations()) {
//            if (!structDcl.getName().contains("应答[下行]")) continue;
//
//            ((DefaultStructDeclaration) structDcl).addMetaAnnotation(msgIdMappingAnn);
//        }

        DefaultFieldAndPropertyMapping fieldAndPropertyMapping = new DefaultFieldAndPropertyMapping();
        DefaultFieldValueAndPropertyMapping fieldValueAndPropertyMapping = new DefaultFieldValueAndPropertyMapping();

        return new SimpleStructAndMessageMapper(structAndThingMapping, fieldAndPropertyMapping, fieldValueAndPropertyMapping);
    }

    /**
     * 充电桩登录认证消息[上行], 0x01
     */
    private static DefaultStructDeclaration     buildAuthRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩登录认证消息[上行]", "CMD:0x01");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("AuthRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 30));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x01));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfPileType());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("充电枪数量", "gunCount", BaseDataType.UINT8, (short) (8));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        fieldDcl = buildDataFieldDcl("通信协议版本", "protocolVersion", BaseDataType.UINT8, (short) (9));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        fieldDcl = buildDataFieldDcl("程序版本", "firmwareVersion", BaseDataType.CHARS08, (short) (10));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        fieldDcl = buildDataFieldDcl("网络链接类型", "networkType", BaseDataType.UINT8, (short) (18));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        fieldDcl = buildDataFieldDcl("SIM卡", "simNo", BaseDataType.BCD10_STR, (short) (19));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        fieldDcl = buildDataFieldDcl("运营商", "simSP", BaseDataType.UINT8, (short) (29));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电桩登录认证应答[下行], 0x02
     */
    private static DefaultStructDeclaration     buildAuthResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩登录认证应答[下行]", "CMD:0x02");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("AuthResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x02));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("登陆结果", "rstFlag", BaseDataType.UINT8, (short)(7));
        fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput(
                ThingValueNormalizations.ofToDictVal(YKCV1DictBookBuilder.buildLoginAuthRstFlagDict(), (byte) 0x01)
        ));
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 充电桩心跳包[上行], 0x03
     * <li>10秒周期上送，用于链路状态判断，3次未收到心跳包视为网络异常，需要重新登陆</li>
     */
    private static DefaultStructDeclaration     buildHeartBeatPingStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩心跳包[上行]", "CMD:0x03");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("HeartBeatPing"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x03));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo().addMeta(ThingAnnotation.EventData()));

        DefaultFieldDeclaration fieldDcl;

        //0x00：正常 0x01：故障
        fieldDcl = buildDataFieldDcl("枪状态", "gunStatus", BaseDataType.UINT8, (short) (8));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildGunStatusDictMapping("gunStatusDesc"))));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 心跳包应答[下行], 0x04
     */
    private static DefaultStructDeclaration     buildHeartBeatPongStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("心跳包应答[下行]", "CMD:0x04");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("HeartBeatPong"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x04));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo().addMeta(ThingAnnotation.AckOutput()));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("心跳应答", "pongFlag", BaseDataType.UINT8, (short) (8));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.AckOutput()).setDefaultValue((byte) 0));

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 计费模型验证请求[上行], 0x05
     * <li>主动请求，直到成功</li>
     * <li>充电桩在登陆成功后，都需要对当前计费模型校验，如计费模型与平台当前不一致，则需要向平台请求新的计费模型</li>
     */
    private static DefaultStructDeclaration     buildCheckFeeTermsRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型验证请求[上行]", "CMD:0x05");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CheckFeeTermsRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x03));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        //首次连接到平台时置零
        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short) (7));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 计费模型验证请求应答[下行], 0x06
     */
    private static DefaultStructDeclaration     buildCheckFeeTermsRequestReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型验证请求应答[下行]", "CMD:0x06");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CheckFeeTermsRequestReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 10));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x06));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short) (7));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));

        //0x00 桩计费模型与平台一致 0x01 桩计费模型与平台不一致
        fieldDcl = buildDataFieldDcl("验证结果", "rstFlag", BaseDataType.UINT8, (short) (9));
        fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput(YKCV1DictBookBuilder.buildCheckFeeTermsRstCodeDict()));
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 充电桩计费模型请求[上行], 0x09
     * <li>主动请求，直到成功</li>
     * <li>充电桩计费模型与平台不一致时，都需要请求计费模型，如计费模型请求不成功，则禁止充电</li>
     */
    private static DefaultStructDeclaration     buildBillingTermsRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩计费模型请求[上行]", "CMD:0x09");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("BillingTermsRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x09));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 计费模型请求应答[下行], 0x0A
     * <li>用户充电费用计算，每半小时为一个费率段，共48段，每段对应尖峰平谷其中一个费率，充电时桩屏幕按此费率分别显示已充电费和服务费</li>
     */
    private static DefaultStructDeclaration     buildBillingTermsRequestReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型请求应答[下行]", "CMD:0x0A");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("BillingTermsRequestReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 90));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x0A));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.BCD02_STR, (short) (7));
        structDcl.addField(fieldDcl.setDefaultValue("0100"));

        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖费电费费率", "sharpEUP", BaseDataType.UINT32, (short) (9));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖服务费费率", "sharpSUP", BaseDataType.UINT32, (short) (13));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰电费费率", "peakEUP", BaseDataType.UINT32, (short) (17));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰服务费费率", "peakSUP", BaseDataType.UINT32, (short) (21));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平电费费率", "shoulderEUP", BaseDataType.UINT32, (short) (25));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平服务费费率", "shoulderSUP", BaseDataType.UINT32, (short) (29));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷电费费率", "offPeakEUP", BaseDataType.UINT32, (short) (33));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷服务费费率", "offPeakSUP", BaseDataType.UINT32, (short) (37));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));

        //
        fieldDcl = buildDataFieldDcl("计损比例", "withLostRate", BaseDataType.UINT8, (short) (41));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));

        for (int i = 0, j = 0; i < 24; i++, j += 2) {
            //0x00：尖费率 0x01：峰费率 0x02：平费率 0x03：谷费率
            fieldDcl = buildDataFieldDcl(String.format("%02d:00～%02d:30时段费率号", i, i),
                                    String.format("rateNoOf%02d00%02d30", i, i),
                                    BaseDataType.UINT8, (short) (42 + j));
            structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()).setDefaultValue((byte) 0x00));
            fieldDcl = buildDataFieldDcl(String.format("%02d:30～%02d:00时段费率号", i, i + 1),
                                        String.format("rateNoOf%02d30%02d00", i, i + 1),
                                        BaseDataType.UINT8, (short) (43 + j));
            structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()).setDefaultValue((byte) 0x00));
        }

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 读取实时监测数据[指令], 0x12
     * <li>主动请求</li>
     * <li>运营平台根据需要主动发起读取实时数据的请求</li>
     */
    private static DefaultStructDeclaration     buildCallOfRealTimeMonitorDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读取实时监测数据[指令]", "CMD:0x12");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CallOfRealTimeMonitorData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x12));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo().addMeta(ThingAnnotation.FuncInput()));

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 上传实时监测数据[上行], 0x13
     * <li>周期上送、变位上送、召唤</li>
     * <li>上送充电枪实时数据，周期上送时，待机 5 分钟、充电 15 秒</li>
     */
    private static DefaultStructDeclaration     buildReportRealTimeMonitorDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上传实时监测数据[上行]", "CMD:0x13");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportRealTimeMonitorData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 60));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x13));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) (16)));
        structDcl.addField(buildDFDclOfGunNo());

        // buildPileStatusDict()
        fieldDcl = buildDataFieldDcl("状态", "pileStatus", BaseDataType.UINT8, (short) (24));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0x00 否 0x01 是 0x02 未知 （无法检测到枪是否插回枪座即 未知）
        fieldDcl = buildDataFieldDcl("枪是否归位", "gunIsHoming", BaseDataType.UINT8, (short) (25));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0x00 否 0x01 是需做到变位上送
        fieldDcl = buildDataFieldDcl("是否插枪", "gunIsPlugin", BaseDataType.UINT8, (short) (26));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后一位；待机置零
        fieldDcl = buildDataFieldDcl("输出电压", "outputVoltage", BaseDataType.UINT16, (short) (27));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //精确到小数点后一位；待机置零
        fieldDcl = buildDataFieldDcl("输出电流", "outputCurrent", BaseDataType.UINT16, (short) (28));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //整形，偏移量-50；待机置零
        fieldDcl = buildDataFieldDcl("枪线温度", "temperatureOfGunWire", BaseDataType.UINT8, (short) (31));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //没有置零
        fieldDcl = buildDataFieldDcl("枪线编码", "gunWireCode", BaseDataType.HEX08_STR, (short) (32));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //待机置零；交流桩置零
        fieldDcl = buildDataFieldDcl("整车动力蓄电池荷电状态", "SOC", BaseDataType.UINT8, (short) (40));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //整形，偏移量-50 ºC；待机置零； 交流桩置零
        fieldDcl = buildDataFieldDcl("电池组最高温度", "maxTemperatureOfBattery", BaseDataType.UINT8, (short) (41));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //单位：min；待机置零
        fieldDcl = buildDataFieldDcl("累计充电时间", "chargingAccDuration", BaseDataType.UINT16, (short) (42));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //单位：min；待机置零、交流桩置零
        fieldDcl = buildDataFieldDcl("剩余时间", "chargingRemainDuration", BaseDataType.UINT16, (short) (44));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //精确到小数点后四位；待机置零
        fieldDcl = buildDataFieldDcl("充电度数", "chargingEC", BaseDataType.UINT32, (short) (46));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //精确到小数点后四位；待机置零  未设置计损比例时等于充电度数
        fieldDcl = buildDataFieldDcl("计损充电度数", "chargingECWithLose", BaseDataType.UINT32, (short) (50));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //精确到小数点后四位；待机置零 （电费+服务费）* 计损充电度数
        fieldDcl = buildDataFieldDcl("已充金额", "chargeAmount", BaseDataType.UINT32, (short) (54));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //Bit 位表示（0 否 1 是），低位到高位顺序
        //Bit1：急停按钮动作故障；Bit2：无可用整流模块； Bit3：出风口温度过高；Bit4：交流防雷故障；
        //Bit5：交直流模块 DC20 通信中断； Bit6：绝缘检测模块 FC08 通信中断；
        //Bit7：电度表通信中断；Bit8：读卡器通信中断； Bit9：RC10 通信中断；Bit10：风扇调速板故障；
        //Bit11：直流熔断器故障；Bit12：高压接触器故障；Bit13：门打开；
        fieldDcl = buildDataFieldDcl("硬件故障", "faultCode", BaseDataType.UINT16, (short) (58));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电握手[上行], 0x15
     * <li>主动请求</li>
     * <li>GBT-27930 充电桩与BMS充电握手阶段报文</li>
     */
    private static DefaultStructDeclaration     buildReportChargingHandshakeDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电握手[上行]", "CMD:0x15");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportChargingHandshakeData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 73));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x15));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //当前版本为 V1.1，表示为：byte3，byte2—0001H；byte1—01H
        fieldDcl = buildDataFieldDcl("BMS 通信协议版本号", "BMSProtocolVersion", BaseDataType.Num010101_Str, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //电池类型,01H:铅酸电池;02H:氢电池;03H:磷酸铁锂电池;04H:锰酸锂电池;05H:钴酸锂电池;06H:三元材料电池;07H:聚合物锂离子电池;08H:钛酸锂电池;FFH:其他;
        fieldDcl = buildDataFieldDcl("BMS 电池类型", "BMSBatteryType", BaseDataType.INT8, (short) 27);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildBMSBatteryTypeDict())));
        //0.1 Ah/位，0 Ah 偏移量
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池系统额定容量", "BMSBatteryRatedCapacity", BaseDataType.UINT16, (short) 28);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //0.1V/位，0V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池系统额定总电压", "BMSBatteryRatedVoltage", BaseDataType.UINT16, (short) 30);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //标准 ASCII 码
        fieldDcl = buildDataFieldDcl("BMS 电池生产厂商名称", "BMSBatteryManufacturer", BaseDataType.CHARS04, (short) 32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //预留，由厂商自行定义
        fieldDcl = buildDataFieldDcl("BMS 电池组序号", "BMSBatterySNO", BaseDataType.HEX04_STR, (short) 36);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //1985 年偏移量，数据范围：1985～ 2235 年
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期年", "BMSBatteryProductionYear", BaseDataType.UINT8, (short) 40);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(1985))));
        //0 月偏移量，数据范围：1～12 月
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期月", "BMSBatteryProductionMonth", BaseDataType.INT8, (short) 41);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //0 日偏移量，数据范围：1～31 日
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期日", "BMSBatteryProductionDay", BaseDataType.INT8, (short) 42);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //1次/位，0次偏移量，以BMS统计为准
        fieldDcl = buildDataFieldDcl("BMS 电池组充电次数", "BMSBatteryCountOfCharges", BaseDataType.UINT24, (short) 43);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //0=租赁；1=车自有
        fieldDcl = buildDataFieldDcl("BMS 电池组产权标识", "BMSBatteryPRFCode", BaseDataType.INT8, (short) 46);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildBMSBatteryOwnershipDict())));
        //
        fieldDcl = buildDataFieldDcl("预留位", "reversed01", BaseDataType.UINT8, (short) 47);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //
        fieldDcl = buildDataFieldDcl("BMS 车辆识别码", "VIN", BaseDataType.CHARS17, (short) 48);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));
        //
        fieldDcl = buildDataFieldDcl("BMS 软件版本号", "BMSFirmwareVersion", BaseDataType.Num0101010203_Str, (short) 65);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 参数配置[上行], 0x17
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 参数配置阶段报文</li>
     */
    private static DefaultStructDeclaration     buildReportChargingSettingWithBMSStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("参数配置[上行]", "CMD:0x17");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportChargingSettingWithBMS"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 45));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x15));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 0.01 V/位，0 V 偏移量； 数据范围：0~24 V
        fieldDcl = buildDataFieldDcl("BMS 单体动力蓄电池最高允许充电电压", "BMSSingleBatteryMaxChargingVoltage", BaseDataType.UINT16, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 0.1 A/位，-400A 偏移量
        fieldDcl = buildDataFieldDcl("BMS 最高允许充电电流", "BMSBatteryMaxChargingCurrent", BaseDataType.INT16, (short) 26);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

        // 0.1 kWh/位，0 kWh 偏移量； 数据范围：0~1000 kWh
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池标称总能量", "BMSBatteryRatedCapacity", BaseDataType.INT16, (short) 28);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 0.1 V/位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 最高允许充电总电压", "BMSTotalBatteryRatedMaxChargingVoltage", BaseDataType.INT16, (short) 30);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //1ºC/位，-50 ºC 偏移量；数据范 围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 最高允许温度", "BMSRatedMaxTemperature", BaseDataType.INT8, (short) 32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-50))));

        //0.1%/位，0%偏移量；数据范围：0 ~ 100%
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池荷电状态(soc)", "SOC", BaseDataType.INT16, (short) 33);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //整车动力蓄电池总电压
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池当前电池电压", "BMSBatteryVoltage", BaseDataType.INT16, (short) 35);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 V /位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("电桩最高输出电压", "pileMaxOutputVoltage", BaseDataType.INT16, (short) 37);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 V /位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("电桩最低输出电压", "pileMinOutputVoltage", BaseDataType.INT16, (short) 39);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("电桩最大输出电流", "pileMaxOutputCurrent", BaseDataType.INT16, (short) 41);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

        //0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("电桩最小输出电流", "pileMinOutputCurrent", BaseDataType.INT16, (short) 43);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电结束[上行], 0x19
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电结束阶段报文</li>
     */
    private static DefaultStructDeclaration     buildReportChargingFinishEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电结束[上行]", "CMD:0x19");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportChargingFinishEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 39));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x19));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        //1%/位，0%偏移量；数据范围：0~100%
        fieldDcl = buildDataFieldDcl("BMS 中止荷电状态 SOC", "BMSEndSOC", BaseDataType.INT8, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.01 V/位，0 V 偏移量；数据范 围：0 ~24 V
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池单体最低电压", "BMSSingleBatteryMinVoltage", BaseDataType.INT16, (short) 25);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.01 V/位，0 V 偏移量；数据范 围：0 ~24 V
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池单体最高电压", "BMSSingleBatteryMaxVoltage", BaseDataType.INT16, (short) 27);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池最低温度", "BMSMinTemperature", BaseDataType.INT8, (short) 29);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-50))));

        //1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池最高温度", "BMSMaxTemperature", BaseDataType.INT8, (short) 30);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-50))));

        //1 min/位，0 min 偏移量；数据范围：0~600 min
        fieldDcl = buildDataFieldDcl("电桩累计充电时间", "accChargingDuration", BaseDataType.UINT16, (short) 31);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 kWh/位，0 kWh 偏移量；数据范围：0~1000 kWh
        fieldDcl = buildDataFieldDcl("电桩输出能量", "pileOutputEC", BaseDataType.UINT16, (short) 33);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //充电机编号， 1/位， 1偏移量 ，数 据范 围 ： 0 ～ 0xFFFFFFFF
        fieldDcl = buildDataFieldDcl("充电机编号", "pileChargerNo", BaseDataType.UINT32, (short) 35);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 错误报文[上行], 0x1B
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电错误报文</li>
     */
    private static DefaultStructDeclaration     buildReportErrorEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("错误报文[上行]", "CMD:0x1B");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportErrorEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 32));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x19));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        fieldDcl = buildDataFieldDcl("错误码数值", "errorCode", BaseDataType.BYTES08, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildErrorReportErrorCodeDict("errorCodes", "errorDescs"))));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电阶段BMS中止[上行], 0x1D
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电阶段 BMS 中止报文</li>
     */
    private static DefaultStructDeclaration     buildReportBMSStopEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电阶段BMS中止[上行]", "CMD:0x1D");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportBMSStopEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 28));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x1D));

        // 数据块
        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        DefaultFieldDeclaration fieldDcl;
        ThingItemMapping<String> itemMapping;

        fieldDcl = buildDataFieldDcl("BMS中止充电原因/故障原因/错误原因", "reason", BaseDataType.BYTES04, (short) 24);
        itemMapping = YKCV1DictBookBuilder.buildBMSStopChargingReasonCodeDict("BMSStopReasonCodes", "BMSStopReasonDescs");
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(itemMapping)));

//        fieldDcl = buildDataFieldDcl("BMS中止充电故障原因", "BMSStopFault", BaseDataType.UINT16, (short) 25);
//        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildErrorReportErrorCodeDict("BMSStopFaultCodes", "BMSStopFaultDescs"))));
//
//        fieldDcl = buildDataFieldDcl("BMS中止充电错误原因", "BMSStopError", BaseDataType.UINT8, (short) 27);
//        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildErrorReportErrorCodeDict("BMSStopErrorCodes", "BMSStopErrorDescs"))));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电阶段充电机中止[上行], 0x21
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电阶段充电机中止报文</li>
     */
    private static DefaultStructDeclaration     buildReportChargerStopEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电阶段充电机中止[上行]", "CMD:0x21");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportChargerStopEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 28));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x21));

        // 数据块

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        DefaultFieldDeclaration fieldDcl;
        ThingItemMapping<String> itemMapping;

        fieldDcl = buildDataFieldDcl("充电机中止充电原因/故障原因/错误原因", "reason", BaseDataType.BYTES04, (short) 24);
        itemMapping = YKCV1DictBookBuilder.buildChargerStopChargingReasonCodeDict("reasonCodes", "reasonDescs");
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(itemMapping)));

//        fieldDcl = buildDataFieldDcl("充电机中止充电故障原因", "chargerStopFaultCodes", BaseDataType.UINT16, (short) 25);
//        structDcl.addField(fieldDcl);
//
//        fieldDcl = buildDataFieldDcl("充电机中止充电错误原因", "chargerStopErrorCodes", BaseDataType.UINT8, (short) 27);
//        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电过程BMS需求与充电机输出[上行], 0x23
     * <li>周期上送（15 秒）</li>
     * <li>GBT-27930 充电桩与BMS充电过程BMS需求、充电机输出</li>
     */
    private static DefaultStructDeclaration     buildReportBMSRequirementAndChargerOutputDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电过程BMS需求与充电机输出[上行]", "CMD:0x23");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportBMSRequirementAndChargerOutputData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 44));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x23));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        // 0.1 V/位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 电压需求", "BMSReqVoltage", BaseDataType.INT16, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("BMS 电流需求", "BMSReqCurrent", BaseDataType.INT16, (short) 26);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

        // 0x01：恒压充电；0x02：恒流充电
        fieldDcl = buildDataFieldDcl("BMS 充电模式", "BMSChargingMode", BaseDataType.INT8, (short) 28);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildBMSChargingModeDict())));

        // 0.1 V/位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 充电电压测量值", "BMSChargingVoltage", BaseDataType.INT16, (short) 29);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("BMS 充电电流测量值", "BMSChargingCurrent", BaseDataType.INT16, (short) 31);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

        //1-12 位：最高单体动力蓄电池电压，数据分辨率：0.01 V/位，0 V 偏移量；数据范围：0~24 V；
        //13-16 位：最高单体动力蓄电池电压所在组号，数据分辨率：1/位，0 偏移量；数据范围：0~15
        fieldDcl = buildDataFieldDcl("BMS 最高单体动力蓄电池电压及组号", "BMSBatterySingleMaxVoltage", BaseDataType.HEX02_STR, (short) 33);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //1%/位，0%偏移量；数据范围：0~100%
        fieldDcl = buildDataFieldDcl("BMS 当前荷电状态 SOC", "SOC", BaseDataType.UINT8, (short) 35);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //1 min/位，0 min 偏移量；数据范围：0~600 min
        fieldDcl = buildDataFieldDcl("BMS 估算剩余充电时间", "BMSChargingRemainDuration", BaseDataType.UINT16, (short) 36);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 V/位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("电桩电压输出值", "pileOutputVoltage", BaseDataType.UINT16, (short) 38);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("电桩电流输出值", "pileOutputCurrent", BaseDataType.UINT16, (short) 40);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-4000))));

        //1 min/位，0 min 偏移量；数据范围：0 ~ 600 min
        fieldDcl = buildDataFieldDcl("累计充电时间", "accChargingDuration", BaseDataType.UINT16, (short) 42);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电过程BMS信息[上行], 0x25
     * <li>周期上送（15 秒）</li>
     * <li>GBT-27930 充电桩与BMS充电过程BMS信息</li>
     */
    private static DefaultStructDeclaration     buildReportBMSChargingDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电过程BMS信息[上行]", "CMD:0x25");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportBMSChargingData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 31));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x25));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData()));

        // 1/位，1 偏移量；数据范围：1~256
        fieldDcl = buildDataFieldDcl("BMS 最高单体动力蓄电池电压所在编号", "BMSBatteryNoOfMaxSingleVoltage", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~ +200 ºC
        fieldDcl = buildDataFieldDcl("BMS 最高动力蓄电池温度", "BMSBatteryMaxTemperature", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-50))));

        // 1/位，1 偏移量；数据范围：1~128
        fieldDcl = buildDataFieldDcl("最高温度检测点编号", "BMSCheckPointNoOfMaxTemperature", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        // 1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("最低动力蓄电池温度", "BMSBatteryMinTemperature", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(ThingValueNormalizations.plusOffsetAndToInt(-50))));

        // 1/位，1 偏移量；数据范围：1~128
        fieldDcl = buildDataFieldDcl("最低动力蓄电池温度检测点编号", "BMSCheckPointNoOfMinTemperature", BaseDataType.UINT8, (short) 28);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //
        fieldDcl = buildDataFieldDcl("BMS动力蓄电池状态码", "BMSBatteryStatus", BaseDataType.BYTES02, (short) 29);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildBMSOnChargingStatusDict("BMSBatteryStatusCodes", "BMSBatteryStatusDescs"))));


//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电桩主动申请启动充电[上行], 0x31
     * <li>按需发送</li>
     * <li>用户通过帐号密码及刷卡在充电桩上操作请求充电</li>
     */
    private static DefaultStructDeclaration     buildPileSwitchOnChargingRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩主动申请启动充电[上行]", "CMD:0x31");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("PileSwitchOnChargingRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 51));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x31));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7).addMeta(ThingAnnotation.DevReqInput()));

        //0x01 表示通过刷卡启动充电
        //0x02 表求通过帐号启动充电（暂不支持）
        //0x03 表示vin码启动充电
        fieldDcl = buildDataFieldDcl("启动方式", "launchMethod", BaseDataType.INT8, (short) 8);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        // 0x00 不需要 0x01 需要
        fieldDcl = buildDataFieldDcl("是否需要密码", "needPassword", BaseDataType.INT8, (short) 9);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        // 不足 8 位补 0，具体见示例：00000000D14B0A54
        fieldDcl = buildDataFieldDcl("账号或者物理卡号", "usernameOrCardNo", BaseDataType.HEX08_STR, (short) 10);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        // 对用户输入的密码进行16位MD5加密，采用小写上传
        fieldDcl = buildDataFieldDcl("输入密码", "password", BaseDataType.HEX16_STR, (short) 18);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

        //启动方式为vin码启动充电时上送, 其他方式置零( ASCII码)，VIN码需要反序上送
        fieldDcl = buildDataFieldDcl("VIN码", "VIN", BaseDataType.CHARS17, (short) 34);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqInput()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 运营平台确认启动充电应答[下行], 0x32
     * <li>应答</li>
     * <li>启动充电鉴权结果</li>
     */
    private static DefaultStructDeclaration     buildPileSwitchOnChargingRequestReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台确认启动充电应答[下行]", "CMD:0x32");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("PileSwitchOnChargingRequestReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 38));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x32));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.DevReqReplyOutput()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.DevReqReplyOutput(NormToShort)));

        // 显示在屏幕上，不足 8 位补零
        fieldDcl = buildDataFieldDcl("逻辑卡号", "cardDisplayNo", BaseDataType.BCD08_STR, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()));

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("账户余额", "accountAmount", BaseDataType.INT32, (short) 32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()).setDefaultValue(0));

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("鉴权成功标志", "rstFlag", BaseDataType.UINT8, (short) 36);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput(NormToShort)).setDefaultValue((short) 0x00));

        //0x01 账户不存在
        //0x02 账户冻结
        //0x03 账户余额不足
        //0x04 该卡存在未结账记录
        //0x05 桩停用
        //0x06 该账户不能在此桩上充电
        //0x07 密码错误
        //0x08 电站电容不足
        //0x09 系统中 vin 码不存在0x0A 该桩存在未结账记录0x0B 该桩不支持刷卡
        fieldDcl = buildDataFieldDcl("失败原因", "reasonCode", BaseDataType.BCD01_STR, (short) 37);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DevReqReplyOutput()).setDefaultValue("00"));

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 运营平台远程控制启机命令[指令], 0x34
     * <li>按需发送</li>
     * <li>当用户通过远程启动充电时，发送本命令</li>
     */
    private static DefaultStructDeclaration     buildSwitchOnChargingFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台远程控制启机命令[指令]", "CMD:0x34");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SwitchOnChargingFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 44));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x34));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.FuncInput()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.FuncInput(NormToShort)));

        // 显示在屏幕上，不足 8 位补零
        fieldDcl = buildDataFieldDcl("逻辑卡号", "cardDisplayNo", BaseDataType.BCD08_STR, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        // 不足补零，桩与平台交互需使用的物理卡号
        fieldDcl = buildDataFieldDcl("物理卡号", "cardNo", BaseDataType.HEX08_STR, (short) 32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("账户余额", "accountAmount", BaseDataType.INT32, (short) 40);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 远程启动充电命令回复[指令响应], 0x33
     * <li>应答</li>
     * <li>启动充电鉴权结果</li>
     */
    private static DefaultStructDeclaration     buildSwitchOnChargingFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程启动充电命令回复[指令响应]", "CMD:0x33");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SwitchOnChargingFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x33));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.FuncOutput()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.FuncOutput(NormToShort)));

        fieldDcl = buildDataFieldDcl("启动结果", "rstFlag", BaseDataType.INT8, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncOutput(YKCV1DictBookBuilder.buildSuccessOrFailDict())));

        fieldDcl = buildDataFieldDcl("失败原因", "reasonCode", BaseDataType.INT8, (short) 25);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncOutput(YKCV1DictBookBuilder.buildRemoteSwitchOnFailReasonCodeDict("reasonDesc"))));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 运营平台远程停机[指令], 0x36
     * <li>按需发送</li>
     * <li>当用户通过远程停止充电时，发送本命令，如APP停止充电</li>
     */
    private static DefaultStructDeclaration     buildSwitchOffChargingStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台远程停机[指令]", "CMD:0x36");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SwitchOffChargingFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x36));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7).addMeta(ThingAnnotation.FuncInput(NormToShort)));

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 远程停机命令回复[指令响应], 0x35
     * <li>应答发送</li>
     * <li>远程停止充电命令回复，平台发送 0x36 后即关闭订单，接收到停机指令后设备务必保证停机。</li>
     */
    private static DefaultStructDeclaration     buildPileSwitchOffFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程停机命令回复[指令响应]", "CMD:0x35");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("PileSwitchOffFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 10));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x35));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo((short) 0).addMeta(ThingAnnotation.FuncOutput()));
        structDcl.addField(buildDFDclOfGunNo((short) 7).addMeta(ThingAnnotation.FuncOutput(NormToShort)));


        fieldDcl = buildDataFieldDcl("停止结果", "rstFlag", BaseDataType.INT8, (short) 8);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncOutput(YKCV1DictBookBuilder.buildSuccessOrFailDict())));

        fieldDcl = buildDataFieldDcl("失败原因", "reasonCode", BaseDataType.INT8, (short) 9);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncOutput(YKCV1DictBookBuilder.buildRemoteSwitchOffFailReasonCodeDict("reasonDesc"))));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 上报交易记录[上行], 0x3B
     * <li>主动上送</li>
     * <li>充电桩在网络正常情况下，主运发送结算账单，直到运营平台响应成账单上传成功
     * （若未收到 0x40 回复间隔 30s 再重试一次，最多重试 3 次），收到账单结算成功，本账单在充电桩本地删除。
     * 每次接收到启机命令并已执行启机过程，无论启机成功与否，都需在订单结束充电后生成账单上传</li>
     */
    private static DefaultStructDeclaration     buildReportTransOrderStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报交易记录[上行]", "CMD:0x3B");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportTransOrder"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 158));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x3B));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo().addMeta(ThingAnnotation.EventData()));
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23).addMeta(ThingAnnotation.EventData(NormToShort)));

        fieldDcl = buildDataFieldDcl("开始时间", "beginTime", BaseDataType.CP56Time2a, (short) 24);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        fieldDcl = buildDataFieldDcl("结束时间", "endTime", BaseDataType.CP56Time2a, (short) 31);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("尖单价", "sharpUP", BaseDataType.INT32LE, (short) 38);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("尖电量", "sharpTotalEC", BaseDataType.INT32LE, (short) 42);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损尖电量", "sharpTotalECWithLose", BaseDataType.INT32LE, (short) 46);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("尖金额", "sharpTotalAmount", BaseDataType.INT32LE, (short) 50);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("峰单价", "peakUP", BaseDataType.INT32LE, (short) 54);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("峰电量", "peakTotalEC", BaseDataType.INT32LE, (short) 58);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损峰电量", "peakTotalECWithLose", BaseDataType.INT32LE, (short) 62);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("峰金额", "peakTotalAmount", BaseDataType.INT32LE, (short) 66);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("平单价", "shoulderUP", BaseDataType.INT32LE, (short) 70);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("平电量", "shoulderTotalEC", BaseDataType.INT32LE, (short) 74);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损平电量", "shoulderTotalECWithLose", BaseDataType.INT32LE, (short) 78);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("平金额", "shoulderTotalAmount", BaseDataType.INT32LE, (short) 82);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("谷单价", "offPeakUP", BaseDataType.INT32LE, (short) 86);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("谷电量", "offPeakTotalEC", BaseDataType.INT32LE, (short) 90);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损谷电量", "offPeakTotalECWithLose", BaseDataType.INT32LE, (short) 94);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("谷金额", "offPeakTotalAmount", BaseDataType.INT32LE, (short) 98);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("电表总起值", "beginMeterEC", BaseDataType.INT40LE, (short) 102);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("电表总止值", "endMeterEC", BaseDataType.INT40LE, (short) 107);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("总电量", "totalEC", BaseDataType.INT32LE, (short) 112);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损总电量", "totalMeterECWithLose", BaseDataType.INT32LE, (short) 116);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("消费金额", "totalFeeAmount", BaseDataType.INT32LE, (short) 120);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //VIN 码，此处 VIN 码和充电时 VIN码不同，正序直接上传，无需补 0和反序
        fieldDcl = buildDataFieldDcl("电动汽车唯一标识", "VIN", BaseDataType.CHARS17, (short) 124);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //0x01：app 启动0x02：卡启动
        //0x04：离线卡启动
        //0x05: vin 码启动充电
        fieldDcl = buildDataFieldDcl("交易标识", "transFlag", BaseDataType.INT8, (short) 141);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //
        fieldDcl = buildDataFieldDcl("交易时间", "transDateTime", BaseDataType.CP56Time2a, (short) 142);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

        //
        fieldDcl = buildDataFieldDcl("停止原因", "terminateCode", BaseDataType.INT8, (short) 149);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData(YKCV1DictBookBuilder.buildChargingEndDict("terminateDesc"))));

        //
        fieldDcl = buildDataFieldDcl("物理卡号", "cardNo", BaseDataType.HEX08_STR, (short) 150);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.EventData()));

//        structDcl.addField(buildCRCFieldDcl());
//        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 交易记录确认[下行], 0x40
     * <li>应答发送</li>
     * <li>运营平台接收到结算账单上传后，都需回复此确认信息。若桩未收到回复帧，则 5 分钟后继续
     * 上送一次交易记录，此情况下无论平台是否成功回复都停止上送。
     * 这一帧仅是报文交互使用，意指平台成功接收到交易记录报文，并不代表交易订单成功结算</li>
     */
    private static DefaultStructDeclaration buildReportTransOrderAckStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("交易记录确认[下行]", "CMD:0x40");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportTransOrderAck"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 17));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x40));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());

        //0x00 上传成功 0x01 非法账单
        fieldDcl = buildDataFieldDcl("确认结果", "rstFlag", BaseDataType.UINT8, (short) 16);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程账户余额更新[下行], 0x42
     * <li>按需发送</li>
     * <li>平台在用户完成充值后会将用户更新的余额下发到充电桩，桩接收到此数据帧需要对当前充电用户的信息进行校验并更新余额信息</li>
     */
    private static DefaultStructDeclaration buildWriteCardBalanceFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程账户余额更新[下行]", "CMD:0x42");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteCardBalanceFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 20));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x42));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        // 不足 8 位补零
        // 如果不为零，需要校验本次充电是否为此卡充电
        // 如果为零，则不校验，直接更新桩当前充电用户余额
        fieldDcl = buildDataFieldDcl("物理卡号", "cardNo", BaseDataType.HEX08_STR, (short) 8);
        structDcl.addField(fieldDcl);

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("修改后账户金额", "newAccountAmount", BaseDataType.INT32, (short) 16);
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 余额更新应答[上行], 0x41
     * <li>应答回复</li>
     * <li>平台在用户完成充值后会将用户更新的余额下发到充电桩，桩接收到此数据帧需要对当前充电用户的信息进行校验并更新余额信息</li>
     */
    private static DefaultStructDeclaration buildUpdateAccountBalanceFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("余额更新应答[上行]", "CMD:0x41");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("UpdateAccountBalanceFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x41));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());

        // 不足 8 位补零
        // 如果不为零，需要校验本次充电是否为此卡充电
        // 如果为零，则不校验，直接更新桩当前充电用户余额
        fieldDcl = buildDataFieldDcl("物理卡号", "cardNo", BaseDataType.HEX08_STR, (short) 7);
        structDcl.addField(fieldDcl);

        //0x00-修改成功 0x01-设备编号错误 0x02-卡号错误
        fieldDcl = buildDataFieldDcl("修改结果", "rstCode", BaseDataType.UINT8, (short) 15);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 下发离线卡数据指令[下行], 0x44
     * <li>按需发送</li>
     * <li>离线卡适用于桩离线充电模式，平台在充电桩在线时会下发此数据帧到充电桩，
     * 充电桩接收到后储存离线卡信息到桩本地（如果已存在离线卡则用最新的数据覆盖本地数据，不存在则插入），
     * 若用户刷卡充电时桩处理离线模式，则刷鉴权走桩本地进行判断。</li>
     */
    private static DefaultStructDeclaration buildAddOrUpdateCardFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("下发离线卡数据指令[下行]", "CMD:0x44");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("AddOrUpdateCardFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x44));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        //TODO 补全

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "itemCount", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl);

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("条目", "items", (short)5, (short)8);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
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

        DefaultFieldDeclaration fieldDcl;

        //
        fieldDcl = buildDataFieldDcl("第X个卡逻辑卡号", "", BaseDataType.CHARS08, anchor, (short) 0);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX08_STR, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 下发离线卡数据指令响应[上行], 0x43
     * <li>应答回复</li>
     * <li>离线卡数据同步应答</li>
     */
    private static DefaultStructDeclaration buildWriteICDataFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("下发离线卡数据指令响应[上行]", "CMD:0x43");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteICDataFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x43));

        //TODO 补全

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());

        //0x01 卡号格式错误 0x02 储存空间不足
        fieldDcl = buildDataFieldDcl("保存结果", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        //0x01 卡号格式错误 0x02 储存空间不足
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, (short) 9);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 清除离线卡数据指令[下行], 0x46
     * <li>按需发送</li>
     * <li>离线卡清除是平台主动下发的操作，平台在充电桩在线时会下发此数据帧到充电桩，
     *      充电桩接收到离线卡数据清除报文后清除到桩本地对应的离线卡数据</li>
     */
    private static DefaultStructDeclaration buildCleanICDataFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("清除离线卡数据指令[下行]", "CMD:0x46");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CleanICDataFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x46));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl);

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("目标卡X", "cardX", (short)5, (short)8);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
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

        DefaultFieldDeclaration fieldDcl;

        //
        fieldDcl = buildDataFieldDcl("第X个卡逻辑卡号", "", BaseDataType.CHARS08, anchor, (short) 0);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX08_STR, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 清除离线卡数据指令响应[上行], 0x45
     * <li>应答回复</li>
     * <li>离线卡数据清除应答</li>
     */
    private static DefaultStructDeclaration buildClearICDataFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("清除离线卡数据指令响应[上行]", "CMD:0x45");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CleanICDataFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x45));

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl); //TODO 通过数据区长度求解卡的个数

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("条目", "itemX", (short) 7, (short) 10);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
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

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX08_STR, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        // 0x00 清除失败 0x01 清除成功
        fieldDcl = buildDataFieldDcl("清除标记", "", BaseDataType.UINT8, anchor, (short) 0);
        structDcl.addField(fieldDcl);

        //0x01 卡号格式错误 0x02 清除成功
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, anchor, (short) 0);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写充电桩工作参数设置[指令], 0x52
     * <li>按需下发</li>
     * <li>远程设置充电桩是否停用；设置充电桩允许输出功率，以实现电网功率的调节</li>
     */
    private static DefaultStructDeclaration buildWritePileSettingFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写充电桩工作参数设置[指令]", "CMD:0x52");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WritePileSettingFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x52));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("是否允许工作", "enable", BaseDataType.UINT8, (short) 7);
        structDcl.addField(fieldDcl);

        // 1BIN 表示 1%，最大 100%，最小30%
        fieldDcl = buildDataFieldDcl("充电桩最大允许输出功率", "maxOutputRate", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 写充电桩工作参数设置响应[指令响应], 0x51
     * <li>按需发送</li>
     * <li>充电桩接收到运营平台充电桩工作参数设置时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildWritePileSettingFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写充电桩工作参数设置响应[指令响应]", "CMD:0x51");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WritePileSettingFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x51));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "rstFlag", BaseDataType.HEX08_STR, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写对时设置[指令], 0x56
     * <li>周期发送（1 天）</li>
     * <li>运营平台同步充电桩时钟，以保证充电桩与运营平台的时钟一致</li>
     */
    private static DefaultStructDeclaration buildWriteTimestampFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写对时设置[指令]", "CMD:0x56");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteTimestampFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 14));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x56));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("当前时间", "timestamp", BaseDataType.CP56Time2a, (short) 7);
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 写对时设置响应[指令响应], 0x55
     * <li>应答</li>
     * <li>充电桩接收到运营平台同步充电桩时钟时应答</li>
     */
    private static DefaultStructDeclaration buildWriteTimestampFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写对时设置响应[指令响应]", "CMD:0x55");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteTimestampFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 14));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x55));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("当前时间", "timestamp", BaseDataType.CP56Time2a, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写计费模型设置[指令], 0x58
     * <li>按需发送</li>
     * <li>用户充电费用计算，每半小时为一个费率段，共48段，每段对应尖峰平谷其中一个费率充电时桩屏幕按此费率分别显示已充电费和服务费</li>
     */
    private static DefaultStructDeclaration buildWriteBillingTermsStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写计费模型设置[指令]", "CMD:0x58");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteBillingTermsFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 90));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x58));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.BCD02_STR, (short) (7));
        structDcl.addField(fieldDcl);

        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖费电费费率", "sharpEUP", BaseDataType.UINT32, (short) (9));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖服务费费率", "sharpSUP", BaseDataType.UINT32, (short) (13));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰电费费率", "peakEUP", BaseDataType.UINT32, (short) (17));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰服务费费率", "peakSUP", BaseDataType.UINT32, (short) (21));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平电费费率", "shoulderEUP", BaseDataType.UINT32, (short) (25));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平服务费费率", "shoulderSUP", BaseDataType.UINT32, (short) (29));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷电费费率", "offPeakEUP", BaseDataType.UINT32, (short) (33));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷服务费费率", "offPeakSUP", BaseDataType.UINT32, (short) (37));
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("计损比例", "withLostRate", BaseDataType.UINT8, (short) (41));
        structDcl.addField(fieldDcl);

        for (int i = 0, j = 0; i < 24; i++, j += 2) {
            //0x00：尖费率 0x01：峰费率 0x02：平费率 0x03：谷费率
            fieldDcl = buildDataFieldDcl(String.format("%02d:00～%02d:30时段费率号", i, i),
                                        String.format("rateNoOf%02d00%02d30", i, i),
                                        BaseDataType.UINT8, (short) (42 + j));
            structDcl.addField(fieldDcl);
            fieldDcl = buildDataFieldDcl(String.format("%02d:30～%02d:00时段费率号", i, i + 1),
                                        String.format("rateNoOf%02d30%02d00", i, i + 1),
                                        BaseDataType.UINT8, (short) (43 + j));
            structDcl.addField(fieldDcl);
        }

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 写计费模型设置响应[指令响应], 0x57
     * <li>应答</li>
     * <li>充电桩接收到运营平台计费模型时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildWriteBillingTermsReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写计费模型设置响应[指令响应]", "CMD:0x57");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("WriteBillingTermsFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x55));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "rstFlag", BaseDataType.UINT8, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 地锁数据上送[上行], 0x61
     * <li>应答</li>
     * <li>充电桩接收到运营平台计费模型时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildReportParkLockDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("地锁数据上送[上行]", "CMD:0x61");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportParkLockData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x61));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        // 0x00：未到位状态 0x55：升锁到位状态 0xFF：降锁到位状态
        fieldDcl = buildDataFieldDcl("车位锁状态", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        // 0x00：无车辆 0xFF：停放车辆
        fieldDcl = buildDataFieldDcl("车位状态", "", BaseDataType.UINT8, (short) 9);
        structDcl.addField(fieldDcl);

        // 百分比值0~100
        fieldDcl = buildDataFieldDcl("地锁电量状态", "", BaseDataType.UINT8, (short) 10);
        structDcl.addField(fieldDcl);

        //0x00：正常无报警
        //0xFF：待机状态摇臂破坏
        //0x55：摇臂升降异常(未到位)
        fieldDcl = buildDataFieldDcl("报警状态", "", BaseDataType.UINT8, (short) 11);
        structDcl.addField(fieldDcl);

        // 全部置0
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short) 12);
        structDcl.addField(fieldDcl.setDefaultValue(0));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 遥控地锁升锁与降锁命令[下行], 0x62
     * <li>按需发送</li>
     * <li>服务器下发命令给地锁，地锁执行动作</li>
     */
    private static DefaultStructDeclaration buildParkLockControlFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("遥控地锁升锁与降锁命令[下行]", "CMD:0x62");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ParkLockControlFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x62));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("升/降地锁", "", BaseDataType.UINT8, (short)(8));
        structDcl.addField(fieldDcl);

        // 全部置 0（可用于多枪）
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short)(9));
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 遥控地锁升锁与降锁命令响应[上行], 0x63
     * <li>按需发送</li>
     * <li>地锁收到遥控地锁升锁与降锁命令指令，响应本数据</li>
     */
    private static DefaultStructDeclaration buildParkLockControlFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("遥控地锁升锁与降锁命令响应[上行]", "CMD:0x63");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ParkLockControlFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x63));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        // 1，鉴权成功；0，鉴权失败
        fieldDcl = buildDataFieldDcl("地锁控制返回标志", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        // 全部置0
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short) 9);
        structDcl.addField(fieldDcl.setDefaultValue(0));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程重启命令[指令], 0x92
     * <li>按需发送</li>
     * <li>重启充电桩，应对部分问题，如卡死</li>
     */
    private static DefaultStructDeclaration buildRebootFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程重启命令[指令]", "CMD:0x92");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("RebootFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x92));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        //0x01：立即执行  0x02：空闲执行
        fieldDcl = buildDataFieldDcl("执行控制", "option", BaseDataType.UINT8, (short)(7));
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 远程重启应答[指令响应], 0x91
     * <li>按需发送</li>
     * <li>充电桩接收到运营平台远程重启指令时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildRebootFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程重启应答[指令响应]", "CMD:0x91");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("RebootFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x91));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "rstFlag", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程更新[指令], 0x94
     * <li>按需发送</li>
     * <li>对桩进行软件升级，平台升级模式为 ftp 文件升级，由桩企提供升级需要的更新文件（特定文件名，由桩企定义），
     *      平台在数据帧中提供访问更新文件相关服务器地址及下载路径信息，桩下载完更新程序后对文件进行较验，并对桩进行升级。</li>
     */
    private static DefaultStructDeclaration buildCallOfOTAFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程更新[指令]", "CMD:0x94");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CallOfOTAFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x94));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        //0x01：直流  0x02：交流
        fieldDcl = buildDataFieldDcl("桩型号", "", BaseDataType.UINT8, (short)(7));
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.DeviceId()));

        //不足 2 位补零
        fieldDcl = buildDataFieldDcl("桩功率", "", BaseDataType.UINT16, (short)(8));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("升级服务器地址", "", BaseDataType.CHARS16, (short)(10));
        structDcl.addField(fieldDcl);

        //不足 2 位补零
        fieldDcl = buildDataFieldDcl("升级服务器端口", "", BaseDataType.UINT16, (short)(26));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("用户名", "", BaseDataType.CHARS16, (short)(28));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("密码", "", BaseDataType.CHARS16, (short)(44));
        structDcl.addField(fieldDcl);

        //不足 32 位补零，文件路径名由平台定义
        fieldDcl = buildDataFieldDcl("文件路径", "", BaseDataType.CHARS32, (short)(60));
        structDcl.addField(fieldDcl);

        //0x01：立即执行 0x02：空闲执行
        fieldDcl = buildDataFieldDcl("执行控制", "", BaseDataType.UINT8, (short)(92));
        structDcl.addField(fieldDcl);

        //单位：min
        fieldDcl = buildDataFieldDcl("下载超时时间", "", BaseDataType.UINT8, (short)(93));
        structDcl.addField(fieldDcl);

//        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculatorForEncode());

        return structDcl;
    }

    /**
     * 远程更新应答[指令响应], 0x93
     * <li>按需发送</li>
     * <li>充电桩执行过运营平台远程更新指令，响应本数据</li>
     */
    private static DefaultStructDeclaration buildCallOfOTAFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程更新应答[指令响应]", "CMD:0x93");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CallOfOTAFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x93));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00-成功  0x01-编号错误    0x02-程序与桩型号不符  0x03-下载更新文件超时
        fieldDcl = buildDataFieldDcl("升级状态", "rstFlag", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 数据字段：交易流水号
     */
    private static DefaultFieldDeclaration buildDFDclOfTransNo() {
        return buildDataFieldDcl("交易流水号", "transNo", BaseDataType.BCD16_STR, (short) 0);
    }

    /**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo() {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.BCD07_STR, DATA_BEGIN_IDX)
                    .addMeta(ThingAnnotation.DeviceId());
    }

    /**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo(short absOffset) {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.BCD07_STR, (short)(DATA_BEGIN_IDX + absOffset))
                .addMeta(ThingAnnotation.DeviceId());
    }

    /**
     * 数据字段：桩类型
     */
    private static DefaultFieldDeclaration buildDFDclOfPileType() {
        return new DefaultFieldDeclaration("桩类型", "pileType", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 7));
    }

    /**
     * 数据字段：枪号
     */
    private static DefaultFieldDeclaration buildDFDclOfGunNo() {
        return new DefaultFieldDeclaration("枪号", "gunNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 7));
    }

    /**
     * 数据字段：枪号
     */
    private static DefaultFieldDeclaration buildDFDclOfGunNo(short absOffset) {
        return new DefaultFieldDeclaration("枪号", "gunNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + absOffset));
    }

    /**
     * 公共字段：帧头
     */
    private static DefaultFieldDeclaration buildSOP() {
        return new DefaultFieldDeclaration("公共：帧头", "SOP", BaseDataType.UINT8, (short)0)
                .setDefaultValue((byte)0x68);
    }

    /**
     * 公共字段：报文长度
     * @param sizeOfData DATA字段的长度，单位：字节
     */
    private static DefaultFieldDeclaration buildLENFieldDcl(byte sizeOfData) {
        return new DefaultFieldDeclaration("报文长度", "LEN", BaseDataType.UINT8, (short) 1)
                .setDefaultValue(sizeOfData + 4);
    }

    /**
     * 公共字段：报文序号
     */
    private static DefaultFieldDeclaration buildMsgNoFieldDcl() {
        return new DefaultFieldDeclaration("报文序号", CODE_OF_MSG_NO_FIELD, BaseDataType.UINT16, (short) 2)
                    .addMeta(ThingAnnotation.MsgIdUint16Reverse(MsgIdMapping))
                    .setDefaultValue(0);
    }

    /**
     * 公共字段：加密标志
     */
    private static DefaultFieldDeclaration buildEncyFlagFieldDcl() {
        return new DefaultFieldDeclaration("加密标志", CODE_OF_ENCY_FLAG_FIELD, BaseDataType.UINT8, (short) 4)
                .setDefaultValue(0);
    }

    /**
     * 公共字段：报文类型
     */
    private static DefaultFieldDeclaration buildMsgTypeFieldDcl(byte msgType) {
        return new DefaultFieldDeclaration("报文类型", CODE_OF_MSG_TYPE_FIELD, BaseDataType.UINT8, (short) 5)
                .setDefaultValue(msgType);
    }

    /**
     * 公共字段：校验
     */
    private static DefaultFieldDeclaration buildCRCFieldDcl() {
        return new DefaultFieldDeclaration("校验", "CHECK_SUM", BaseDataType.UINT16, (short) -2)
                    .setDefaultValue((short) 0x0000);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor nextToThisAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(nextToThisAnchor, (short)0);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType, short absOffsetInData) {
        return new DefaultFieldDeclaration(name, code, dataType, (short)(DATA_BEGIN_IDX + absOffsetInData));
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor anchor, short offsetToAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(anchor, offsetToAnchor);
    }

    private static CRCCalculator buildCRCCalculator() {
        return new CRC180DCRCCalculator(2, -2);
    }

    private static CRCCalculator buildCRCCalculatorForEncode() {
        return new CRC180DCRCCalculator(2, 0);
    }

    private static class YKCV1FeatureCodeExtractor implements FeatureCodeExtractor {
        public static final byte MAGIC_ID_OF_V1 = (byte) 0x68;

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[6];

            buf.readerIndex(0);
            if (buf.readableBytes() < headerBuf.length) {
                return "[YKCV1]WRONG_SIZE:" + Hex.encodeHexString(buf.array());
            }
            buf.readBytes(headerBuf);

            byte frameType = headerBuf[5];
            if (headerBuf[0] != MAGIC_ID_OF_V1) {
                return "[YKCV1]WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + String.format("%02X", 0x00ff & frameType);
        }

        @Override
        public boolean isValidFeatureCode(String featureCode) {
            return (featureCode != null && featureCode.startsWith("CMD:"));
        }

    }

    private static class YKCV1EncodeSigner implements EncodeSigner {

        private final CRCCalculator crcCalculator = buildCRCCalculatorForEncode();

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
