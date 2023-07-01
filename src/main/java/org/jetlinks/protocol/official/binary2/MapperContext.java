package org.jetlinks.protocol.official.binary2;

public interface MapperContext {

    String  getDeviceId();

    String  getSessionId();

    void putAttribute(String key, Object value);

    Object getAttribute(String key);

    void touch();

    Short acquireAndBindThingMessageId(String messageId);

    String getThingMessageId(Short shortMsgId);
}
