package org.jetlinks.protocol.official.core;

import org.junit.Assert;
import org.junit.Test;

public class ByteUtilsTest {

    @Test
    public void reverseBits() {
        int val = ByteUtils.reverseBits(0x00000001);
        Assert.assertEquals(0x80000000, val);

        int sVal = 0x00000201;
        val = ByteUtils.reverseBits(sVal);
        Assert.assertEquals(0x80400000, val);

        sVal = 0x04000000;
        val = ByteUtils.reverseBits(sVal);
        Assert.assertEquals(0x00000020, val);

        sVal = 0xF0030201;
        val = ByteUtils.reverseBits(sVal);
        Assert.assertEquals(0x8040C00F, val);

        sVal = 0xa4704541;
        val = ByteUtils.reverseBits(sVal);
        Assert.assertEquals(0x82A20E25, val);
    }
}