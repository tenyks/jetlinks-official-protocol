package org.jetlinks.protocol.official.tcp;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import org.jetlinks.core.message.*;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.protocol.official.binary.AckCode;
import org.jetlinks.protocol.official.binary.BinaryAcknowledgeDeviceMessage;
import org.jetlinks.protocol.official.binary.BinaryMessageType;
import org.jetlinks.protocol.official.binary2.BinaryMessageCodec;
import org.jetlinks.protocol.official.common.IntercommunicateStrategy;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * TCP交换行为：<br>
 * <li>TCP链接建立后，首次心跳消息触发设备认证和设备上线，该消息之前的消息将会被丢弃</li>
 * <li></li>
 */
public class StrategyTcpDeviceMessageCodec implements DeviceMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(StrategyTcpDeviceMessageCodec.class);

    public static final String CONFIG_KEY_SECURE_KEY = "secureKey";

    public static final DefaultConfigMetadata tcpConfig = new DefaultConfigMetadata(
            "TCP认证配置", "")
            .add(CONFIG_KEY_SECURE_KEY, "secureKey", "密钥", new PasswordType());

    private BinaryMessageCodec  codec;

    private IntercommunicateStrategy itcmncStrategy;

    public StrategyTcpDeviceMessageCodec(BinaryMessageCodec codec, IntercommunicateStrategy strategy) {
        this.codec = codec;
        this.itcmncStrategy = strategy;
    }

    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.TCP;
    }

    @NonNull @Override
    public Publisher<? extends Message> decode(@NonNull MessageDecodeContext context) {
        ByteBuf payload = context.getMessage().getPayload();

        // 设备未登录
        if (context.getDevice() == null) {
            DeviceMessage msg = codec.decode(context, payload);

            boolean fireLogin = itcmncStrategy.canFireLogin(msg);
            if (!fireLogin) {
                if (log.isInfoEnabled()) {
                    log.info("[TCPCodec]忽略登录前的消息：raw={}, msg={}", ByteUtils.toHexStr(payload), msg.toJson());
                }
                return Mono.empty();
            } else {
                DeviceOnlineMessage onlineMsg = itcmncStrategy.buildLoginMessage(msg);
                if (onlineMsg == null) {

                }

                return handleLogin(context, onlineMsg);
            }
        }

        DeviceMessage msg = codec.decode(context, payload);

        return Mono.justOrEmpty(msg);
    }

    private Mono<DeviceMessage> handleLogin(MessageDecodeContext context, DeviceOnlineMessage message) {
        String deviceId = message.getDeviceId();
        return context
                .getDevice(deviceId)
                .flatMap(device -> {
                    if (itcmncStrategy.needAckWhileLoginSuccess()) {
                        return doAck(message, AckCode.ok, context).thenReturn((DeviceMessage)message);
                    } else {
                        return Mono.justOrEmpty(message);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    if (itcmncStrategy.needAckWhileLoginFail()) {
                        return doAck(message, AckCode.noAuth, context);
                    } else {
                        return Mono.empty();
                    }
                }));
    }

    private <T> Mono<T> doAck(DeviceMessage source, AckCode code, MessageDecodeContext context) {
        AcknowledgeDeviceMessage ackMsg = buildAckMessage(source, code);
        final EncodedMessage encMsg = EncodedMessage.simple(codec.encode(context, ackMsg));

        return ((FromDeviceMessageContext) context)
                .getSession()
                .send(encMsg)
                .then(Mono.fromRunnable(() -> {
                    if (itcmncStrategy.needCloseConnectionWhileSendAckFail()) {
                        if (source instanceof DeviceOnlineMessage && code != AckCode.ok) {
                            ((FromDeviceMessageContext) context).getSession().close();
                        }
                    }
                }));
    }

    private AcknowledgeDeviceMessage buildAckMessage(DeviceMessage source, AckCode code) {
        AcknowledgeDeviceMessage message = new AcknowledgeDeviceMessage();
        message.addHeader(BinaryAcknowledgeDeviceMessage.codeHeader, code.name());
        message.setDeviceId(source.getDeviceId());
        message.setMessageId(source.getMessageId());
        message.setCode(code.name());
        message.setSuccess(code == AckCode.ok);

        source.getHeader(BinaryMessageType.HEADER_MSG_SEQ)
                .ifPresent(seq -> message.addHeader(BinaryMessageType.HEADER_MSG_SEQ, seq));

        return message;
    }

    @NonNull
    @Override
    public Publisher<? extends EncodedMessage> encode(@NonNull MessageEncodeContext context) {
        DeviceMessage deviceMessage = ((DeviceMessage) context.getMessage());
        if (deviceMessage instanceof DisconnectDeviceMessage) {
            return Mono.empty();
        }

        ByteBuf payload = codec.encode(context, deviceMessage);

        return Mono.just(EncodedMessage.simple(payload));
    }
}
