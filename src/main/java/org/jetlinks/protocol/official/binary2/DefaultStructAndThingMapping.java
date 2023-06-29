package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @date 2023/6/29 23:36
 */
public class DefaultStructAndThingMapping implements StructAndThingMapping {

    private Map<StructDeclaration, Class<? extends DeviceMessageReply>>    struct2ClassMap;

    private Map<Class<? extends DeviceMessage>, StructDeclaration>  class2StructMap;

    public DefaultStructAndThingMapping() {
        this.struct2ClassMap = new HashMap<>();
        this.class2StructMap = new HashMap<>();
    }

    @Override
    public DeviceMessageReply map(StructDeclaration structDcl) throws IllegalAccessException, InstantiationException {
        Class<? extends DeviceMessageReply> clazz = struct2ClassMap.get(structDcl);
        if (clazz == null) return null;

        return clazz.newInstance();
    }

    @Override
    public StructDeclaration map(DeviceMessage message) {
        return class2StructMap.get(message.getClass());
    }
}
