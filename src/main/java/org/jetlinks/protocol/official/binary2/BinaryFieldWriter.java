package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

public interface BinaryFieldWriter {

    short write(FieldInstance instance, ByteBuf outputBuf);

}
