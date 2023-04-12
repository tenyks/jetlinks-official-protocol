package org.jetlinks.protocol.official.lwm2m;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.defaults.Authenticator;
import org.jetlinks.core.device.*;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MResource;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.*;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.DeviceConfigScope;
import org.jetlinks.core.metadata.types.EnumType;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.serial.PayloadParserSuit;
import org.jetlinks.supports.protocol.serial.PayloadWriterSuit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JetLinksLwM2MDeviceMessageCodec implements DeviceMessageCodec, Authenticator {
    public static final DefaultConfigMetadata CONFIG = new DefaultConfigMetadata(
            "LwM2M认证配置",
            "使用LwM2M进行数据上报时,需要对数据进行加密:encrypt(payload,secureKey);")
            .add("encAlg", "加密算法", "加密算法",
                    new EnumType().addElement(EnumType.Element.of("AES", "AES加密(ECB,PKCS#5)", "加密模式:ECB,填充方式:PKCS#5")),
                    DeviceConfigScope.product)
            .add("secureKey", "密钥", "16位密钥KEY", new PasswordType());

    private RouteMessageCodec   codec;

    public JetLinksLwM2MDeviceMessageCodec(PayloadParserSuit parserSuit, PayloadWriterSuit writerSuit) {
        List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclList = new ArrayList<>();

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerReport)
                        .upstream(true).downstream(false)
                        .group("属性上报").messageType("ReportPropertyMessage")
                        .description("上报物模型属性数据")
                        .example("{\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(ReportPropertyMessage.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
        );

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerReport)
                        .upstream(true).downstream(false)
                        .group("事件上报").messageType("EventMessage")
                        .description("上报物模型事件数据")
                        .example("{\"data\":{\"key\":\"value\"}}").build())
                .thingMessageType(EventMessage.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
        );

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(false).downstream(true)
                        .group("调用功能").messageType("FunctionInvokeMessage")
                        .description("平台下发功能调用指令")
                        .example("{\"messageId\":\"消息ID,回复时需要一致.\"," +
                                "\"functionId\":\"功能标识\"," +
                                "\"inputs\":[{\"name\":\"参数名\",\"value\":\"参数值\"}]}").build())
                .thingMessageType(FunctionInvokeMessage.class)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(true).downstream(false)
                        .group("调用功能").messageType("FunctionInvokeMessageReply")
                        .description("设备响应平台下发的功能调用指令")
                        .example("{\"messageId\":\"消息ID,与下发指令中的messageId一致.\"," +
                                "\"output\":\"输出结果,格式与物模型中定义的类型一致\"").build())
                .thingMessageType(FunctionInvokeMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
        );

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(false).downstream(true)
                        .group("读取属性").messageType("ReadPropertyMessage")
                        .description("平台下发读取物模型属性数据指令")
                        .example("{\"messageId\":\"消息ID,回复时需要一致.\",\"properties\":[\"属性ID\"]}").build())
                .thingMessageType(ReadPropertyMessage.class)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(true).downstream(false)
                        .group("读取属性").messageType("ReadPropertyMessage")
                        .description("对平台下发的读取属性指令进行响应")
                        .example("{\"messageId\":\"消息ID,与读取指令中的ID一致.\",\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(ReadPropertyMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
        );

        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(false).downstream(true)
                        .group("修改属性").messageType("WritePropertyMessage")
                        .description("平台下发修改物模型属性数据指令")
                        .example("{\"messageId\":\"消息ID,回复时需要一致.\",\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(WritePropertyMessage.class)
        );
        dclList.add(new SimpleMessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>()
                .route(LwM2MRoute.builder(LwM2MResource.BinaryAppDataContainerCommand)
                        .upstream(true).downstream(false)
                        .group("修改属性").messageType("WritePropertyMessageReply")
                        .description("对平台下发的修改属性指令进行响应")
                        .example("{\"messageId\":\"消息ID,与修改指令中的ID一致.\",\"properties\":{\"属性ID\":\"属性值\"}}").build())
                .thingMessageType(WritePropertyMessageReply.class)
                .upstreamRoutePredict(this::isUpstreamRouteMatched)
        );

        codec = new RouteMessageCodec(parserSuit, writerSuit, dclList);
    }

    protected boolean isUpstreamRouteMatched(LwM2MRoute route, LwM2MUplinkMessage msg, JSONObject parsedMsg) {
        String messageType = parsedMsg.getString("messageType");
        return route.getResource().equals(msg.getObjectAndResource()) && messageType.equals(route.getMessageType());
    }

    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.LwM2M;
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request,
                                                     @Nonnull DeviceOperator device) {
        if (!(request instanceof LwM2MAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }

        LwM2MAuthenticationRequest req = ((LwM2MAuthenticationRequest) request);

        String ep = req.getEndpoint();
        if (device.getDeviceId().equals(ep)) {
            return Mono.just(AuthenticationResponse.error(201, "设备认证通过"));
        } else {
            return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
        }
    }

    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request,
                                                     @Nonnull DeviceRegistry registry) {
        if (!(request instanceof LwM2MAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }
        LwM2MAuthenticationRequest req = ((LwM2MAuthenticationRequest) request);
        String ep = req.getEndpoint();
        return registry.getDevice(ep)
                .flatMap(device -> authenticate(request, device))
                .defaultIfEmpty(deviceNotFound);
    }

    static AuthenticationResponse deviceNotFound = AuthenticationResponse.error(403, "设备不存在");

    @Nonnull
    @Override
    public Flux<DeviceMessage> decode(@Nonnull MessageDecodeContext context) {
        LwM2MUplinkMessage message = (LwM2MUplinkMessage) context.getMessage();

        return codec.decode(message);
    }

    @Nonnull
    @Override
    public Flux<LwM2MDownlinkMessage> encode(@Nonnull MessageEncodeContext context) {
        DeviceMessage message = (DeviceMessage) context.getMessage();

        return codec.encode(message);
    }

}
