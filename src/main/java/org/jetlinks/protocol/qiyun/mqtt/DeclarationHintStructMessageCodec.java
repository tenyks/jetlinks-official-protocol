package org.jetlinks.protocol.qiyun.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.MessagePayloadType;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.core.utils.BytesUtils;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author v-lizy81
 * @date 2023/4/11 21:21
 */
public class DeclarationHintStructMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(DeclarationHintStructMessageCodec.class);

    private final BinaryMessageCodec    backendCodec;

    private final String                manufacturerCode;

    private final List<MessageCodecDeclaration<MqttRoute, MqttMessage>>    dclList;

    private final Map<Class<? extends DeviceMessage>, MessageCodecDeclaration<MqttRoute, MqttMessage>> dclIdx;

    public DeclarationHintStructMessageCodec(String manufacturerCode,
                                             List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList,
                                             BinaryMessageCodec backendCodec) {
        this.manufacturerCode = manufacturerCode;
        this.dclList = dclList;
        this.backendCodec = backendCodec;

        this.dclIdx = new HashMap<>();
        dclList.forEach(item -> dclIdx.put(item.getThingMessageType(), item));
    }

    public Tuple2<Optional<DeviceMessage>, Optional<MqttMessage>> decode(MessageCodecContext context, MqttMessage message) throws DecoderException {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl = findUpstreamRoute(message);
        if (dcl == null) {
            log.warn("[QiYUnOverMQTT]没有匹配的路由，忽略消息：{}", message);
            return Tuples.of(Optional.empty(), Optional.empty());
        }

        String  hexPayload = message.payloadAsString();
        ByteBuf payloadBuf = BytesUtils.fromHexStrWithTrim(hexPayload);

        if (MessageContentType.STRUCT.equals(dcl.getPayloadContentType())) {
            DeviceMessage devMsg = backendCodec.decode(context, payloadBuf);
            return Tuples.of(Optional.of(devMsg), Optional.empty());
        }

        return Tuples.of(Optional.empty(), Optional.empty());
    }

    public Mono<MqttMessage> encode(MessageCodecContext context, DeviceMessage thingMsg) {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl = findDownstreamRoute(thingMsg);
        if (dcl == null) {
            log.warn("[QiYUnOverMQTT]没有匹配的路由，忽略消息：{}", thingMsg);
            return Mono.empty();
        }

        ByteBuf buf;
        try {
            buf = backendCodec.encode(context, thingMsg);
            if (buf == null) return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }

        MqttRoute route = dcl.getRoute();

        return Mono
                .justOrEmpty(thingMsg.getHeader("productId").map(String::valueOf))
                .switchIfEmpty(context.getDevice(thingMsg.getDeviceId())
                        .flatMap(device -> device.getSelfConfig(DeviceConfigKey.productId))
                ).defaultIfEmpty("null")
                .map(productId -> SimpleMqttMessage.builder()
                        .clientId(thingMsg.getDeviceId())
                        .topic(route.getTopicTemplate().concreteTopic(manufacturerCode, productId, thingMsg.getDeviceId()))
                        .payloadType(MessagePayloadType.HEX)
                        .payload(Unpooled.wrappedBuffer(ByteUtils.toHexStr(buf).getBytes(StandardCharsets.UTF_8)))
                        .build()
                );
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage> findUpstreamRoute(MqttMessage msg) {
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            if (dcl.isRouteAcceptable(msg, null)) {
                return dcl;
            }
        }

        return null;
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage> findDownstreamRoute(DeviceMessage thingMsg) {
        return dclIdx.get(thingMsg.getClass());
    }

}
