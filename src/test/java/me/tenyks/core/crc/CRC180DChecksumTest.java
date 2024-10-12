package me.tenyks.core.crc;

import org.junit.Test;

import static org.junit.Assert.*;

public class CRC180DChecksumTest {

    @Test
    public void update() {

        CRC180DChecksum checksum = new CRC180DChecksum();

        byte[] buf = new byte[]{0x68, 0x0C, 0x00, 0x00, 0x00, 0x12, 0x32, 0x01, 0x02, 0x00, 0x00, 0x00, 0x01, 0x01};
        checksum.update(buf, 2, buf.length - 2);
        System.out.printf("%04X%n", (short)checksum.getValue());

        checksum = new CRC180DChecksum();
        buf = new byte[]{0x68, 0x0C, 0x00, 0x00, 0x00, 0x12, 0x32, 0x01, 0x02, 0x00, 0x00, 0x00, 0x01, 0x01};
        checksum.update(buf, 2, buf.length - 2);
        System.out.printf("%04X%n", (short)checksum.getValue());
    }
}