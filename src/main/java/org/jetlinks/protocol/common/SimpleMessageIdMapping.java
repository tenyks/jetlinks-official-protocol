package org.jetlinks.protocol.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/4
 * @since V3.1.0
 */
public class SimpleMessageIdMapping implements MessageIdMapping {

    private final Cache<String, String>   cache;

    public SimpleMessageIdMapping(int maxTtlInSecs, int maxSize) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(maxTtlInSecs))
                .maximumSize(maxSize)
                .initialCapacity(100)
                .concurrencyLevel(1)
                .build();
    }

    @Override
    public synchronized boolean submitBinding(String thingMsgId, String protocolMsgId) {
        String tmp = cache.getIfPresent(protocolMsgId);
        if (tmp == null) {
            cache.put(protocolMsgId, thingMsgId);
            return true;
        } else {
            return (tmp.equals(thingMsgId));
        }
    }

    @Override
    public synchronized String revokeBinding(String protocolMsgId) {
        String tmp = cache.getIfPresent(protocolMsgId);
        cache.invalidate(protocolMsgId);

        return tmp;
    }

}
