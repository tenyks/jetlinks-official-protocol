package me.tenyks.qiyun.mqtt;

import org.apache.commons.codec.DecoderException;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DisconnectDeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.Nonnull;

/**
 * 祺云定制协议之MQTT设备直连协议，支持设备和边缘网关通过MQTT协议直连接入
 * <pre>
 *      下行Topic:
 *      {厂家编码}/{产品标识}/{设备标识}/多个定制的主题
 *
 *      上行Topic:
 *      {厂家编码}/{产品标识}/{设备标识}/多个定制的主题
 * </pre>
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/24
 * @since V3.1.0
 */
public class QiYunCustomizedMqttDeviceMessageCodec implements DeviceMessageCodec {

    //TODO 应该与QiYunOverMqttDeviceMessageCodec合并

    private static final Logger     log = LoggerFactory.getLogger(QiYunCustomizedMqttDeviceMessageCodec.class);

    private final Transport         transport;

    private final DeclarationHintStructMessageCodec backendCodec;


    public QiYunCustomizedMqttDeviceMessageCodec(@Nonnull Transport transport,
                                                 @Nonnull DeclarationHintStructMessageCodec backendCodec) {
        this.transport = transport;
        this.backendCodec = backendCodec;
    }

    @Override
    public Transport        getSupportTransport() {
        return transport;
    }

    @Nonnull
    @Override
    public Mono<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        MqttMessage message = (MqttMessage) context.getMessage();

        Tuple2<DeviceMessage, Mono<MqttMessage>> decodeRst;
        try {
            decodeRst = backendCodec.decode(context, message);
            if (decodeRst != null) {

                return decodeRst.getT2()
                        .map(reply -> doReply(context, reply))
                        .switchIfEmpty(Mono.just(Mono.just(false)))
                        .flatMap((flag) -> flag.map(f -> decodeRst.getT1()));
            } else {
                log.warn("[QiYunCustMQTT]解码MQTT消息不成功：{}", message.print());
                return Mono.empty();
            }
        } catch (DecoderException e) {
            log.error("[QiYunCustMQTT]解码MQTT消息负载异常失败：{}", message.print(), e);
            return Mono.empty();
        }
    }

    @Nonnull
    @Override
    public Mono<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.defer(() -> {
            Message message = context.getMessage();

            if (message instanceof DisconnectDeviceMessage) {
                log.info("[QiYunCustMQTT]关闭MQTT会话：{}", (context.getDevice() != null ? context.getDevice() : "NO_DEVICE"));
                return ((ToDeviceMessageContext) context).disconnect().then(Mono.empty());
            }

            if (message instanceof DeviceMessage) {
                DeviceMessage deviceMessage = ((DeviceMessage) message);

                return backendCodec.encode(context, deviceMessage);
            } else {
                log.warn("[QiYunCustMQTT]不支持的类型，忽略该消息：{}", message.toJson());
                return Mono.empty();
            }
        });
    }

    private Mono<Boolean> doReply(MessageCodecContext context, MqttMessage reply) {
        if (context instanceof FromDeviceMessageContext) {
            if (log.isInfoEnabled()) {
                log.debug("[QiYunCustMQTT]下发FunctionHandleResponse消息：{}", reply.print());
            }

            return ((FromDeviceMessageContext) context)
                    .getSession()
                    .send(reply);
        } else if (context instanceof ToDeviceMessageContext) {
            if (log.isInfoEnabled()) {
                log.debug("[QiYunCustMQTT]下发FunctionHandleResponse消息：{}", reply.print());
            }

            return ((ToDeviceMessageContext) context)
                    .sendToDevice(reply);
        }

        return Mono.empty();
    }
}
