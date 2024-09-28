package org.jetlinks.protocol.official.common;

/**
 * 8421 BCD码
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/28
 * @since V3.1.0
 */
public class BCD8421BinaryCodec {

    private static final char[] BIN_2_CHAR_IDX = new char[]{
        '0',//0b0000
        '1',//0b0001
        '2',//0b0010
        '3',//0b0011
        '4',//0b0100
        '5',//0b0101
        '6',//0b0110
        '7',//0b0111
        '8',//0b1000
        '9',//0b1001
        ' ',//0b1010
        ' ',//0b1011
        ' ',//0b1100
        ' ',//0b1101
        ' ',//0b1110
        ' ',//0b1111
    };

    private static final byte[] CHAR_2_BIN_IDX = new byte[]{
            0b0000,0b0001,0b0010,0b0011,0b0100,
            0b0101,0b0110,0b0111,0b1000,0b1001
    };

    public static String decode(byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length == 0) return "";

        char[] buf = new char[bytes.length * 2];
        int idx = 0;
        for (byte b : bytes) {
            buf[idx++] = BIN_2_CHAR_IDX[(b >> 4 & 0x0f)];
            buf[idx++] = BIN_2_CHAR_IDX[(b & 0x0f)];
        }

        return new String(buf);
    }

    public static byte[] encode(String str) {
        if (str == null) return null;
        if (str.length() == 0) return new byte[0];

        byte[] buf = new byte[(int)Math.floor(str.length() / 2.0)];
        int idx = 0;
        for (int i = 1; i < str.length(); i++) {
            char c1 = str.charAt(i - 1), c2 = str.charAt(i);

            if(c1 < '0') c1 = '0';
            if(c1 > '9') c1 = '9';
            if(c2 < '0') c2 = '0';
            if(c2 > '9') c2 = '9';

            buf[idx++] = (byte)((0xF0 & (CHAR_2_BIN_IDX[c1 - '0'] << 4)) | (0x0F & (CHAR_2_BIN_IDX[c2 - '0'])));
        }
        if (str.length() % 2 == 1) {
            char c1 = str.charAt(str.length() - 1), c2 = '0';
            if(c1 < '0') c1 = '0';
            if(c1 > '9') c1 = '9';

            buf[idx++] = (byte)((0xF0 & (CHAR_2_BIN_IDX[c1 - '0'] << 4)) | (0x0F & (CHAR_2_BIN_IDX[c2 - '0'])));
        }

        return buf;
    }

    public static byte[] encodeWithPadding(String str, int bytesSize) {
        byte[] buf = new byte[bytesSize];
        if (str == null || str.length() == 0) {
            for (int i = 0; i < bytesSize; i++) buf[i] = '0';

            return buf;
        }

        int idx = 0;
        for (int i = 1; i < str.length() && idx < bytesSize; i++) {
            char c1 = str.charAt(i - 1), c2 = str.charAt(i);

            if(c1 < '0') c1 = '0';
            if(c1 > '9') c1 = '9';
            if(c2 < '0') c2 = '0';
            if(c2 > '9') c2 = '9';

            buf[idx++] = (byte)((0xF0 & (CHAR_2_BIN_IDX[c1 - '0'] << 4)) | (0x0F & (CHAR_2_BIN_IDX[c2 - '0'])));
        }
        if (str.length() % 2 == 1) {
            char c1 = str.charAt(str.length() - 1), c2 = '0';
            if(c1 < '0') c1 = '0';
            if(c1 > '9') c1 = '9';

            buf[idx++] = (byte)((0xF0 & (CHAR_2_BIN_IDX[c1 - '0'] << 4)) | (0x0F & (CHAR_2_BIN_IDX[c2 - '0'])));
        }
        if (idx < bytesSize) {
            for (int i = idx; i < bytesSize; i++) {
                buf[i] = '0';
            }
        }

        return buf;
    }

}
