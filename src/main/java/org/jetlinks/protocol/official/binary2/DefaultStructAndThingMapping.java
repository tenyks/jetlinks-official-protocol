package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;
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

    private Map<StructDeclaration, Class<? extends DeviceMessage>>    struct2ClassMap;

    private Map<Class<? extends DeviceMessage>, StructDeclaration>  class2StructMap;

    public DefaultStructAndThingMapping() {
        this.struct2ClassMap = new HashMap<>();
        this.class2StructMap = new HashMap<>();
    }

    public void addMapping(StructDeclaration structDcl, Class<? extends DeviceMessage> msgClazz) {
        this.struct2ClassMap.put(structDcl, msgClazz);
    }

    public void addMapping(Class<? extends DeviceMessage> msgClazz, StructDeclaration structDcl) {
        this.class2StructMap.put(msgClazz, structDcl);
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
        return class2StructMap.get(message.getClass());
    }
}
