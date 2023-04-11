package org.jetlinks.protocol.official.lwm2m;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.route.Route;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.serial.PayloadParserSuit;
import org.jetlinks.supports.protocol.serial.PayloadWriterSuit;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author v-lizy81
 * @date 2023/4/11 21:21
 */
public class RouteMessageCodec {

    private PayloadParserSuit parserSuit;

    private PayloadWriterSuit writerSuit;

    private List<MessageCodecDeclaration>    dclList;

    public Flux<DeviceMessage> decode(LwM2MUplinkMessage message) {
        return null;
    }

    public Flux<LwM2MDownlinkMessage> encode(DeviceMessage thingMsg) {
        return null;
    }

    protected JSONObject parseMessage(LwM2MUplinkMessage message) {
        return null;
    }

    protected Route findRoute(LwM2MUplinkMessage msg, JSONObject parseMsg) {
        return null;
    }

    protected void copyTo(JSONObject parsedMsg, DeviceMessage thingMsg) {

    }

}
