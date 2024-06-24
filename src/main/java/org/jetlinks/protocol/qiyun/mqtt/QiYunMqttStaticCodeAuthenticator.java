package org.jetlinks.protocol.qiyun.mqtt;

import org.jetlinks.core.defaults.Authenticator;
import org.jetlinks.core.device.AuthenticationRequest;
import org.jetlinks.core.device.AuthenticationResponse;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.DeviceRegistry;
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
            "补充说明")
            ;

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request, @Nonnull DeviceOperator device) {
        return null;
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request, @Nonnull DeviceRegistry registry) {
        return null;
    }

}
