package org.jetlinks.protocol.official.binary2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultMapperContext implements MapperContext {

    private static final short MAX_MSG_ID = 10000;

    private String  deviceId;

    private String  sessionId;

    private AtomicInteger   seqNo;

    private Map<String, Object> attrMap;

    private AtomicLong  latestAccessTime;

    public DefaultMapperContext(String deviceId, String sessionId) {
        this.deviceId = deviceId;
        this.sessionId = sessionId;
        this.attrMap = new HashMap<>();
        this.latestAccessTime = new AtomicLong();
        this.seqNo = new AtomicInteger(1);
    }

    @Override
    public void touch() {
        latestAccessTime.set(System.currentTimeMillis());
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void putAttribute(String key, Object value) {
        attrMap.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attrMap.get(key);
    }

    @Override
    public synchronized Short acquireAndBindThingMessageId(String messageId) {
        short shortMsgId = (short)(seqNo.addAndGet(1) % MAX_MSG_ID);
        if (shortMsgId <= 0) {
            seqNo.set(1);
            shortMsgId = 1;
        }

        String key = String.format("SMID_%d", shortMsgId);
        attrMap.put(key, messageId);

        return shortMsgId;
    }

    @Override
    public synchronized String getThingMessageId(Short shortMsgId) {

        String key = String.format("SMID_%d", shortMsgId);
        return (String)attrMap.get(key);
    }
}
