package org.jetlinks.protocol.qiyun.mqtt;

import io.netty.buffer.Unpooled;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DisconnectDeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.jetlinks.core.message.codec.mqtt.SimpleMqttMessage;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.core.metadata.types.StringType;
import org.jetlinks.protocol.official.FunctionalTopicHandlers;
import org.jetlinks.protocol.official.ObjectMappers;
import org.jetlinks.protocol.official.TopicPayload;
import org.jetlinks.protocol.official.core.TopicMessageCodec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

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

    private final StructMqttDeviceMessageCodec  codec;

    public QiYunOverMqttDeviceMessageCodec(Transport transport, StructMqttDeviceMessageCodec  codec) {
        this.transport = transport;
        this.codec = codec;
    }

    @Override
    public Transport getSupportTransport() {
        return null;
    }

    @Nonnull
    @Override
    public Flux<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        MqttMessage message = (MqttMessage) context.getMessage();
        String[] topicParts = message.getTopic().split("/");

        byte[] payload = message.payloadAsBytes();



        return codec.decode(context);
    }

    @Nonnull
    @Override
    public Flux<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.defer(() -> {
            Message message = context.getMessage();

            if (message instanceof DisconnectDeviceMessage) {
                return ((ToDeviceMessageContext) context)
                        .disconnect()
                        .then(Mono.empty());
            }

            if (message instanceof DeviceMessage) {
                DeviceMessage deviceMessage = ((DeviceMessage) message);

                TopicPayload convertResult = TopicMessageCodec.encode(mapper, deviceMessage);
                if (convertResult == null) {
                    return Mono.empty();
                }
                return Mono
                        .justOrEmpty(deviceMessage.getHeader("productId").map(String::valueOf))
                        .switchIfEmpty(context.getDevice(deviceMessage.getDeviceId())
                                .flatMap(device -> device.getSelfConfig(DeviceConfigKey.productId))
                        )
                        .defaultIfEmpty("null")
                        .map(productId -> SimpleMqttMessage
                                .builder()
                                .clientId(deviceMessage.getDeviceId())
                                .topic("/".concat(productId).concat(convertResult.getTopic()))
                                .payloadType(MessagePayloadType.JSON)
                                .payload(Unpooled.wrappedBuffer(convertResult.getPayload()))
                                .build());
            } else {
                return Mono.empty();
            }
        });
    }
}
