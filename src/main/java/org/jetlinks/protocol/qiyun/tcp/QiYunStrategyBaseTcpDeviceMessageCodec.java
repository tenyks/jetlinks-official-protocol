package org.jetlinks.protocol.qiyun.tcp;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import org.jetlinks.core.device.AuthenticationRequest;
import org.jetlinks.core.message.*;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.event.ThingEventMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessageReply;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import org.jetlinks.protocol.common.DeviceRequestHandler;
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
import reactor.util.function.Tuple2;

import java.util.Optional;

/**
 * TCP通讯交互行为：<br>
 * <li>TCP链接建立后，首次心跳消息触发设备认证和设备上线，该消息之前的消息将会被丢弃</li>
 * <li></li>
 */
public class QiYunStrategyBaseTcpDeviceMessageCodec implements DeviceMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(QiYunStrategyBaseTcpDeviceMessageCodec.class);

    private final BinaryMessageCodec  codec;

    private final IntercommunicateStrategy itcmncStrategy;

    private final DeviceRequestHandler    requestHandler;

    public QiYunStrategyBaseTcpDeviceMessageCodec(BinaryMessageCodec codec,
                                                  IntercommunicateStrategy strategy,
                                                  DeviceRequestHandler requestHandler) {
        this.codec = codec;
        this.itcmncStrategy = strategy;
        this.requestHandler = requestHandler;
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
            DeviceMessage devMsg = codec.decode(context, payload);
            if (devMsg == null) {
                log.warn("[TCPCodec]忽略Decoder不支持的消息：{}", ByteUtils.toHexStr(payload));
                return Mono.empty();
            }

            if (devMsg instanceof AuthenticationRequest) {

            }

            boolean fireLogin = itcmncStrategy.canFireLogin(devMsg);
            if (!fireLogin) {
                if (log.isInfoEnabled()) {
                    log.info("[TCPCodec]按策略忽略登录前的消息：raw={}, devMsg={}",
                            ByteUtils.toHexStr(payload), devMsg.toJson());
                }
                return Mono.empty();
            }

            DeviceOnlineMessage onlineMsg = itcmncStrategy.buildLoginMessage(devMsg);
            return handleLogin(context, onlineMsg);
        }

        return Mono.defer(() -> {
            DeviceMessage devMsg = codec.decode(context, payload);
            if (devMsg == null) {
                log.warn("[TCPCodec]消息无法解码：{}", ByteUtils.toHexStr(payload));
                return Mono.empty();
            }

            if (log.isDebugEnabled()) {
                log.debug("[TCPCodec]消息解码成功：payload={}, msg={}", ByteUtils.toHexStr(payload), devMsg.toJson());
            }

            if (!(devMsg instanceof DeviceRequestMessage<?>)) return Mono.just(devMsg);

            // 请求消息
            if (requestHandler == null) return Mono.just(devMsg);

            Tuple2<DeviceRequestMessageReply, Optional<ThingEventMessage>> reqReplyTuple;
            reqReplyTuple = requestHandler.apply(context.getDevice(), (DeviceRequestMessage<?>) devMsg);

            EncodedMessage encMsg = EncodedMessage.simple(codec.encode(context, (DefaultDeviceRequestMessageReply)reqReplyTuple.getT1()));

            Mono<Boolean> sendAckMono = ((FromDeviceMessageContext) context).getSession().send(encMsg);

            if (reqReplyTuple.getT2().isPresent()) {
                return sendAckMono.thenReturn(reqReplyTuple.getT2().get());
            } else {
                return sendAckMono.then(Mono.empty());
            }
        });
    }

    private Mono<DeviceMessage> handleLogin(MessageDecodeContext context, DeviceOnlineMessage message) {
        if (log.isInfoEnabled()) {
            log.info("[TCPCodec]发现设备上线消息：msg={}", message.toJson());
        }

        String deviceId = message.getDeviceId();
        return context
                .getDevice(deviceId)
                .flatMap(device -> {
                    log.info("[TCPCodec]设备上线OK：deviceId={}", deviceId);

                    if (itcmncStrategy.needAckWhileLoginSuccess()) {
                        return doAck(message, AckCode.ok, context)
                                .thenReturn((DeviceMessage)message);
                    } else {
                        return Mono.justOrEmpty(message);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("[TCPCodec]设备上线Fail：deviceId={}", deviceId);

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
        if (payload == null) {
            log.warn("[TCPCodec]Encoder不支持的消息：msg={}", deviceMessage.toJson());
            return Mono.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("[TCPCodec]设备消息编码结果：msg={}, payload={}", deviceMessage.toJson(), ByteUtils.toHexStr(payload));
        }

        payload.readerIndex(0);
        return Mono.just(EncodedMessage.simple(payload));
    }
}
