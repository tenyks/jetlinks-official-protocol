package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/6/29 00:27
 */
public class DeclarationBasedBinaryMessageCodec implements BinaryMessageCodec {

    private StructSuit  structSuit;

    private StructAndMessageMapper  mapper;

    private Map<String, MapperContext>   contextMap; //TODO 优化：自动释放、并发控制

    public DeclarationBasedBinaryMessageCodec(StructSuit structSuit, StructAndMessageMapper mapper) {
        this.structSuit = structSuit;
        this.mapper = mapper;
        this.contextMap = new HashMap<>();
    }

    @Override
    public DeviceMessage decode(MessageCodecContext context, ByteBuf buf) {
        StructInstance structInst = structSuit.deserialize(buf);

        MapperContext mapperContext = getOrCreateContext(context);

        DeviceMessage deviceMsg = mapper.toDeviceMessage(mapperContext, structInst);
        return deviceMsg;
    }

    @Override
    public ByteBuf encode(MessageCodecContext context, DeviceMessage message) {
        MapperContext mapperContext = getOrCreateContext(context);

        StructInstance structInst = mapper.toStructInstance(mapperContext, message);

        ByteBuf rst = structSuit.serialize(structInst);
        if (rst != null && structSuit.getSigner() != null) {
            rst = structSuit.getSigner().apply(rst);
        }

        return rst;
    }

    private synchronized MapperContext getOrCreateContext(MessageCodecContext context) {
        DeviceOperator deviceOperator = context.getDevice();
        if (deviceOperator == null) return null;

        String deviceId = deviceOperator.getDeviceId();
        String sessionId = deviceOperator.getSessionId().block(Duration.ofSeconds(30));

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
}
