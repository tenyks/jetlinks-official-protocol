package me.tenyks.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 将UUID重新映射为Short，并且能反向查询
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/3/30
 * @since V1.3.0
 */
public class UuidRemapShort {
    /**
     * 循环使用时的Index
     */
    private short   curId;

    private final Map<String, Short>  forwardIdx;

    private final Map<Short, String>  reverseIdx;

    /**
     * 最近使用的时间
     */
    private long    lruTimestamp;

    public UuidRemapShort() {
        this.lruTimestamp = System.currentTimeMillis();
        this.curId = 1;
        this.forwardIdx = new HashMap<>();
        this.reverseIdx = new HashMap<>();
    }

    public synchronized Short shrink(String uuid) {
        Short forwardId = curId++; //TODO 优化实现尽可能减少冲突覆盖，应当优先使用已调用过recovery的值

        // 覆盖前需清除重叠的forwardIdx，否则forwardIdx的脏数据会耗尽内存
        String replaceThis = reverseIdx.get(forwardId);
        if (replaceThis != null) {
            forwardIdx.remove(replaceThis);
        }

        forwardIdx.put(uuid, forwardId);
        reverseIdx.put(forwardId, uuid);

        lruTimestamp = System.currentTimeMillis();

        return forwardId;
    }

    public synchronized String recovery(Short shrinkUuid) {
        lruTimestamp = System.currentTimeMillis();
        return reverseIdx.get(shrinkUuid);
    }

    public synchronized void removeUuid(String uuid) {
        Short forwardUuid = forwardIdx.get(uuid);
        forwardIdx.remove(uuid);

        reverseIdx.remove(forwardUuid);
    }

    synchronized void touch() {
        lruTimestamp = System.currentTimeMillis();
    }

    public long getLRUTimestamp() {
        return lruTimestamp;
    }
}
