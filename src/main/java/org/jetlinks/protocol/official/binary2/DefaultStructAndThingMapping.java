package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/6/29 23:36
 */
public class DefaultStructAndThingMapping implements StructAndThingMapping {

    private static final Logger log = LoggerFactory.getLogger(DefaultStructAndThingMapping.class);

    private final Map<StructDeclaration, Class<? extends DeviceMessage>>    struct2ClassMap;

    private final Map<String, StructDeclaration>  class2StructMap;

    public DefaultStructAndThingMapping() {
        this.struct2ClassMap = new HashMap<>();
        this.class2StructMap = new HashMap<>();
    }

    public void addMapping(StructDeclaration structDcl, Class<? extends DeviceMessage> msgClazz) {
        this.struct2ClassMap.put(structDcl, msgClazz);
    }

    public void addMapping(Class<? extends DeviceMessage> msgClazz, String subKey, StructDeclaration structDcl) {
        if (structDcl == null) return ;

        String key = buildMappingKey(msgClazz, subKey);
        this.class2StructMap.put(key, structDcl);
    }

    private String buildMappingKey(Class<? extends DeviceMessage> msgClazz, String subKey) {
        if (subKey != null) {
            return String.format("%s:%s", msgClazz.getName(), subKey);
        } else {
            return msgClazz.getName();
        }
    }

    @Override
    public DeviceMessage map(StructDeclaration structDcl) {
        Class<? extends DeviceMessage> clazz = struct2ClassMap.get(structDcl);
        if (clazz == null) return null;

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(String.format("构建%s的对象失败", clazz), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("构建%s的对象失败：默认构造函数不符合约定", clazz), e);
        }
    }

    @Override
    public StructDeclaration map(DeviceMessage message) {
        String key = buildMappingKey(message);

        return class2StructMap.get(key);
    }

    private String buildMappingKey(DeviceMessage message) {
        String subKey = null;
        if (message instanceof FunctionInvokeMessage) {
            subKey = ((FunctionInvokeMessage) message).getFunctionId();
        } else if (message instanceof EventMessage) {
            subKey = ((EventMessage) message).getEvent();
        } else if (message instanceof FunctionInvokeMessageReply) {
            subKey = ((FunctionInvokeMessageReply) message).getFunctionId();
        } else if (message instanceof DeviceRequestMessage<?>) {
            subKey = ((DeviceRequestMessage<?>) message).getFunctionId();
        } else if (message instanceof DeviceRequestMessageReply) {
            subKey = ((DeviceRequestMessageReply) message).getFunctionId();
        }

        return buildMappingKey(message.getClass(), subKey);
    }
}
