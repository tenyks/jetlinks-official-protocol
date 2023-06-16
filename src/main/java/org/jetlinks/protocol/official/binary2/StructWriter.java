package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * @author v-lizy81
 * @date 2023/6/16 22:59
 */
public interface StructWriter {

    ByteBuf write(StructInstance instance);

}
