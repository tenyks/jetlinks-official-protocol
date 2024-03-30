package me.tenyks.core.utils;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/3/30
 * @since V1.3.0
 */
public class UuidRemapFactory {

    public static final UuidRemapFactory DEF_INST = new UuidRemapFactory(3600000);

    /**
     * 超过这个空闲时长的对象将被清除，单位：毫秒
     */
    private final long  cleanOverIdleDuration;

    private final Map<String, UuidRemapShort>   instances;

    private long nextCleanTime;


    /**
     *
     * @param cleanOverIdleDuration     超过这个空闲时长的对象将被清除，单位：毫秒
     */
    public UuidRemapFactory(long cleanOverIdleDuration) {
        this.cleanOverIdleDuration = cleanOverIdleDuration;
        this.nextCleanTime = System.currentTimeMillis() + cleanOverIdleDuration;
        this.instances = new HashMap<>();
    }

    @NotNull
    public synchronized UuidRemapShort createOrGet(String namespace) {
        if (System.currentTimeMillis() >= nextCleanTime) {
            cleanLRU();
        }

        UuidRemapShort inst = instances.get(namespace);
        if (inst != null) {
            inst.touch();
            return inst;
        }

        inst = new UuidRemapShort();
        instances.put(namespace, inst);

        return inst;
    }

    /**
     * 清除超过设置时长没有使用的对象
     */
    private void cleanLRU() {
        nextCleanTime = System.currentTimeMillis() + cleanOverIdleDuration;
        long thr = System.currentTimeMillis() - cleanOverIdleDuration;

        Set<String> cleanSet = new HashSet<>();
        for (String key : instances.keySet()) {
            UuidRemapShort inst = instances.get(key);

            if (inst.getLRUTimestamp() <= thr) {
                cleanSet.add(key);
            }
        }

        if (cleanSet.size() > 0) {
            for (String key : cleanSet) {
                instances.remove(key);
            }
        }
    }
}
