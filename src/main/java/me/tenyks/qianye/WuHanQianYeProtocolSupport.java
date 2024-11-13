package me.tenyks.qianye;

import me.tenyks.qiyun.mqtt.DeclarationHintStructMessageCodec;
import me.tenyks.qiyun.mqtt.QiYunCustomizedMqttDeviceMessageCodec;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.route.DownstreamRoutePredictBySvcId;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.official.PluginConfig;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.JsonPathFeatureCodeExtractor;
import org.jetlinks.protocol.official.common.SimpleStructAndMessageMapper;
import org.jetlinks.protocol.official.common.StructAndMessageMapper;
import org.jetlinks.protocol.official.format.DeclarationBasedFormatMessageCodec;
import org.jetlinks.protocol.official.format.FormatStructSuit;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;

import java.util.ArrayList;
import java.util.List;

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

    private static final String     CODE_OF_REQ_METHOD_FIELD = "method";
    private static final String     CODE_OF_RST_CODE_FIELD = "code";
    private static final String     CODE_OF_RST_DESC_FIELD = "message";

    private static final String     CODE_OF_MSG_NO_FIELD = "id";

    public static QiYunCustomizedMqttDeviceMessageCodec buildDeviceMessageCodec(PluginConfig config) {
        DeclarationBasedFormatMessageCodec  formatCodec = buildFormatMessageCodec(config);
        DeclarationHintStructMessageCodec   hintCodec = new DeclarationHintStructMessageCodec(
                buildRouteDeclaration(), formatCodec, null
        );

        return new QiYunCustomizedMqttDeviceMessageCodec(DefaultTransport.MQTT, hintCodec);
    }

    public static DeclarationBasedFormatMessageCodec buildFormatMessageCodec(PluginConfig config) {
        FormatStructSuit structSuit = buildStructSuitV1();
        StructAndMessageMapper mapper = buildMapper(structSuit);
        return new DeclarationBasedFormatMessageCodec(structSuit, mapper);
    }

    public static FormatStructSuit buildStructSuitV1() {
        FormatStructSuit suit = new FormatStructSuit(
                "武汉千烨国标市电插座控制协议",
                "V1.17",
                "document-mqtt-WuHan-QianYe.md",
                new JsonPathFeatureCodeExtractor("method")
        );

        suit.addStructDeclaration(buildCallOfDeviceInfoFunInvStructDcl());
        suit.addStructDeclaration(buildReportDeviceInfoStructDcl());
        suit.addStructDeclaration(buildReportSocketStateStructDcl());
        suit.addStructDeclaration(buildReadSocketStateFunInvStructDcl());
        suit.addStructDeclaration(buildReadSocketStateFunInvReplyStructDcl());
        suit.addStructDeclaration(buildSocketSwitchOnOffFunInvStructDcl());
        suit.addStructDeclaration(buildSocketSwitchOnOffFunInvReplyStructDcl());

        return suit;
    }

    public static List<MessageCodecDeclaration<MqttRoute, MqttMessage>> buildRouteDeclaration() {
        List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList = new ArrayList<>();

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/online/post")
                        .upstream(true).group("上下线")
                        .description("设备上下线消息，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/deviceinfo/post")
                        .upstream(true).group("设备信息")
                        .description("设备上报设备信息，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/property/set_reply")
                        .upstream(true).group("属性")
                        .description("应答设备属性设置指令，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );
        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/property/get_reply")
                        .upstream(true).group("属性")
                        .description("应答设备属性获取，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );
        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/property/post")
                        .upstream(true).group("属性")
                        .description("设备属性上报，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/event/post")
                        .upstream(true).group("事件")
                        .description("设备事件上报，消息负载JSON编码").build())
                .payloadContentType(MessageContentType.JSON)
        );

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/property/set")
                        .downstream(true).group("属性")
                        .description("设备属性设置，消息负载JSON编码").build())
                .downstreamRoutePredict(new DownstreamRoutePredictBySvcId<>("SocketSwitchOnOffFunInv"))
                .payloadContentType(MessageContentType.JSON)
        );
        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("qytech/+/+/thing/property/get")
                        .downstream(true).group("属性")
                        .description("设备属性获取，消息负载JSON编码").build())
                .downstreamRoutePredict(new DownstreamRoutePredictBySvcId<>("ReadSocketStateFunInv"))
                .payloadContentType(MessageContentType.JSON)
        );

        return dclList;
    }

    public static StructAndMessageMapper        buildMapper(FormatStructSuit structSuit) {
        DefaultStructAndThingMapping structAndThingMapping = new DefaultStructAndThingMapping();

        DefaultStructDeclaration target;

        target = (DefaultStructDeclaration) structSuit.getStructDeclaration("呼叫上报设备信息的指令[下行]");
        structAndThingMapping.addMapping(FunctionInvokeMessage.class, "CallOfDeviceInfoFunInv", target);

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
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("呼叫上报设备信息的指令[下行]", "thing.deviceinfo.get", true);

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("CallOfDeviceInfoFunInv"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581220"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.deviceinfo.get"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("协议报文格式", "format","params.format", BaseDataType.STRING);
        structDcl.addField(fieldDcl.setDefaultValue("json").addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 上报设备信息消息[上行]
     */
    private static DefaultStructDeclaration     buildReportDeviceInfoStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报设备信息消息[上行]", "thing.deviceinfo.post", true);

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportDeviceInfo"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581220"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.deviceinfo.post"));

        DefaultFieldDeclaration fieldDcl;
        fieldDcl = buildDataFieldDcl("设备固件版本", "firmwareVersion", "params.version", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("设备唯一标识", "deviceSN", "params.deviceId", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("WIFI的SSID", "wifiSSID", "params.ssid", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("WIFI客户端的IP地址", "wifiIP", "params.ip", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("WIFI客户端的物理地址", "wifiMAC", "params.mac", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("通断状态", "socketStatus", "params.relay", BaseDataType.INT32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property(WuHanQianYeV1DictBookBuilder.buildSocketStatusDict())));

        fieldDcl = buildDataFieldDcl("当前模式", "activeMode", "params.mode", BaseDataType.INT32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property(WuHanQianYeV1DictBookBuilder.buildActiveModeDict())));

        return structDcl;
    }

    /**
     * 上报插座状况消息[上行]
     */
    private static DefaultStructDeclaration     buildReportSocketStateStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报插座状况消息[上行]", "thing.property.post", true);

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReportSocketState"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581821"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.post"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("当前电压", "socketVol", "params.Power.Vol", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("当前电流", "socketCurrent", "params.Power.Current", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("当前电压", "socketPower", "params.Power.ActiveP", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property()));

        fieldDcl = buildDataFieldDcl("通断标志", "socketStatus", "params.Reply.value", BaseDataType.INT32);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.Property(WuHanQianYeV1DictBookBuilder.buildSocketStatusDict())));

        return structDcl;
    }

    /**
     * 读插座的状况指令[下行]
     */
    private static DefaultStructDeclaration     buildReadSocketStateFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读插座的状况指令[下行]", "thing.property.get", true);

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReadSocketStateFunInv"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581821"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.get"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("查询选项", "option", "params.value", BaseDataType.STRING);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput(WuHanQianYeV1DictBookBuilder.buildReadPropertyDict())));

        return structDcl;
    }

    /**
     * 读插座的状况指令响应[上行]
     */
    private static DefaultStructDeclaration     buildReadSocketStateFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读插座的状况指令响应[上行]", "thing.property.get", true);

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("ReadSocketStateFunInvReply"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581821"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.get"));
        structDcl.addField(buildResultCodeFieldDcl());
        structDcl.addField(buildResultDescFieldDcl());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("当前电压", "socketVol", "params.Power.Vol", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        fieldDcl = buildDataFieldDcl("当前电流", "socketCurrent", "params.Power.Current", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        fieldDcl = buildDataFieldDcl("当前电压", "socketPower", "params.Power.ActiveP", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        fieldDcl = buildDataFieldDcl("通断标志", "replyFlag", "params.Reply.value", BaseDataType.FLOAT);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput(WuHanQianYeV1DictBookBuilder.buildRelayStatusDict())));

        return structDcl;
    }

    /**
     * 插座通断指令[下行]
     */
    private static DefaultStructDeclaration     buildSocketSwitchOnOffFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("插座通断指令[下行]", "thing.property.set", true);

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SocketSwitchOnOffFunInv"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581811"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.set"));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("通断选项", "option", "params.Relay.value", BaseDataType.BOOLEAN);
        structDcl.addField(fieldDcl.addMeta(ThingAnnotation.FuncInput()));

        return structDcl;
    }

    /**
     * 插座通断指令响应[上行]
     */
    private static DefaultStructDeclaration     buildSocketSwitchOnOffFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("插座通断指令响应[上行]", "thing.property.set", true);

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.ServiceId("SocketSwitchOnOffFunInvReply"));

        structDcl.addField(buildVersionFieldDcl());
        structDcl.addField(buildMsgNoFieldDcl("581811"));
        structDcl.addField(buildRequestMethodFieldDcl("thing.property.set"));
        structDcl.addField(buildResultCodeFieldDcl());
        structDcl.addField(buildResultDescFieldDcl());

        return structDcl;
    }

    /**
     * 公共字段：报文序号
     */
    private static DefaultFieldDeclaration      buildMsgNoFieldDcl(String defVal) {
        return new DefaultFieldDeclaration("公共字段：消息流水号", CODE_OF_MSG_NO_FIELD, CODE_OF_MSG_NO_FIELD, BaseDataType.STRING)
                .setDefaultValue(defVal);
    }

    /**
     * 公共字段：版本号
     */
    private static DefaultFieldDeclaration      buildVersionFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：版本号", "version", "version", BaseDataType.STRING)
                    .setDefaultValue("1.0");
    }

    /**
     * 公共字段：请求方法
     */
    private static DefaultFieldDeclaration      buildRequestMethodFieldDcl(String method) {
        return new DefaultFieldDeclaration("公共字段：请求方法", CODE_OF_REQ_METHOD_FIELD, CODE_OF_REQ_METHOD_FIELD, BaseDataType.STRING)
                .setDefaultValue(method);
    }

    /**
     * 公共字段：结果状态码
     */
    private static DefaultFieldDeclaration      buildResultCodeFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：结果状态码", CODE_OF_RST_CODE_FIELD, CODE_OF_RST_CODE_FIELD, BaseDataType.INT32)
                .addMeta(ThingAnnotation.FuncOutput());
    }

    /**
     * 公共字段：结果信息
     */
    private static DefaultFieldDeclaration      buildResultDescFieldDcl() {
        return new DefaultFieldDeclaration("公共字段：结果信息", CODE_OF_RST_DESC_FIELD, CODE_OF_RST_DESC_FIELD, BaseDataType.STRING)
                .addMeta(ThingAnnotation.FuncOutput());
    }

    private static DefaultFieldDeclaration      buildDataFieldDcl(String name, String code, String pathInStruct, BaseDataType dataType) {
        return new DefaultFieldDeclaration(name, code, pathInStruct, dataType);
    }

}
