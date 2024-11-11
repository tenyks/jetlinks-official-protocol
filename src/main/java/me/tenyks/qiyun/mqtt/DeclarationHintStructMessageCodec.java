package me.tenyks.qiyun.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.device.DeviceProductOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.MessagePayloadType;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.common.FunctionHandler;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.jetlinks.protocol.official.format.FormatMessageCodec;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/4/11 21:21
 */
public class DeclarationHintStructMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(DeclarationHintStructMessageCodec.class);

    private final BinaryMessageCodec    backendCodec;

    private final FormatMessageCodec    formatBackendCodec;

    private final List<MessageCodecDeclaration<MqttRoute, MqttMessage>>    dclList;

    private MqttRoute     routeForFunctionHandleResponse;

    private final Map<Class<? extends DeviceMessage>, MessageCodecDeclaration<MqttRoute, MqttMessage>> dclIdx;

    private final FunctionHandler funHandler;

    public DeclarationHintStructMessageCodec(List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList,
                                             BinaryMessageCodec backendCodec, FunctionHandler funHandler) {
        this.dclList = dclList;
        this.backendCodec = backendCodec;
        this.formatBackendCodec = null;
        this.funHandler = funHandler;

        this.dclIdx = new HashMap<>();
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            dclIdx.put(dcl.getThingMessageType(), dcl);
            if (dcl.getRoute().isDownstreamForFunctionHandleResponse()) {
                routeForFunctionHandleResponse = dcl.getRoute();
            }
        }

        dclList.forEach(item -> dclIdx.put(item.getThingMessageType(), item));
    }

    public DeclarationHintStructMessageCodec(List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList,
                                             FormatMessageCodec backendCodec, FunctionHandler funHandler) {
        this.dclList = dclList;
        this.backendCodec = null;
        this.formatBackendCodec = backendCodec;
        this.funHandler = funHandler;

        this.dclIdx = new HashMap<>();
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            dclIdx.put(dcl.getThingMessageType(), dcl);
            if (dcl.getRoute().isDownstreamForFunctionHandleResponse()) {
                routeForFunctionHandleResponse = dcl.getRoute();
            }
        }

        dclList.forEach(item -> dclIdx.put(item.getThingMessageType(), item));
    }

    public Tuple2<DeviceMessage, Mono<MqttMessage>>
    decode(MessageCodecContext context, MqttMessage message) throws DecoderException {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl = findUpstreamRoute(message);
        if (dcl == null) {
            log.warn("[QiYunMQTT]没有匹配的路由，忽略消息：{}", message);
            return null;
        }

        ByteBuf payloadBuf = message.getPayload();

        if (MessageContentType.STRUCT.equals(dcl.getPayloadContentType())) {
            DeviceMessage devMsg = backendCodec.decode(context, payloadBuf);

            if (log.isInfoEnabled()) {
                String hexPayload = ByteUtils.toHexStrPretty(payloadBuf);
                log.info("[QiYunMQTT]协议报文解码成功解码为物模型消息：protocol={}, thingMsg={}",
                        hexPayload, devMsg.toJson());
            }

            if (routeForFunctionHandleResponse != null) {
                ByteBuf responsePayload = funHandler.apply(message, devMsg);
                return Tuples.of(devMsg, buildMqttMessage(
                        routeForFunctionHandleResponse, context.getDevice().getProduct().map(DeviceProductOperator::getId),
                        devMsg.getDeviceId(), responsePayload
                ));
            } else {
                log.warn("[QiYunMQTT]缺少FunctionHandleResponse消息的路由，不发送该消息");
                return Tuples.of(devMsg, Mono.empty());
            }
        }
        if (MessageContentType.JSON.equals(dcl.getPayloadContentType())) {
            String payloadJsonStr = ByteUtils.toUTF8Str(payloadBuf);
            DeviceMessage devMsg = formatBackendCodec.decode(context, payloadJsonStr);

            if (log.isInfoEnabled()) {
                log.info("[QiYunMQTT]协议报文解码成功解码为物模型消息：protocol={}, thingMsg={}",
                        payloadJsonStr, devMsg.toJson());
            }

            if (routeForFunctionHandleResponse != null) {
                ByteBuf responsePayload = funHandler.apply(message, devMsg);
                return Tuples.of(devMsg, buildMqttMessage(
                        routeForFunctionHandleResponse, context.getDevice().getProduct().map(DeviceProductOperator::getId),
                        devMsg.getDeviceId(), responsePayload
                ));
            } else {
                log.warn("[QiYunMQTT]缺少FunctionHandleResponse消息的路由，不发送该消息");
                return Tuples.of(devMsg, Mono.empty());
            }
        }

        return null;
    }

    public Mono<MqttMessage> encode(MessageCodecContext context, DeviceMessage thingMsg) {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl = findDownstreamRoute(thingMsg);
        if (dcl == null) {
            log.warn("[QiYunMQTT]没有匹配的路由，忽略消息：{}", thingMsg);
            return Mono.empty();
        }

        if (MessageContentType.STRUCT.equals(dcl.getPayloadContentType())) {
            ByteBuf buf;
            try {
                buf = backendCodec.encode(context, thingMsg);
                if (buf == null) return Mono.empty();
            } catch (Exception e) {
                return Mono.error(e);
            }

            Mono<String> prodId = Mono.justOrEmpty(thingMsg.getHeader("productId").map(String::valueOf))
                    .switchIfEmpty(context.getDevice(thingMsg.getDeviceId())
                            .flatMap(device -> device.getSelfConfig(DeviceConfigKey.productId)));

            return buildMqttMessage(dcl.getRoute(), prodId, thingMsg.getDeviceId(), buf);
        }

        if (MessageContentType.JSON.equals(dcl.getPayloadContentType())) {
            String buf;
            try {
                buf = formatBackendCodec.encode(context, thingMsg);
                if (buf == null) return Mono.empty();
            } catch (Exception e) {
                return Mono.error(e);
            }

            Mono<String> prodId = Mono.justOrEmpty(thingMsg.getHeader("productId").map(String::valueOf))
                    .switchIfEmpty(context.getDevice(thingMsg.getDeviceId())
                            .flatMap(device -> device.getSelfConfig(DeviceConfigKey.productId)));

            return buildMqttMessage(dcl.getRoute(), prodId, thingMsg.getDeviceId(), Unpooled.wrappedBuffer(buf.getBytes(StandardCharsets.UTF_8)));
        }

        return Mono.empty();
    }

    private Mono<MqttMessage> buildMqttMessage(final MqttRoute route, Mono<String> prodId, final String deviceId,
                                               @Nullable ByteBuf buf) {
        if (buf == null) return Mono.empty();

        return prodId.defaultIfEmpty("null")
                .map(productId -> SimpleMqttMessage.builder()
                        .clientId(deviceId)
                        .topic(route.getTopicTemplate().concreteTopic(productId, deviceId))
                        .payloadType(MessagePayloadType.HEX)
//                        .payload(Unpooled.wrappedBuffer(ByteUtils.toHexStr(buf).getBytes(StandardCharsets.UTF_8)))
                        .payload(buf)
                        .build()
                );
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage> findUpstreamRoute(MqttMessage msg) {
        //TODO 优化性能
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            if (dcl.getRoute().isUpstream() && dcl.isRouteAcceptableUpstream(msg, null)) {
                return dcl;
            }
        }

        return null;
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage> findDownstreamRoute(DeviceMessage thingMsg) {
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            if (dcl.getRoute().isDownstream() && dcl.isRouteAcceptableDownstream(thingMsg)) {
                return dcl;
            }
        }

        //TODO 优化性能
        return dclIdx.get(thingMsg.getClass());
    }

}
