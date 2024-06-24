package org.jetlinks.protocol.official.mqtt;

import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.core.metadata.types.StringType;
import org.jetlinks.protocol.official.ObjectMappers;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;

/**
 * 祺云透明传输MQTT，用于MQTT边缘网关或DTU对接
 * <pre>
 *      下行Topic:
 *
 *
 *      上行Topic:
 *
 * </pre>
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/24
 * @since V3.1.0
 */
public class QiYunTransparentMqttDeviceMessageCodec implements DeviceMessageCodec {

    public static final DefaultConfigMetadata mqttConfig = new DefaultConfigMetadata(
            "祺云MQTT认证配置"
            , "MQTT认证时需要的配置,mqtt用户名,密码算法:\n" +
            "clientId=deviceSn\n" +
            "username=productId\n" +
            "password=SHA256(secureId|timestamp|secureKey)\n" +
            "\n" +
            "timestamp为时间戳, 与服务时间不能相差5分钟")
            .add("鉴权码", "secureKey", "密钥KEY", new PasswordType());

    private final Transport transport;

    public QiYunTransparentMqttDeviceMessageCodec(Transport transport) {
        this.transport = transport;
    }

    @Override
    public Transport getSupportTransport() {
        return null;
    }

    @Nonnull
    @Override
    public Publisher<? extends Message> decode(@Nonnull MessageDecodeContext context) {
        return null;
    }

    @Nonnull
    @Override
    public Publisher<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return null;
    }
}
