package org.jetlinks.protocol.qiyun.mqtt;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.codec.lwm2m.SimpleLwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<DeviceMessage> decode(MessageCodecContext context, MqttMessage message) {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl;
        dcl = findUpstreamRoute(message);

        if (dcl == null) {
            log.info("[OverMQTT]没有匹配的路由，忽略消息：{}", message);
            return Flux.empty();
        }

        if (MessageContentType.STRUCT.equals(dcl.getPayloadContentType())) {
            DeviceMessage devMsg = backendCodec.decode(context, message.getPayload());
            return devMsg != null ? Flux.just(devMsg) : Flux.empty();
        }

        return Flux.empty();
    }

    public Mono<MqttMessage> encode(MessageCodecContext context, DeviceMessage thingMsg) {
        MessageCodecDeclaration<MqttRoute, MqttMessage> dcl = findDownstreamRoute(thingMsg);
        if (dcl == null) {
            log.info("[OverMQTT]没有匹配的路由，忽略消息：{}", thingMsg);
            return Mono.empty();
        }

        ByteBuf buf;
        MqttRoute route = dcl.getRoute();
        try {
            buf = backendCodec.encode(context, thingMsg);
            if (buf == null) return Mono.empty();

            DeviceOperator device = context.getDevice();
            String productId = device.getProduct().block().getId();
            String deviceId = device.getDeviceId();

            SimpleMqttMessage msg = new SimpleMqttMessage();
            msg.setTopic(route.getTopicTemplate().concreteTopic(manufacturerCode, productId, deviceId));
            msg.setPayload(buf);

            return Flux.just(msg);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage>
    findUpstreamRoute(MqttMessage msg) {
        for (MessageCodecDeclaration<MqttRoute, MqttMessage> dcl : dclList) {
            if (dcl.isRouteAcceptable(msg, null)) {
                return dcl;
            }
        }

        return null;
    }

    protected MessageCodecDeclaration<MqttRoute, MqttMessage>
    findDownstreamRoute(DeviceMessage thingMsg) {
        return dclIdx.get(thingMsg.getClass());
    }

}
