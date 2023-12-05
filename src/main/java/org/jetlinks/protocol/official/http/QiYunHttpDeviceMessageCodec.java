package org.jetlinks.protocol.official.http;

import com.fasterxml.jackson.core.JsonParseException;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.codec.http.HttpExchangeMessage;
import org.jetlinks.core.message.codec.http.SimpleHttpResponseMessage;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.core.trace.DeviceTracer;
import org.jetlinks.core.trace.FluxTracer;
import org.jetlinks.protocol.common.DedicatedMessageCodec;
import org.jetlinks.protocol.official.ObjectMappers;
import org.jetlinks.protocol.official.core.TopicMessageCodec;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.Base64;

/**
 * Http消息的编解码器
 * <li>认证</li>
 *
 * @author tenyks@163.com
 * @since 3.0.0
 */
@Slf4j
public class QiYunHttpDeviceMessageCodec implements DeviceMessageCodec {
    public static final DefaultConfigMetadata httpConfig = new DefaultConfigMetadata(
            "HTTP认证配置", "使用HTTP Bearer Token进行认证"
            )
            .add("bearer_token", "Token", "Token", new PasswordType());

    private final Transport transport;

    private final DedicatedMessageCodec payloadCodec;

    public QiYunHttpDeviceMessageCodec(DedicatedMessageCodec payloadCodec) {
        this.transport = DefaultTransport.HTTP;
        this.payloadCodec = payloadCodec;
    }

    @Override
    public Transport getSupportTransport() {
        return transport;
    }

    @Nonnull
    public Mono<EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.empty();
    }

    private static SimpleHttpResponseMessage unauthorized(String msg) {
        return SimpleHttpResponseMessage
                .builder()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"success\":false,\"code\":\"unauthorized\",\"message\":\"" + msg + "\"}")
                .status(401)
                .build();
    }

    private static SimpleHttpResponseMessage badRequest() {
        return SimpleHttpResponseMessage
                .builder()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"success\":false,\"code\":\"bad_request\"}")
                .status(400)
                .build();
    }

    @Nonnull
    @Override
    public Flux<DeviceMessage> decode(@Nonnull MessageDecodeContext context) {
        if (context.getMessage() instanceof HttpExchangeMessage) {
            return decodeHttp(context);
        }

        return Flux.empty();
    }

    private Flux<DeviceMessage> decodeHttp(MessageDecodeContext context) {
        HttpExchangeMessage message = (HttpExchangeMessage) context.getMessage();

        if (log.isInfoEnabled()) {
            log.info("[QiYunHttpDeviceMessageCodec]URI={}, Headers={}, BODY={}",
                    message.getPath(), message.getHeaders(), Base64.getEncoder().encodeToString(message.getBytes(0, 100)));
        }

        //提取Topic
        String[] paths = TopicMessageCodec.removeProductPath(message.getPath());
        if (paths.length < 1) {
            return message
                    .response(badRequest())
                    .thenMany(Mono.empty());
        }

        String deviceId = paths[1];
        return context
                .getDevice(deviceId)
                .switchIfEmpty(Mono.defer(() -> message
                        .response(unauthorized("Device no register"))
                        .then(Mono.empty())))
                //解码
                .flatMapMany(ignore -> doDecodePayload(message, paths))
                .switchOnFirst((s, flux) -> {
                    Mono<Void> handler;
                    //有结果则认为成功
                    if (s.hasValue()) {
                        handler = message.ok("{\"success\":true}");
                    } else {
                        return message
                                .response(badRequest())
                                .then(Mono.empty());
                    }
                    return handler.thenMany(flux);
                })
                .onErrorResume(err -> message
                        .error(500, getErrorMessage(err))
                        .then(Mono.error(err)))
                //跟踪信息
                .as(FluxTracer
                            .create(DeviceTracer.SpanName.decode(deviceId),
                                    builder -> builder.setAttribute(DeviceTracer.SpanKey.message, message.print())));
    }

    protected Flux<DeviceMessage> doDecodePayload(HttpExchangeMessage message, String[] paths) {
        return message
                .payload()
                .flatMapMany(buf -> {
                    byte[] body = ByteBufUtil.getBytes(buf);
                    return payloadCodec.decode(ObjectMappers.JSON_MAPPER, paths, body);
                });
    }

    public String getErrorMessage(Throwable err) {
        if (err instanceof JsonParseException) {
            return "{\"success\":false,\"code\":\"request_body_format_error\"}";
        }
        return "{\"success\":false,\"code\":\"server_error\"}";
    }

}
