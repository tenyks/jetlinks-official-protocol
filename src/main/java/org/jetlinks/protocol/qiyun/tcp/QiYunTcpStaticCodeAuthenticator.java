package org.jetlinks.protocol.qiyun.tcp;

import org.jetlinks.core.Value;
import org.jetlinks.core.defaults.Authenticator;
import org.jetlinks.core.device.*;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.PasswordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * 祺云IOT标准协议之TCP设备认证：无鉴权码或静态码，支持一型一密，一机一密
 * @author v-lizy81
 * @date 2024/09/28 14:26
 */
public class QiYunTcpStaticCodeAuthenticator implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(QiYunTcpStaticCodeAuthenticator.class);

    public static final AuthenticationResponse AUTH_METHOD_NOT_SUPPORT = AuthenticationResponse.error(400, "不支持的认证方式");
    public static final AuthenticationResponse AUTH_FAIL = AuthenticationResponse.error(403, "设备认证不通过");

    public static final DefaultConfigMetadata CONFIG = new DefaultConfigMetadata(
            "TCP静态码认证配置",
            "TCP认证时需要的配置：使用TCP协议报文中的设备码或加上鉴权码进行设备认证\n")
            .add("secret", "鉴权码", "32个字符的随机数字和字符串", new PasswordType());


    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request, @Nonnull DeviceOperator device) {
        if (!(request instanceof TcpAuthenticationRequest)) {
            return Mono.just(AUTH_METHOD_NOT_SUPPORT);
        }

        final TcpAuthenticationRequest req = ((TcpAuthenticationRequest) request);
        final String clientId = req.getClientId();

        if (!clientId.equals(device.getDeviceId())) {
            log.info("[MQTT][U+P]设备认证不通过，不匹配的设备：clientId({}) != deviceId({})", clientId, device.getDeviceId());
            return Mono.just(AUTH_FAIL);
        }

        return device.getProduct()
                .zipWith(device.getConfig("secret").switchIfEmpty(Mono.just(Value.simple(""))))
                .map(tuple -> {
                    log.debug("[MQTT][U+P]设备认证通过：clientId=deviceId={}", clientId);
                    return AuthenticationResponse.success(device.getDeviceId());
                });
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request,
                                                     @Nonnull DeviceRegistry registry) {
        TcpAuthenticationRequest req = ((TcpAuthenticationRequest) request);

        return registry.getDevice(req.getClientId())
                .flatMap(device -> authenticate(request, device))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("[MQTT][U+P]设备认证不通过，未登记的设备：clientId={}", req.getClientId());
                    return Mono.just(AUTH_FAIL);
                }));
    }

}
