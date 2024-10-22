package org.jetlinks.protocol.official.common;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.codec.MessageCodecContext;
import org.jetlinks.protocol.official.binary2.DefaultMapperContext;
import org.jetlinks.protocol.official.binary2.MapperContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/22
 * @since V3.1.0
 */
public abstract class AbstractMessageCodec {

    private final StructAndMessageMapper        mapper;

    private final Map<String, MapperContext>    contextMap; //TODO 优化：自动释放、并发控制

    public AbstractMessageCodec(StructAndMessageMapper mapper) {
        this.mapper = mapper;
        this.contextMap = new HashMap<>();
    }

    protected  StructAndMessageMapper   getMapper() {
        return mapper;
    }

    protected synchronized MapperContext  getOrCreateContext(MessageCodecContext context) {
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
}
