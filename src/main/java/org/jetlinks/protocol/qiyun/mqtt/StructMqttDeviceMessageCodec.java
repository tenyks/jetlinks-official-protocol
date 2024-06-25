package org.jetlinks.protocol.qiyun.mqtt;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.common.IntercommunicateStrategy;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
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
        List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList = new ArrayList<>();

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("tt_v1/+/+/+/uplink")
                        .upstream(true)
                        .group("OverMQTT上行的消息")
                        .description("通过MQTT协议封装上行传输的消息")
                        .build())
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("tt_v1/+/+/+/downlink")
                        .downstream(true)
                        .group("OverMQTT下行的消息")
                        .description("通过MQTT协议封装下行行传输的消息")
                        .build())
                .payloadContentType(MessageContentType.STRUCT)
        );

        this.codec = new DeclarationHintStructMessageCodec(dclList, backend);
    }

    public Transport getSupportTransport() {
        return DefaultTransport.MQTT;
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
