package me.tenyks.core.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author v-lizy81
 * @date 2023/12/1 23:15
 */
public class ShortCodeGenerator {

    public static final ShortCodeGenerator INSTANCE = new ShortCodeGenerator();

    private static final AtomicLong SeqNo = new AtomicLong(1);

    public String   next() {
        return String.format("%x_%06x", System.currentTimeMillis(), SeqNo.getAndIncrement());
    }

}
