package org.jetlinks.protocol.qiyun.mqtt;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.lwm2m.LwM2MOperation;
import org.jetlinks.core.message.codec.lwm2m.LwM2MResource;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.*;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.common.IntercommunicateStrategy;
import org.jetlinks.protocol.official.lwm2m.DeclarationHintStructMessageCodec;
import org.jetlinks.protocol.official.lwm2m.SimpleLwM2M11DeviceMessageCodec;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * MQTT消息编解码器
 * 消息负载为字节
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/1
 * @since V3.1.0
 */
public class StructMqttDeviceMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(StructMqttDeviceMessageCodec.class);

    private final DeclarationHintStructMessageCodec codec;

    public StructMqttDeviceMessageCodec(BinaryMessageCodec backend, IntercommunicateStrategy strategy) {
        List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclList = new ArrayList<>();

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerReport.getPath())
                        .upstreamResponse()
                        .group("属性上报").messageType("ReportPropertyMessage")
                        .description("上报物模型属性数据")
                        .example("{\"messageType\":\"ReportPropertyMessage\",\"properties\":{\"属性ID\":\"属性值\"}}")
                        .build())
                .thingMessageType(ReportPropertyMessage.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerReport.getPath())
                        .upstreamResponse()
                        .group("事件上报").messageType("EventMessage")
                        .description("上报物模型事件数据")
                        .example("{\"messageType\":\"EventMessage\",\"data\":{\"key\":\"value\"}}")
                        .build())
                .thingMessageType(EventMessage.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand.getPath())
                        .downstreamRequest(LwM2MOperation.Write)
                        .group("调用功能").messageType("FunctionInvokeMessage")
                        .description("平台下发功能调用指令")
                        .example("{\"messageType\":\"FunctionInvokeMessage\",\"messageId\":\"消息ID,回复时需要一致.\"," +
                                "\"functionId\":\"功能标识\"," +
                                "\"inputs\":[{\"name\":\"参数名\",\"value\":\"参数值\"}]}")
                        .build())
                .thingMessageType(FunctionInvokeMessage.class)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder("/19/*/0")
                        .upstreamResponse()
                        .group("调用功能").messageType("FunctionInvokeMessageReply")
                        .description("设备响应平台下发的功能调用指令")
                        .example("{\"messageType\":\"FunctionInvokeMessageReply\",\"messageId\":\"消息ID,与下发指令中的messageId一致.\"," +
                                "\"output\":\"输出结果,格式与物模型中定义的类型一致\"")
                        .build())
                .thingMessageType(FunctionInvokeMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand.getPath())
                        .downstreamRequest(LwM2MOperation.Write)
                        .group("读取属性").messageType("ReadPropertyMessage")
                        .description("平台下发读取物模型属性数据指令")
                        .example("{\"messageType\":\"ReadPropertyMessage\",\"messageId\":\"消息ID,回复时需要一致.\",\"properties\":[\"属性ID\"]}")
                        .build())
                .thingMessageType(ReadPropertyMessage.class)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand.getPath())
                        .upstreamResponse()
                        .group("读取属性").messageType("ReadPropertyMessageReply")
                        .description("对平台下发的读取属性指令进行响应")
                        .example("{\"messageType\":\"ReadPropertyMessageReply\",\"messageId\":\"消息ID,与读取指令中的ID一致.\",\"properties\":{\"属性ID\":\"属性值\"}}")
                        .build())
                .thingMessageType(ReadPropertyMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand.getPath())
                        .downstreamRequest(LwM2MOperation.Write)
                        .group("修改属性").messageType("WritePropertyMessage")
                        .description("平台下发修改物模型属性数据指令")
                        .example("{\"messageType\":\"WritePropertyMessage\",\"messageId\":\"消息ID,回复时需要一致.\",\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(WritePropertyMessage.class)
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand.getPath())
                        .upstreamResponse()
                        .group("修改属性").messageType("WritePropertyMessageReply")
                        .description("对平台下发的修改属性指令进行响应")
                        .example("{\"messageType\":\"WritePropertyMessageReply\",\"messageId\":\"消息ID,与修改指令中的ID一致.\",\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(WritePropertyMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
                .payloadContentType(MessageContentType.STRUCT)
        );

        this.codec = new DeclarationHintStructMessageCodec(dclList, backend);
    }

    protected boolean isUpstreamRouteMatched(LwM2MRoute route, LwM2MUplinkMessage msg, JSONObject parsedMsg) {
        return route.isUpstream() && route.acceptPath(msg.getPath());
    }

    public Transport getSupportTransport() {
        return DefaultTransport.LwM2M;
    }

    @Nonnull
    public Flux<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        LwM2MUplinkMessage message = (LwM2MUplinkMessage) context.getMessage();

        return codec.decode(context, message);
    }

    public Flux<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        DeviceMessage message = (DeviceMessage) context.getMessage();

        return codec.encode(context, message);
    }

}
