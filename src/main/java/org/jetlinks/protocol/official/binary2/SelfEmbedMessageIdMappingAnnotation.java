package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

/**
 *
 * 
 * @author  v-lizy81
 * @since   V3.1.0
 * @version 1.0.0
 * @date    2024/10/8
 */
public class SelfEmbedMessageIdMappingAnnotation implements MessageIdMappingAnnotation {

    @Override
    public void mark(@NotNull String thingMsgId, @NotNull String sessionId, @NotNull StructInstance structInst) {

    }

    @Override
    public String take(@NotNull String sessionId, @NotNull StructInstance structInst) {
        return null;
    }

}
