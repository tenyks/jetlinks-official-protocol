package org.jetlinks.protocol.official.core;

import org.jetlinks.core.message.DeviceMessage;

import java.io.Serializable;

/**
 * 物模型Header、Property和Parameter及取值
 * @author tenyks.lee
 * @since 3.1
 * @version 1.0
 */
public class ThingItemAndValue implements Serializable {
    private static final long serialVersionUID = -6352177193341922652L;

    /**
     * 归属的消息类型
     */
    private Class<? extends DeviceMessage>  belong;

    private String  key;

    private Object  value;

}
