package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * 基于字节无格式的结构Writer
 * @author v-lizy81
 * @date 2023/6/16 22:59
 */
public interface BinaryStructWriter {

    ByteBuf write(StructInstance instance);

}
