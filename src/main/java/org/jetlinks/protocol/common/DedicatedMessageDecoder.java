package org.jetlinks.protocol.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.core.message.DeviceMessage;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/11/30
 * @since V1.3.0
 */
public interface DedicatedMessageDecoder {

    @Nonnull
    Publisher<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload);

}
