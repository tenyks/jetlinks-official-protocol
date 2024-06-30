package org.jetlinks.protocol.qiyun.mqtt;

import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import reactor.core.publisher.Flux;

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
        return Flux.empty();
    }
}
