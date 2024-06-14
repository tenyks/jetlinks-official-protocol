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

    private static final byte[] sta_4 = new byte[]{
            0x00, 0x08, 0x04, 0x0C, 0x02, 0x0A, 0x06, 0x0E,
            0x01, 0x09, 0x05, 0x0D, 0x03, 0x0B, 0x07, 0x0F
    };

    public static byte reverseBits(byte c) {
        byte d = 0;
        d |= (byte)((sta_4[c & 0x0F]) << 4);
        d |= sta_4[(c >> 4) & 0x0F];
        return d;
    }

    public static int reverseBits(int c) {
        int rst;

        rst = reverseBits((byte)(c & 0x000000ff)) & 0x00000000ff;
        rst = reverseBits((byte)(c >> 8 & 0x000000ff)) & 0x00000000ff | (rst << 8);
        rst = reverseBits((byte)(c >> 16 & 0x000000ff)) & 0x00000000ff | (rst << 8);
        rst = reverseBits((byte)(c >> 24 & 0x000000ff)) & 0x00000000ff | (rst << 8);

        return rst;
    }

}
