package org.jetlinks.protocol.common;

import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/11/30
 * @since V1.3.0
 */
public interface DedicatedMessageEncoder {

    Publisher<? extends EncodedMessage> encode(@Nonnull MessageEncodeContext context);

}
