package me.tenyks.dataSky;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.protocol.common.DedicatedMessageCodec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

public class DataSkyDedicatedMessageCodec implements DedicatedMessageCodec {

    private final DataSkyDedicatedMessageDecoder  decoder;

    public DataSkyDedicatedMessageCodec() {
        this.decoder = new DataSkyDedicatedMessageDecoder();
    }

    @Nonnull
    @Override
    public Publisher<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload) {
        return decoder.decode(mapper, topics, payload);
    }

    @Override
    public Publisher<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context) {
        return Mono.empty();
    }
}
