package org.jetlinks.protocol.official.common;

import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class BCD8421BinaryCodecTest {

    @Test
    public void decode() {
        String rst;

        rst = BCD8421BinaryCodec.decode(new byte[]{(byte) 0b10000101, (byte)0b01010000, (byte)0b01011000});
        System.out.println(rst);
        Assert.assertEquals("855058", rst);
    }

    @Test
    public void encode() {
        byte[] rst;

        byte[] expected = new byte[]{(byte) 0b10000101, (byte) 0b01010000, (byte) 0b01011000};
        rst = BCD8421BinaryCodec.encode("855058");
        System.out.println(ByteUtils.toBitStr(rst));
        Assert.assertEquals(expected[0], rst[0]);
        Assert.assertEquals(expected[1], rst[1]);
        Assert.assertEquals(expected[2], rst[2]);

        expected = new byte[]{(byte) 0b10000101, (byte) 0b01010000, (byte) 0b01011000, (byte) 0b10010000};
        rst = BCD8421BinaryCodec.encode("8550589");
        System.out.println(ByteUtils.toBitStr(rst));
        Assert.assertEquals(expected[0], rst[0]);
        Assert.assertEquals(expected[1], rst[1]);
        Assert.assertEquals(expected[2], rst[2]);
        Assert.assertEquals(expected[3], rst[3]);
    }

    @Test
    public void encodeQuick() {
        byte[] rst = BCD8421BinaryCodec.encode("55031412782305");
        System.out.println(ByteUtils.toHexStr(rst));
    }

    @Test
    public void decodeQuick() {
        String rst = BCD8421BinaryCodec.decode(new byte[]{0x55, 0x03, 0x14, 0x12, 0x78, 0x23, 0x05});
        System.out.println(rst);
    }

    @Test
    public void encodeWithPadding() {
        byte[] rst;

        byte[] expected = new byte[]{(byte) 0b10000101, (byte) 0b01010000, (byte) 0b01011000, (byte) 0b00000000};
        rst = BCD8421BinaryCodec.encodeWithPadding("855058", 4);
        System.out.println(ByteUtils.toBitStr(rst));
        Assert.assertEquals(expected[0], rst[0]);
        Assert.assertEquals(expected[1], rst[1]);
        Assert.assertEquals(expected[2], rst[2]);
        Assert.assertEquals(expected[3], rst[3]);

        expected = new byte[]{(byte) 0b10000101, (byte) 0b01010000, (byte) 0b01011000, (byte) 0b10010000};
        rst = BCD8421BinaryCodec.encodeWithPadding("8550589", 4);
        System.out.println(ByteUtils.toBitStr(rst));
        Assert.assertEquals(expected[0], rst[0]);
        Assert.assertEquals(expected[1], rst[1]);
        Assert.assertEquals(expected[2], rst[2]);
        Assert.assertEquals(expected[3], rst[3]);

        expected = new byte[]{(byte) 0b10000101, (byte) 0b01010000, (byte) 0b01011000, (byte) 0b10010000, (byte) 0b00000000};
        rst = BCD8421BinaryCodec.encodeWithPadding("8550589", 5);
        System.out.println(ByteUtils.toBitStr(rst));
        Assert.assertEquals(expected[0], rst[0]);
        Assert.assertEquals(expected[1], rst[1]);
        Assert.assertEquals(expected[2], rst[2]);
        Assert.assertEquals(expected[3], rst[3]);
        Assert.assertEquals(expected[4], rst[4]);
    }
}