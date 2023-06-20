package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

public interface CRCCalculator {

    int apply(ByteBuf buf);

}
