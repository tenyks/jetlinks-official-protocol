package org.jetlinks.protocol.official.core;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;

public class ByteUtils {

    public static String toHexStr(ByteBuf buf) {
        if (buf == null) return null;

        buf.readerIndex(0);
        byte[] tmp = new byte[buf.writerIndex()];
        buf.readBytes(tmp);
        return Hex.encodeHexString(tmp);
    }

}
