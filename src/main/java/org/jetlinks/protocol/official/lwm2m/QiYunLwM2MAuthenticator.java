package org.jetlinks.protocol.official.lwm2m;

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
 * 祺云IOT标准协议之LwM2M设备认证
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/1
 * @since V3.1.0
 */
public class QiYunLwM2MAuthenticator implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(QiYunLwM2MAuthenticator.class);

    public static final AuthenticationResponse deviceNotFound = AuthenticationResponse.error(403, "设备不存在");

    public static final DefaultConfigMetadata CONFIG = new DefaultConfigMetadata(
            "LwM2M认证配置",
            "使用LwM2M进行数据上报时,需要对数据进行加密:encrypt(payload,secureKey);")
            .add("encAlg", "加密算法", "加密算法",
                    new EnumType().addElement(EnumType.Element.of("AES", "AES加密(ECB,PKCS#5)", "加密模式:ECB,填充方式:PKCS#5")),
                    DeviceConfigScope.product)
            .add("secureKey", "密钥", "16位密钥KEY", new PasswordType());

    @Override
    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request,
                                                     @Nonnull DeviceOperator device) {
        if (!(request instanceof LwM2MAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }

        LwM2MAuthenticationRequest req = ((LwM2MAuthenticationRequest) request);
        String ep = req.getEndpoint();
        if (device.getDeviceId().equals(ep)) {
            log.debug("[LwM2M]设备认证通过：ep={}", ep);
            return Mono.just(AuthenticationResponse.success(device.getDeviceId()));
        } else {
            log.info("[LwM2M]设备认证不通过：ep={}", ep);
            return Mono.just(AuthenticationResponse.error(403, "设备认证不通过"));
        }
    }

    public Mono<AuthenticationResponse> authenticate(@Nonnull AuthenticationRequest request,
                                                     @Nonnull DeviceRegistry registry) {
        if (!(request instanceof LwM2MAuthenticationRequest)) {
            return Mono.just(AuthenticationResponse.error(400, "不支持的认证方式"));
        }

        LwM2MAuthenticationRequest req = ((LwM2MAuthenticationRequest) request);
        String ep = req.getEndpoint();
        return registry.getDevice(ep)
                .flatMap(device -> authenticate(request, device))
                .defaultIfEmpty(deviceNotFound);
    }
}
