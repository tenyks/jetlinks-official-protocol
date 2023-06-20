package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

public interface FieldWriter {

    short write(FieldInstance instance, ByteBuf outputBuf);

}
