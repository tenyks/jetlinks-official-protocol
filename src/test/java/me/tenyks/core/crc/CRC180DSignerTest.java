package me.tenyks.core.crc;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class CRC180DSignerTest {

    @Test
    public void testOne() {
        //这里写入需要生成校验码的字符串
        String str = "HelloWorld";

        CRC180DChecksum crc = new CRC180DChecksum();
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        crc.update(buf, 0, buf.length);

        long value = crc.getValue();
        System.out.printf("%04X%n", value);
    }

}