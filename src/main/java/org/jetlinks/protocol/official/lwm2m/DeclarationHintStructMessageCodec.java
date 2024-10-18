package org.jetlinks.protocol.official.lwm2m;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.codec.lwm2m.SimpleLwM2MDownlinkMessage;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/4/11 21:21
 */
public class DeclarationHintStructMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(DeclarationHintStructMessageCodec.class);

    private final BinaryMessageCodec backendCodec;

    private final List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>>    dclList;

    private final Map<Class<? extends DeviceMessage>, MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclIdx;

    public DeclarationHintStructMessageCodec(List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclList,
                                             BinaryMessageCodec backendCodec) {
        this.dclList = dclList;
        this.backendCodec = backendCodec;

        this.dclIdx = new HashMap<>();
        dclList.forEach(item -> dclIdx.put(item.getThingMessageType(), item));
    }

    public Flux<DeviceMessage> decode(MessageCodecContext context, LwM2MUplinkMessage message) {
        MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl;
        dcl = findUpstreamRoute(message);

        if (dcl == null) {
            log.info("[LwM2M]没有匹配的路由，忽略消息：{}", message);
            return Flux.empty();
        }

        if (MessageContentType.STRUCT.equals(dcl.getPayloadContentType())) {
            DeviceMessage devMsg = backendCodec.decode(context, message.getPayload());
            return devMsg != null ? Flux.just(devMsg) : Flux.empty();
        }

        return Flux.empty();
    }

    public Flux<LwM2MDownlinkMessage> encode(MessageCodecContext context, DeviceMessage thingMsg) {
        MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl = findDownstreamRoute(thingMsg);
        if (dcl == null) {
            log.info("[LwM2M]没有匹配的路由，忽略消息：{}", thingMsg);
            return Flux.empty();
        }

        ByteBuf buf;
        LwM2MRoute route = dcl.getRoute();
        try {
            buf = backendCodec.encode(context, thingMsg);
            if (buf == null) return Flux.empty();

            SimpleLwM2MDownlinkMessage msg = new SimpleLwM2MDownlinkMessage(route);
            msg.setPayload(buf);

            return Flux.just(msg);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }


    protected MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>
    findUpstreamRoute(LwM2MUplinkMessage msg) {
        for (MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl : dclList) {
            if (dcl.isRouteAcceptable(msg, null)) {
                return dcl;
            }
        }

        return null;
    }

    protected MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>
    findDownstreamRoute(DeviceMessage thingMsg) {
        return dclIdx.get(thingMsg.getClass());
    }

}
