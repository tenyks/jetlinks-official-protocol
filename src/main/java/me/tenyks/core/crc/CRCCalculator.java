package me.tenyks.core.crc;

import io.netty.buffer.ByteBuf;

public interface CRCCalculator {

    int apply(ByteBuf buf);

}
