package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.MessageIdMapping;
import org.jetlinks.protocol.common.SimpleMessageIdMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.function.Function;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/4
 * @since V3.1.0
 */
public abstract class AbstractMessageIdMappingAnnotation implements MessageIdMappingAnnotation {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageIdMappingAnnotation.class);

    private final MessageIdMapping    mapping;

    protected AbstractMessageIdMappingAnnotation() {
        this.mapping = new SimpleMessageIdMapping(120, 1000);
    }

    @Override
    public void mark(@NotNull String thingMsgId, @NotNull String sessionId, @NotNull StructInstance structInst) {
        if (thingMsgId == null || sessionId == null || structInst == null) {
            throw new IllegalArgumentException("参数不全。[0x17AMIMA2365]");
        }

        String protocolMsgId = String.format("%s_%s", sessionId, buildProtocolMsgId(structInst));

        boolean flag = mapping.submitBinding(thingMsgId, protocolMsgId);
        log.info("[StructCodecMapper]MessageId映射：{} -> {} => {}", thingMsgId, protocolMsgId, flag);
    }

    @Override
    public String take(@NotNull String sessionId, @NotNull StructInstance structInst) {
        if (sessionId == null || structInst == null) {
            throw new IllegalArgumentException("参数不全。[0x17AMIMA3665]");
        }

        String protocolMsgId = String.format("%s_%s", sessionId, buildProtocolMsgId(structInst));
        String thingMsgId = mapping.revokeBinding(protocolMsgId);
        log.info("[StructCodecMapper]MessageId映射：{} <- {}", thingMsgId, protocolMsgId);
        return thingMsgId;
    }

    protected abstract String buildProtocolMsgId(StructInstance structInst);

    public static class OfFunction extends AbstractMessageIdMappingAnnotation {

        private final Function<StructInstance, String> function;

        public OfFunction(Function<StructInstance, String> function) {
            this.function = function;
        }

        @Override
        protected String buildProtocolMsgId(StructInstance structInst) {
            return function.apply(structInst);
        }
    }
}
