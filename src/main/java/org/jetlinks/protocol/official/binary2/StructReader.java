package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:14
 */
public interface StructReader {

    StructInstance read(ByteBuf buf);

}
