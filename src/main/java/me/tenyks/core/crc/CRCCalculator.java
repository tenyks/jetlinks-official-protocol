package me.tenyks.core.crc;

import io.netty.buffer.ByteBuf;

public interface CRCCalculator {

    int apply(ByteBuf buf);

    /**
     * @return 字节数
     */
    default int size() {
        return 1;
    }

}
