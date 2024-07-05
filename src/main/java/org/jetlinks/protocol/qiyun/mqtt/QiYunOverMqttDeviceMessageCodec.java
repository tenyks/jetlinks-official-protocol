package org.jetlinks.protocol.qiyun.mqtt;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.SneakyThrows;
import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DisconnectDeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.route.MqttRoute;
import org.jetlinks.protocol.common.FunctionHandler;
import org.jetlinks.protocol.official.TopicPayload;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.supports.protocol.SimpleMessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageCodecDeclaration;
import org.jetlinks.supports.protocol.codec.MessageContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.Nonnull;
import javax.validation.constraints.Null;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private static final Logger log = LoggerFactory.getLogger(QiYunOverMqttDeviceMessageCodec.class);

    private final Transport         transport;

    private final DeclarationHintStructMessageCodec  codec;

    private final List<MqttRoute>   routes;

    public QiYunOverMqttDeviceMessageCodec(@Nonnull Transport transport,
                                           @Nonnull String manufacturerCode,
                                           @Nonnull BinaryMessageCodec backendCodec,
                                           @Nonnull FunctionHandler funHandler) {
        this.transport = transport;

        List<MessageCodecDeclaration<MqttRoute, MqttMessage>> dclList = new ArrayList<>();

        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("tt_v1/+/+/+/uplink")
                        .upstream(true)
                        .group("OverMQTT上行的消息")
                        .description("通过MQTT协议封装上行传输的消息，消息负载HEX编码")
                        .build())
                .upstreamRoutePredict((route, message, payload) -> {
                    String topic = message.getTopic();
                    return topic.startsWith("tt_v1") && topic.endsWith("/uplink");
                })
                .payloadContentType(MessageContentType.STRUCT)
        );
        dclList.add(new SimpleMessageCodecDeclaration<MqttRoute, MqttMessage>()
                .route(MqttRoute.builder("tt_v1/+/+/+/downlink")
                        .downstreamForFunctionHandleResponse(true)
                        .group("OverMQTT下行的消息")
                        .description("通过MQTT协议封装下行行传输的消息，消息负载HEX编码")

                        .build())
                .thingMessageType(FunctionInvokeMessage.class)
                .payloadContentType(MessageContentType.STRUCT)
        );

        this.routes = dclList.stream().map(MessageCodecDeclaration::getRoute).collect(Collectors.toList());
        this.codec = new DeclarationHintStructMessageCodec(manufacturerCode, dclList, backendCodec, funHandler);
    }

    @Override
    public Transport        getSupportTransport() {
        return transport;
    }

    public List<MqttRoute>  collectRoutes() {
        return routes;
    }

    @Nonnull
    @Override
    public Mono<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        MqttMessage message = (MqttMessage) context.getMessage();

        Tuple2<DeviceMessage, Mono<MqttMessage>> decodeRst;
        try {
            decodeRst = codec.decode(context, message);
            if (decodeRst != null) {

                return decodeRst.getT2()
                        .map(reply -> doReply(context, reply))
                        .flatMap((flag) -> flag.map(f -> decodeRst.getT1()));
            } else {
                log.warn("[QiYunOverMQTT]解码MQTT消息不成功：{}", message.print());
                return Mono.empty();
            }
        } catch (DecoderException e) {
            log.error("[QiYunOverMQTT]解码MQTT消息负载异常失败：{}", message.print(), e);
            return Mono.empty();
        }
    }

    @Nonnull
    @Override
    public Mono<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.defer(() -> {
            Message message = context.getMessage();

            if (message instanceof DisconnectDeviceMessage) {
                log.info("[QiYunOverMQTT]关闭MQTT会话：{}", (context.getDevice() != null ? context.getDevice() : "NO_DEVICE"));
                return ((ToDeviceMessageContext) context).disconnect().then(Mono.empty());
            }

            if (message instanceof DeviceMessage) {
                DeviceMessage deviceMessage = ((DeviceMessage) message);

                return codec.encode(context, deviceMessage);
            } else {
                log.warn("[QiYunOverMQTT]不支持的类型，忽略该消息：{}", message.toJson());
                return Mono.empty();
            }
        });
    }

    private Mono<Boolean> doReply(MessageCodecContext context, MqttMessage reply) {
        if (context instanceof FromDeviceMessageContext) {
            if (log.isInfoEnabled()) {
                log.debug("[QiYunOverMQTT]下发FunctionHandleResponse消息：{}", reply.print());
            }

            return ((FromDeviceMessageContext) context)
                    .getSession()
                    .send(reply);
        } else if (context instanceof ToDeviceMessageContext) {
            if (log.isInfoEnabled()) {
                log.debug("[QiYunOverMQTT]下发FunctionHandleResponse消息：{}", reply.print());
            }

            return ((ToDeviceMessageContext) context)
                    .sendToDevice(reply);
        }

        return Mono.empty();
    }
}
