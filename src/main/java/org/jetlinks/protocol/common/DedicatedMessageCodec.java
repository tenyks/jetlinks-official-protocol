package org.jetlinks.protocol.common;

import org.jetlinks.core.message.codec.MessageCodecDescription;
import reactor.core.publisher.Mono;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/11/30
 * @since V1.3.0
 */
public interface DedicatedMessageCodec extends DedicatedMessageDecoder, DedicatedMessageEncoder {

    /**
     * 获取协议描述
     * @return 协议描述
     */
    default Mono<? extends MessageCodecDescription> getDescription() {
        return Mono.empty();
    }

    static DedicatedMessageCodec findCodec(String[] topic) {
        return null;
    }
}
