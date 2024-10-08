package org.jetlinks.protocol.common;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SelfEmbedMessageIdReverseMappingShortTest {

    private SelfEmbedMessageIdReverseMappingShort mapping = new SelfEmbedMessageIdReverseMappingShort("JU_");

    @Test
    public void mark() {
        String rst;

        rst = mapping.mark((short) 0x0000);
        System.out.println(rst);

        rst = mapping.mark((short) 0x0001);
        System.out.println(rst);

        rst = mapping.mark((short) 0xffff);
        System.out.println(rst);
    }

    @Test
    public void take() {

        Short real;

        real = mapping.take("JU__1728398895722_000001_0000");
        Assert.assertEquals((Short)(short)0x0000, real);

        real = mapping.take("JU__1728398895722_000002_0001");
        Assert.assertEquals((Short)(short)0x0001, real);

        real = mapping.take("JU__1728398895722_000003_ffff");
        Assert.assertEquals((Short)(short)0xffff, real);
    }
}