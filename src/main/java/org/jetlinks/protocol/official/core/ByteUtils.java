package org.jetlinks.protocol.official.core;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;

public class ByteUtils {

    public static String toHexStr(ByteBuf buf) {
        //TODO 优化性能
        if (buf == null) return null;

        int originReaderIdx = buf.readerIndex();

        buf.readerIndex(0);
        byte[] tmp = new byte[buf.writerIndex()];
        buf.readBytes(tmp);

        buf.readerIndex(originReaderIdx);

        return Hex.encodeHexString(tmp);
    }

}
