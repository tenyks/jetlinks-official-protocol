package org.jetlinks.protocol.qiyun.mqtt;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DisconnectDeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.official.TopicPayload;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 祺云标准协议之OverMQTT传输，用于MQTT边缘网关或DTU对接
 * <pre>
 *      下行Topic:
 *      tt_v1/{厂家编码}/{产品标识}/{设备标识}/downlink
 *
 *      上行Topic:
 *      tt_v1/{厂家编码}/{产品标识}/{设备标识}/downlink
 * </pre>
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/24
 * @since V3.1.0
 */
public class QiYunOverMqttDeviceMessageCodec implements DeviceMessageCodec {

    private final Transport transport;

    private final DeclarationHintStructMessageCodec  codec;

    private final List<MqttRoute>   routes;

    public QiYunOverMqttDeviceMessageCodec(Transport transport, String manufacturerCode,
                                           BinaryMessageCodec backendCodec) {
        this.transport = transport;

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
                .thingMessageType(FunctionInvokeMessage.class)
                .payloadContentType(MessageContentType.STRUCT)
        );

        this.routes = dclList.stream().map(MessageCodecDeclaration::getRoute).collect(Collectors.toList());
        this.codec = new DeclarationHintStructMessageCodec(manufacturerCode, dclList, backendCodec);
    }

    @Override
    public Transport getSupportTransport() {
        return transport;
    }

    public List<MqttRoute>  collectRoutes() {
        return routes;
    }

    @Nonnull
    @Override
    public Mono<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        MqttMessage message = (MqttMessage) context.getMessage();

//        return codec.decode(context, message);
        return Mono.empty();
    }

    @Nonnull
    @Override
    public Mono<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.defer(() -> {
            Message message = context.getMessage();

            if (message instanceof DisconnectDeviceMessage) {
                return ((ToDeviceMessageContext) context).disconnect().then(Mono.empty());
            }

            if (message instanceof DeviceMessage) {
                DeviceMessage deviceMessage = ((DeviceMessage) message);

                return codec.encode(context, deviceMessage);
            } else {
                return Mono.empty();
            }
        });
    }

    private Mono<Void> doReply(MessageCodecContext context, TopicPayload reply) {
        if (context instanceof FromDeviceMessageContext) {
            return ((FromDeviceMessageContext) context)
                    .getSession()
                    .send(SimpleMqttMessage
                            .builder()
                            .topic(reply.getTopic())
                            .payload(reply.getPayload())
                            .build())
                    .then();
        } else if (context instanceof ToDeviceMessageContext) {
            return ((ToDeviceMessageContext) context)
                    .sendToDevice(SimpleMqttMessage
                            .builder()
                            .topic(reply.getTopic())
                            .payload(reply.getPayload())
                            .build())
                    .then();
        }
        return Mono.empty();
    }
}
