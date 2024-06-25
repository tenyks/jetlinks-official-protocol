package org.jetlinks.protocol.qiyun.mqtt;

import org.jetlinks.core.Value;
import org.jetlinks.core.defaults.Authenticator;
import org.jetlinks.core.device.*;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.DeviceConfigScope;
import org.jetlinks.core.metadata.types.EnumType;
import org.jetlinks.core.metadata.types.PasswordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * 祺云IOT标准协议之MQTT设备认证：静态码，支持一型一密，一机一密，牵涉到MQTT协议的clientId、username和password
 * @author v-lizy81
 * @date 2024/6/24 21:45
 */
public class QiYunMqttStaticCodeAuthenticator implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(QiYunMqttStaticCodeAuthenticator.class);

    public static final AuthenticationResponse DEVICE_NOT_FOUND = AuthenticationResponse.error(403, "设备不存在");

    public static final DefaultConfigMetadata CONFIG = new DefaultConfigMetadata(
            "MQTT静态码认证配置",
            "支持使用MQTT协议的clientId、username和password进行设备认证，规则示意如：username==productId && clientId==deviceId && password==secret")
            .add("secret", "鉴权码", "32个字符的随机数字和字符串", new PasswordType());

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request, @Nonnull DeviceOperator device) {
        if (!(request instanceof MqttAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }

        MqttAuthenticationRequest req = ((MqttAuthenticationRequest) request);
        String clientId = req.getClientId();
        String username = req.getUsername();
        String password = req.getPassword();

        String productId = device.getProduct().block().getId();
        if (!device.getDeviceId().equals(clientId)) {
            log.info("[MQTT][U+P]设备认证不通过，不匹配的产品：username({}) != productId({})", username, productId);
            return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
        }

        if (!device.getDeviceId().equals(clientId)) {
            log.info("[MQTT][U+P]设备认证不通过，不匹配的设备：clientId({}) != deviceId({})", clientId, device.getDeviceId());
            return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
        }

        Value secret = device.getConfig("secret").block();

        if (secret == null || !secret.as(String.class).equals(password)) {
            log.info("[MQTT][U+P]设备认证不通过，密码与密钥不匹配：password({}) != secret({})", password, secret);
            return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
        }

        log.debug("[MQTT][U+P]设备认证通过：clientId=deviceId={}", clientId);
        return Mono.just(AuthenticationResponse.success(device.getDeviceId()));
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request, @Nonnull DeviceRegistry registry) {
        if (!(request instanceof MqttAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }

        MqttAuthenticationRequest req = ((MqttAuthenticationRequest) request);
        final String clientId = req.getClientId();
        final String username = req.getUsername();
        final String password = req.getPassword();

        Mono<DeviceOperator> device = registry.getDevice(req.getClientId());

        return device
                .flatMap((deviceOperator -> {
                    String productId = deviceOperator.getProduct().block().getId();
                    if (!deviceOperator.getDeviceId().equals(clientId)) {
                        log.info("[MQTT][U+P]设备认证不通过，不匹配的产品：username({}) != productId({})", username, productId);
                        return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
                    }

                    Value secret = deviceOperator.getConfig("secret").block();

                    if (secret == null || !secret.as(String.class).equals(password)) {
                        log.info("[MQTT][U+P]设备认证不通过，密码与密钥不匹配：password({}) != secret({})", password, secret);
                        return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
                    }

                    log.debug("[MQTT][U+P]设备认证通过：clientId=deviceId={}", clientId);
                    return Mono.just(AuthenticationResponse.success(deviceOperator.getDeviceId()));

                }))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("[MQTT][U+P]设备认证不通过，未注册的设备：clientId = {}", clientId);
                    return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
                }));
    }

}
