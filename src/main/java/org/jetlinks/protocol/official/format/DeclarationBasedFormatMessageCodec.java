package org.jetlinks.protocol.official.format;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.protocol.official.binary2.MapperContext;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.AbstractMessageCodec;
import org.jetlinks.protocol.official.common.StructAndMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于声明的文本带格式消息的编解码
 * @author v-lizy81
 * @date 2023/6/29 00:27
 */
public class DeclarationBasedFormatMessageCodec extends AbstractMessageCodec implements FormatMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFormatMessageCodec.class);

    private final FormatStructSuit              structSuit;

    public DeclarationBasedFormatMessageCodec(FormatStructSuit structSuit, StructAndMessageMapper mapper) {
        super(mapper);

        this.structSuit = structSuit;
    }

    @Override
    public DeviceMessage decode(MessageCodecContext context, String buf) {
        try {
            StructInstance structInst = structSuit.deserialize(null);
            if (structInst == null) return null;

            MapperContext mapperContext = getOrCreateContext(context);

            DeviceMessage deviceMsg = getMapper().toDeviceMessage(mapperContext, structInst);

            return deviceMsg;
        } catch (Exception e) {
            log.error("[FormatMessageCodec]解码消息失败：", e);
            return null;
        }
    }

    @Override
    public String encode(MessageCodecContext context, DeviceMessage message) {
        MapperContext mapperContext = getOrCreateContext(context);

        StructInstance structInst = getMapper().toStructInstance(mapperContext, message);
        if (structInst == null) return null;

        try {
            String rst = structSuit.serialize(structInst);
            return rst;
        } catch (Exception e) {
            log.error("[FormatMessageCodec]编码消息失败：msg={}", message.toJson(), e);
            return null;
        }
    }

    public FormatStructSuit getStructSuit() {
        return structSuit;
    }
}
