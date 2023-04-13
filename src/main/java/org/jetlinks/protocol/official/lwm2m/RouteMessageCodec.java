package org.jetlinks.protocol.official.lwm2m;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.codec.lwm2m.SimpleLwM2MDownlinkMessage;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.serial.PayloadParser;
import org.jetlinks.supports.protocol.serial.PayloadParserSuit;
import org.jetlinks.supports.protocol.serial.PayloadWriterSuit;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/4/11 21:21
 */
public class RouteMessageCodec {

    private PayloadParserSuit parserSuit;

    private PayloadWriterSuit writerSuit;

    private List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>>    dclList;

    private Map<Class<? extends DeviceMessage>, MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclIdx;

    public RouteMessageCodec(PayloadParserSuit parserSuit, PayloadWriterSuit writerSuit,
                             List<MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>> dclList) {
        this.parserSuit = parserSuit;
        this.writerSuit = writerSuit;
        this.dclList = dclList;

        this.dclIdx = new HashMap<>();
        dclList.forEach(item -> dclIdx.put(item.getThingMessageType(), item));
    }

    public Flux<DeviceMessage> decode(LwM2MUplinkMessage message) {
        Tuple2<JSONObject, PayloadParser> parsedRst = null;
        try {
            parsedRst = parseMessage(message.getPath(), message.payloadAsBytes());
        } catch (IOException e) {
            return Flux.error(e);
        }
        if (parsedRst == null) return Flux.empty();

        MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl;
        dcl = findUpstreamRoute(message, parsedRst.getT1());

        if (dcl == null) return Flux.empty();

        DeviceMessage thingMsg;
        try {
            thingMsg = dcl.createThingMessage();
        } catch (IllegalAccessException | InstantiationException e) {
            return Flux.error(e);
        }
        copyTo(parsedRst.getT1(), thingMsg);

        return Flux.just(thingMsg);
    }

    public Flux<LwM2MDownlinkMessage> encode(DeviceMessage thingMsg) {
        MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl = findDownstreamRoute(thingMsg);
        if (dcl == null) return Flux.empty();

        byte[] buf = null;
        try {
            buf = writerSuit.buildPayload(dcl.getMessageType(), thingMsg.toJson());
            if (buf == null) return Flux.empty();
        } catch (IOException e) {
            return Flux.error(e);
        }

        SimpleLwM2MDownlinkMessage msg = new SimpleLwM2MDownlinkMessage();
        msg.setResource(dcl.getRoute().getResource());
        msg.setPayload(Unpooled.wrappedBuffer(buf));
//        msg.setMessageId(thingMsg.getMessageId());

        return Flux.just(msg);
    }

    protected Tuple2<JSONObject, PayloadParser> parseMessage(String uriOrTopic, byte[] payload) throws IOException {
        Tuple2<JSONObject, PayloadParser> parsedTuple;
        parsedTuple = parserSuit.parse(uriOrTopic, payload);
        return parsedTuple;
    }

    protected MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>
    findUpstreamRoute(LwM2MUplinkMessage msg, JSONObject parseMsg) {
        for (MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage> dcl : dclList) {
            if (dcl.isRouteAcceptable(msg, parseMsg)) {
                return dcl;
            }
        }

        return null;
    }

    protected MessageCodecDeclaration<LwM2MRoute, LwM2MUplinkMessage>
    findDownstreamRoute(DeviceMessage thingMsg) {
        return dclIdx.get(thingMsg.getClass());
    }

    protected void copyTo(JSONObject parsedMsg, DeviceMessage thingMsg) {
        FastBeanCopier.copy(parsedMsg, thingMsg);
    }

}
