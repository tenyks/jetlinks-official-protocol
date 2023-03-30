package org.jetlinks.protocol.official.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.coap.OptionNumberRegistry;
import org.jetlinks.core.Value;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.CoapMessage;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.Transport;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.DeviceConfigScope;
import org.jetlinks.core.metadata.types.EnumType;
import org.jetlinks.core.metadata.types.PasswordType;
import org.jetlinks.protocol.official.FunctionalTopicHandlers;
import org.jetlinks.protocol.official.ObjectMappers;
import org.jetlinks.protocol.official.TopicMessageCodec;
import org.jetlinks.protocol.official.cipher.Ciphers;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
public class JetLinksCoapDeviceMessageCodec extends AbstractCoapDeviceMessageCodec {
    public static final DefaultConfigMetadata coapConfig = new DefaultConfigMetadata(
            "CoAP认证配置",
            "使用CoAP进行数据上报时,需要对数据进行加密:encrypt(payload,secureKey);")
            .add("encAlg", "加密算法", "加密算法", new EnumType()
                    .addElement(EnumType.Element.of("AES", "AES加密(ECB,PKCS#5)", "加密模式:ECB,填充方式:PKCS#5")), DeviceConfigScope.product)
            .add("secureKey", "密钥", "16位密钥KEY", new PasswordType());

    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.CoAP;
    }

    protected Flux<DeviceMessage> decode(CoapMessage message, MessageDecodeContext context, Consumer<Object> response) {
        String path = getPath(message);
        String deviceId = getDeviceId(message);
        boolean cbor = message
                .getStringOption(OptionNumberRegistry.CONTENT_FORMAT)
                .map(MediaType::valueOf)
                .map(MediaType.APPLICATION_CBOR::includes)
                .orElse(false);
        ObjectMapper objectMapper = cbor ? ObjectMappers.CBOR_MAPPER : ObjectMappers.JSON_MAPPER;
        return context
                .getDevice(deviceId)
                .flatMapMany(device -> device
                        .getConfigs("encAlg", "secureKey")
                        .flatMapMany(configs -> {
                            Ciphers ciphers = configs
                                    .getValue("encAlg")
                                    .map(Value::asString)
                                    .flatMap(Ciphers::of)
                                    .orElse(Ciphers.AES);
                            String secureKey = configs.getValue("secureKey").map(Value::asString).orElse(null);
                            byte[] payload = ciphers.decrypt(message.payloadAsBytes(), secureKey);
                            //解码
                            return TopicMessageCodec
                                    .decode(objectMapper, TopicMessageCodec.removeProductPath(path), payload)
                                    //如果不能直接解码，可能是其他设备功能
                                    .switchIfEmpty(FunctionalTopicHandlers
                                                           .handle(device,
                                                                   path.split("/"),
                                                                   payload,
                                                                   objectMapper,
                                                                   reply -> Mono.fromRunnable(() -> response.accept(reply.getPayload()))));
                        }));
    }


}
