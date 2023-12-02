package me.tenyks.core.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author v-lizy81
 * @date 2023/12/1 23:15
 */
public class ShortCodeGenerator {

    private static final AtomicLong SeqNo = new AtomicLong(1);

    public String   next() {
        return String.format("%d_%06d", System.currentTimeMillis(), SeqNo.getAndIncrement());
    }

}
