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

    @Test
    public void test2() {
        //这里写入需要生成校验码的字符串
        byte[] buf = new byte[] {0x68, 0x0c, 0x00, 0x00, 0x00, 0x02, 0x55, 0x03, 0x14, 0x12, 0x78, 0x23, 0x05, 0x00};

        CRC180DChecksum crc = new CRC180DChecksum();

        crc.update(buf, 2, buf.length - 2);

        long value = crc.getValue();
        System.out.printf("%04X%n", value);
    }

    @Test
    public void test3() {
        //这里写入需要生成校验码的字符串
        byte[] buf = new byte[]{0x68, (byte) 0x0D, 0x00, 0x01, 0x00, (byte) 0x03, (byte) 0x32, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, 0x00};

        CRC180DChecksum crc = new CRC180DChecksum();

        crc.update(buf, 2, buf.length - 2);

        long value = crc.getValue();
        System.out.printf("%04X%n", value);
    }

}