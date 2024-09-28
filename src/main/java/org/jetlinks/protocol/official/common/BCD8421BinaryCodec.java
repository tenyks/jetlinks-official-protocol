package org.jetlinks.protocol.official.common;

/**
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
        return null;
    }

    public static byte[] encode(String str) {
        return null;
    }

}
