package org.jetlinks.protocol.dataSky;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.protocol.common.DedicatedMessageDecoder;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;

/**
 * @author v-lizy81
 * @date 2023/11/30 23:12
 */
public class DataSkyDedicatedMessageDecoder implements DedicatedMessageDecoder {

    @Nonnull
    @Override
    public Publisher<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload) {
        return null;
    }

}
