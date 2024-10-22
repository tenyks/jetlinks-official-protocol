package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.protocol.official.common.StructAndMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于声明的二进制消息编解码
 * @author v-lizy81
 * @date 2023/6/29 00:27
 */
public class DeclarationBasedBinaryMessageCodec implements BinaryMessageCodec {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedBinaryMessageCodec.class);

    private final BinaryStructSuit structSuit;

    private final StructAndMessageMapper mapper;

    private final Map<String, MapperContext>   contextMap; //TODO 优化：自动释放、并发控制

    public DeclarationBasedBinaryMessageCodec(BinaryStructSuit structSuit, StructAndMessageMapper mapper) {
        this.structSuit = structSuit;
        this.mapper = mapper;
        this.contextMap = new HashMap<>();
    }

    @Override
    public DeviceMessage decode(MessageCodecContext context, ByteBuf buf) {
        try {
            StructInstance structInst = structSuit.deserialize(buf);
            if (structInst == null) return null;

            MapperContext mapperContext = getOrCreateContext(context);

            DeviceMessage deviceMsg = mapper.toDeviceMessage(mapperContext, structInst);

            return deviceMsg;
        } catch (Exception e) {
            log.error("[Decoder]解码消息失败：", e);
            return null;
        }
    }

    @Override
    public ByteBuf encode(MessageCodecContext context, DeviceMessage message) {
        MapperContext mapperContext = getOrCreateContext(context);

        StructInstance structInst = mapper.toStructInstance(mapperContext, message);
        if (structInst == null) return null;

        ByteBuf rst = structSuit.serialize(structInst);
        if (rst == null) return null;

        if (structSuit.getSigner() != null) { //按需补校验字段
            rst = structSuit.getSigner().apply(rst);
        }

        return rst;
    }

    private synchronized MapperContext getOrCreateContext(MessageCodecContext context) {
        DeviceOperator deviceOperator = context.getDevice();
        if (deviceOperator == null) return null;

        String deviceId = deviceOperator.getDeviceId();
//        String sessionId = deviceOperator.getSessionId().block(Duration.ofSeconds(30));
        String sessionId = deviceId;

        MapperContext ctx = contextMap.get(sessionId);
        if (ctx != null){
            ctx.touch();
            return ctx;
        }

        ctx = new DefaultMapperContext(deviceId, sessionId);
        ctx.touch();
        contextMap.put(sessionId, ctx);

        return ctx;
    }

    public BinaryStructSuit getStructSuit() {
        return structSuit;
    }
}
