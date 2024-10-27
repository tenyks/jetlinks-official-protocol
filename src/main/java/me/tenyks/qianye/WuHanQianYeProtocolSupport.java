package me.tenyks.qianye;

import me.tenyks.qiyun.protocol.YKCV1APIBuilder;
import me.tenyks.qiyun.protocol.YKCV1DictBookBuilder;
import me.tenyks.qiyun.protocol.YKCV1ReplyResponderBuilder;
import me.tenyks.qiyun.tcp.QiYunStrategyBaseTcpDeviceMessageCodec;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessageReply;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.common.mapping.ThingItemMapping;
import org.jetlinks.protocol.common.mapping.ThingValueNormalizations;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.AbstractIntercommunicateStrategy;
import org.jetlinks.protocol.official.common.JsonPathFeatureCodeExtractor;
import org.jetlinks.protocol.official.common.SimpleStructAndMessageMapper;
import org.jetlinks.protocol.official.common.StructAndMessageMapper;
import org.jetlinks.protocol.official.format.DeclarationBasedFormatMessageCodec;
import org.jetlinks.protocol.official.format.FormatStructSuit;

/**
 * 武汉千烨国标市电插座协议
 *
 * 参考：《物联网开发手册-适用于esp芯片类新产品》，版本V1.17
 *
 * @author v-lizy81
 * @date 2024/10/27 11:10
 */
public class WuHanQianYeProtocolSupport {

    public static final String      NAME_AND_VER = "WHQY_V1.17";

    private static final String     CODE_OF_REQ_METHOD_FIELD = "REQ_METHOD";
    private static final String     CODE_OF_RST_CODE_FIELD = "RST_CODE";
    private static final String     CODE_OF_RST_DESC_FIELD = "RST_DESC";

    private static final String     CODE_OF_MSG_NO_FIELD = "MSG_NO";

    public static QiYunStrategyBaseTcpDeviceMessageCodec    buildDeviceMessageCodec(PluginConfig config) {
        DeclarationBasedBinaryMessageCodec bmCodec = buildBinaryMessageCodec(config);



        return new QiYunStrategyBaseTcpDeviceMessageCodec(bmCodec, strategy);
    }

    public static DeclarationBasedFormatMessageCodec        buildBinaryMessageCodec(PluginConfig config) {
        FormatStructSuit structSuit = buildStructSuitV1();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedFormatMessageCodec(structSuit, mapper);
    }

    public static FormatStructSuit buildStructSuitV1() {
        FormatStructSuit suit = new FormatStructSuit(
                "云快充新能源汽车充电桩协议",
                "V1.6",
                "document-mqtt-YKCV1.md",
                new JsonPathFeatureCodeExtractor("method")
        );

        suit.addStructDeclaration(buildReportDeviceInfoStructDcl());
        suit.addStructDeclaration(buildReportSocketStateStructDcl());
        suit.addStructDeclaration(buildReadSocketStateFunInvStructDcl());
        suit.addStructDeclaration(buildReadSocketStateFunInvReplyStructDcl());
        suit.addStructDeclaration(buildSocketSwitchOnOffFunInvStructDcl());
        suit.addStructDeclaration(buildSocketSwitchOnOffFunInvReplyStructDcl());

        return suit;
    }

    public static StructAndMessageMapper        buildMapper(FormatStructSuit structSuit) {
        DefaultStructAndThingMapping structAndThingMapping = new DefaultStructAndThingMapping();

        DefaultStructDeclaration target;

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("呼叫上报设备信息的指令[下行]");
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "CallOfDeviceInfo", target);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("上报设备信息消息[上行]");
        structAndThingMapping.addMapping(target, ReportPropertyMessage.class);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("上报插座状况消息[上行]");
        structAndThingMapping.addMapping(target, ReportPropertyMessage.class);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("插座通断指令[下行]");
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "SocketSwitchOnOffFunInv", target);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("插座通断指令响应[上行]");
        structAndThingMapping.addMapping(target, FunctionInvokeMessageReply.class);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("读插座的状况指令[下行]");
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "ReadSocketStateFunInv", target);

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("读插座的状况指令响应[上行]");
        structAndThingMapping.addMapping(target, FunctionInvokeMessageReply.class);

        DefaultFieldAndPropertyMapping fieldAndPropertyMapping = new DefaultFieldAndPropertyMapping();
        DefaultFieldValueAndPropertyMapping fieldValueAndPropertyMapping = new DefaultFieldValueAndPropertyMapping();

        return new SimpleStructAndMessageMapper(structAndThingMapping, fieldAndPropertyMapping, fieldValueAndPropertyMapping);
    }

    /**
     * 呼叫上报设备信息的指令[下行]
     */
    private static DefaultStructDeclaration     buildCallOfDeviceInfoFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("呼叫上报设备信息的指令[下行]", "thing.deviceinfo.get");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CallOfDeviceInfo"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.deviceinfo.get"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("协议报文格式", "params.format", BaseDataType.STRING);
        structDcl.addField(fieldDcl.setDefaultValue("json"));

        return structDcl;
    }

    /**
     * 上报设备信息消息[上行]
     */
    private static DefaultStructDeclaration     buildReportDeviceInfoStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报设备信息消息[上行]", "thing.deviceinfo.post");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportDeviceInfo"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.deviceinfo.post"));

        structDcl.addField(buildDataFieldDcl("设备固件版本", "params.version", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("设备唯一标识", "params.deviceId", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("WIFI的SSID", "params.ssid", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("WIFI客户端的IP地址", "params.ip", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("WIFI客户端的物理地址", "params.mac", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("通断状态", "params.relay", BaseDataType.STRING));
        structDcl.addField(buildDataFieldDcl("当前模式", "params.mode", BaseDataType.STRING));

        return structDcl;
    }

    /**
     * 上报插座状况消息[上行]
     */
    private static DefaultStructDeclaration     buildReportSocketStateStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报插座状况消息[上行]", "thing.property.get");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportSocketState"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.get"));
        structDcl.addField(buildResultCodeFieldDcl());
        structDcl.addField(buildResultDescFieldDcl());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("当前电压", "params.Power.Vol", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketVol")));

        fieldDcl = buildDataFieldDcl("当前电流", "params.Power.Current", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketCurrent")));

        fieldDcl = buildDataFieldDcl("当前电压", "params.Power.ActiveP", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketPower")));

        fieldDcl = buildDataFieldDcl("通断标志", "params.Reply.value", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketPower", WuHanQianYeV1DictBookBuilder.buildReplyStatusDict())));

        return structDcl;
    }

    /**
     * 读插座的状况指令[下行]
     */
    private static DefaultStructDeclaration     buildReadSocketStateFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("查询插座的状况指令[下行]", "thing.property.get");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReadSocketStateFunInv"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.get"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("查询选项", "params.value", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("option", WuHanQianYeV1DictBookBuilder.buildReadPropertyDict())));

        return structDcl;
    }

    /**
     * 读插座的状况指令响应[上行]
     */
    private static DefaultStructDeclaration     buildReadSocketStateFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读插座的状况指令响应[上行]", "thing.property.get");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReadSocketStateFunInvReply"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.get"));
        structDcl.addField(buildResultCodeFieldDcl());
        structDcl.addField(buildResultDescFieldDcl());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("当前电压", "params.Power.Vol", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketVol")));

        fieldDcl = buildDataFieldDcl("当前电流", "params.Power.Current", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketCurrent")));

        fieldDcl = buildDataFieldDcl("当前电压", "params.Power.ActiveP", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketPower")));

        fieldDcl = buildDataFieldDcl("通断标志", "params.Reply.value", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("socketPower", WuHanQianYeV1DictBookBuilder.buildReplyStatusDict())));

        return structDcl;
    }

    /**
     * 插座通断指令[下行]
     */
    private static DefaultStructDeclaration     buildSocketSwitchOnOffFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("插座通断指令[下行]", "thing.property.set");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SocketSwitchOnOffFunInv"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.set"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("通断选项", "params.Relay.value", BaseDataType.BOOLEAN);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput("option")));

        return structDcl;
    }

    /**
     * 插座通断指令响应[上行]
     */
    private static DefaultStructDeclaration     buildSocketSwitchOnOffFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("插座通断指令响应[上行]", "thing.property.set");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SocketSwitchOnOffFunInvReply"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.set"));
        structDcl.addField(buildResultCodeFieldDcl());
        structDcl.addField(buildResultDescFieldDcl());

        return structDcl;
    }

    /**
     * 公共字段：报文序号
     */
    private static DefaultFieldDeclaration buildMsgNoFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：消息流水号", CODE_OF_MSG_NO_FIELD, BaseDataType.STRING)
                .setDefaultValue(0);
    }

    /**
     * 公共字段：版本号
     */
    private static DefaultFieldDeclaration buildVersionFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：版本号", "version", BaseDataType.STRING)
                    .setDefaultValue("1.0");
    }

    /**
     * 公共字段：请求方法
     */
    private static DefaultFieldDeclaration buildRequestMethodFieldDcl(String method) {
        return new DefaultFieldDeclaration("公共字段：请求方法", CODE_OF_REQ_METHOD_FIELD, BaseDataType.STRING)
                .setDefaultValue(method);
    }

    /**
     * 公共字段：结果状态码
     */
    private static DefaultFieldDeclaration buildResultCodeFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：结果状态码", CODE_OF_RST_CODE_FIELD, BaseDataType.INT32);
    }

    /**
     * 公共字段：结果信息
     */
    private static DefaultFieldDeclaration buildResultDescFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：结果信息", CODE_OF_RST_DESC_FIELD, BaseDataType.STRING);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType) {
        return new DefaultFieldDeclaration(name, code, dataType);
    }

}
